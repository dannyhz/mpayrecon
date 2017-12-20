package citic.hz.mpos.service;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.object.BatchSqlUpdate;
import org.springframework.util.StringUtils;

import citic.hz.mpos.kit.Env;
import citic.hz.mpos.kit.Globle;
import citic.hz.mpos.service.dao.DayReportDao;
import citic.hz.mpos.service.dao.MPosBatDao;
import citic.hz.phio.kit.PhioH;

/**
 * 获取微信、支付宝对账文件
 * 获取mpos对账流水
 * @author phio
 *
 */
public class CmpGetDtlService {
	
	private static final Logger log = Logger.getLogger(CmpGetDtlService.class);
	
	/**
	 * 微信对账单获取有多种方式，但文件内容格式是一致的，这部分独立出来作为公用逻辑
	 * @param br
	 * @param bsu
	 * @param batno
	 * @param trdt
	 * @param mchNo
	 * @throws DataAccessException
	 * @throws IOException
	 */
	private static void readWxFile(BufferedReader br,BatchSqlUpdate bsu,String batno,
			String trdt,String mchNo) throws DataAccessException, IOException{
		//根据微信文件计算的应收微信的交易额，注意：微信应收是扣除手续费后的
		BigDecimal recvable = new BigDecimal(0);
		String ln = null;
		int i = 0;
		while((ln = br.readLine()) != null){
			if(i++ == 0)
				log.debug("read file start...");
			//处理每一行
			if(!ln.startsWith("`"))
				continue;
			String [] fs = ln.split("^`|,`|,\"`|\",`",-1);
			if(fs.length < 25){
				if(fs.length == 6){	//文件尾
					log.info("(总交易单数,总交易额,总退款金额,总企业红包退款金额,手续费总金额) = ("+ln.replaceAll("`", "")+")");
					continue;
				}else{
					log.error("异常行:" + ln);
					throw new RuntimeException("微信文件处理失败");
				}	
			}
			String trtm = fs[1];
			String chlNo = "05";	//TPAM_TYPE	05=微信
			String chlOrdId = fs[6];
			String myOrdId = fs[7];
			String mchNo2 = fs[3];	//文件内容中的商户号
			String secMchNo = fs[4];
			String tram = fs[13];
			String trtp = "01";	//01-正常 04-退款 21-撤销
			String trst = fs[10];
			String relOrdId = "";
			if("REFUND".equals(trst)){
				trtp = "04";
				tram = "-"+fs[17];
				relOrdId = myOrdId;
				chlOrdId = fs[15];
				myOrdId = fs[16];
			}else if("REVOKED".equals(trst)){
				trtp = "21";
				tram = "-"+fs[17];
				relOrdId = myOrdId;
				chlOrdId = fs[15];
				myOrdId = fs[16];
			}
			String memo = fs[21];
			String cldt = trtm.substring(0,10);	//YYYY-MM-DD
			String paybk = fs[11];
			
			//汇总计算应收金额
			String costRateStr = fs[24].trim();
			BigDecimal costRate = null;
			if(costRateStr.endsWith("%"))
				costRate = new BigDecimal(costRateStr.replaceAll("%", "")).movePointLeft(2);
			else
				costRate = new BigDecimal(costRateStr);
			BigDecimal tramt = new BigDecimal(tram);
			recvable = recvable.add(tramt.subtract(tramt.multiply(costRate).setScale(2, RoundingMode.HALF_UP)));
			
			if(myOrdId.startsWith("9") && Env.ifProduction()){	//9开头的为测试订单
				log.debug("test order:"+Arrays.asList(myOrdId,trtp,tram,secMchNo,relOrdId));
				continue;
			}
			//撤销交易没有撤销订单号的情况，只有原订单号的情况
			if(StringUtils.hasText(relOrdId) && relOrdId.startsWith("9") && Env.ifProduction()){
				log.debug("test order:"+Arrays.asList(myOrdId,trtp,tram,secMchNo,relOrdId));
				continue;
			}
			if(!Env.ifProduction() && myOrdId.startsWith("8"))	//8开头的为生产订单（用于测试）
				continue;
			
			bsu.update(trtm,chlNo,chlOrdId,myOrdId,mchNo2,secMchNo,tram,trtp,relOrdId,memo,cldt,batno,paybk);
		}
		
		if(Env.needRewrite){
			//记录每个主商户的应收
			DayReportDao.addK2V(trdt,"WX_FILE_RECVABLE_SUM",mchNo,recvable.toString(),"根据微信文件计算的应收额(已扣手续费)");
		}
	}

	/**
	 * 微信冻结单的文件读取
	 * @param br
	 * @param bsu
	 * @param batno
	 * @param trdt
	 * @param mchNo
	 * @throws DataAccessException
	 * @throws IOException
	 */
	private static void readWxFrzFile(BufferedReader br,BatchSqlUpdate bsu,String batno,
			String trdt,String mchNo) throws DataAccessException, IOException{
		String ln = null;
		int i = 0;
		while((ln = br.readLine()) != null){
			if(i++ == 0)
				log.debug("read file start...");
			//处理每一行
			//startsWith时有神秘的3f字节影响判断
			if(ln.endsWith("冻结状态") || ln.trim().endsWith("解冻记录订单总金额（元）"))
				continue;
			String [] fs = ln.split(",",-1);
			if(fs.length != 10){
				if(fs.length == 5){	//文件尾
					log.info("(记录总条数，冻结记录总条数，冻结记录订单总金额（元），解冻记录总条数，解冻记录订单总金额（元）) = ("+ln+")");
					continue;
				}else{
					log.error("异常行:" + ln);
					throw new RuntimeException("微信冻结文件处理失败");
				}	
			}
			String trtm = fs[0];
			String chlNo = "05";	//TPAM_TYPE	05=微信
			String chlOrdId = fs[3];
			String myOrdId = fs[4];
			String mchNo2 = fs[1];	//文件内容中的商户号
			String secMchNo = fs[2];
			String tram = fs[5];
			String memo = fs[6];
			String frzFlag = fs[9];
			if("FREEZE".equals(frzFlag))
				frzFlag = "F";
			else if("UNFREEZE".equals(frzFlag))
				frzFlag = "U";
			else
				throw new RuntimeException("未知的冻结解冻标记"+frzFlag);
			String frzdt = fs[7];
			if(StringUtils.hasText(frzdt))
				frzdt = PhioH.cvtDateTime(frzdt, "yyyy-MM-dd", "yyyyMMdd");
			else
				frzdt = null;
			String unfrzdt = fs[8];
			if(StringUtils.hasText(unfrzdt))
				unfrzdt = PhioH.cvtDateTime(unfrzdt, "yyyy-MM-dd", "yyyyMMdd");
			else
				unfrzdt = null;
			
			if(myOrdId.startsWith("9") && Env.ifProduction()){	//9开头的为测试订单
				log.debug("test order:"+Arrays.asList(myOrdId,tram,secMchNo));
				continue;
			}
			if(!Env.ifProduction() && myOrdId.startsWith("8"))	//8开头的为生产订单（用于测试）
				continue;
			
			bsu.update(trtm,chlNo,chlOrdId,myOrdId,mchNo2,secMchNo,tram,frzFlag,frzdt,unfrzdt,memo,batno,"WX");
		}

	}
	
	
	/**
	 * 按主商户号获取微信对账文件
	 * @param trdt
	 */
	public static void getWxFiles(String trdt){
		//一天只做一次
		JSONObject rt = CheckLimitService.checkLimit("MPOS_BAT_LOAD_WXFILE", trdt, 1);	
		if(rt.getInt("respcd") != 0)
			return;
		//批次号，方便异常时清除数据
		String batno = PhioH.newKey();
		//获取微信主商户号
		List<Map<String, Object>> wxmchs = MPosBatDao.lstWxMchs();
		BatchSqlUpdate bsu = MPosBatDao.batWxTrans();
		BufferedReader br = null;
		FTPClient ftpc = new FTPClient();
		try {
			//按主商户号获取文件
			String wxFileFtpIp = Globle.config.get("wxFileFtpIp");	//TODO
			String wxFileFtpUser = Globle.config.get("wxFileFtpUser");	//TODO
			String wxFileFtpPass = Globle.config.get("wxFileFtpPass");	//TODO
			for(Map<String, Object> wxmchMap : wxmchs){
				String mchNo = (String)wxmchMap.get("MAIN_MCHT_NO");
				log.info("开始处理"+mchNo+"文件..."+batno);
				ftpc.connect(wxFileFtpIp);
				ftpc.login(wxFileFtpUser, wxFileFtpPass);
				//ftpc.setFileTransferMode(FTPClient.BINARY_FILE_TYPE);
				ftpc.setFileType(FTP.BINARY_FILE_TYPE);
				ftpc.changeWorkingDirectory("/in/wx/"+trdt);
				InputStream is = ftpc.retrieveFileStream(trdt+"-"+mchNo+".csv");
				br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
				readWxFile(br,bsu,batno,trdt,mchNo);
				br.close();
				ftpc.disconnect();
			}
			WxNotifyService.offerMsg("微信文件处理完成");
		} catch (IOException e) {
			log.error("微信文件处理失败",e);
			WxNotifyService.offerMsg("微信文件处理失败:"+e.getMessage());
			throw new RuntimeException("微信文件处理失败",e);
		}finally {
			if(null != br)
				try {br.close();} catch (IOException ignore) {}
			bsu.flush();
			if(ftpc.isConnected())
				try {ftpc.disconnect();} catch (IOException ignore) {}
		}
	}
	
	
	/**
	 * 按主商户号获取微信对账文件(gzip版)
	 * @param trdt
	 */
	public static void getWxGzFiles(String trdt){
		//一天只做一次
		JSONObject rt = CheckLimitService.checkLimit("MPOS_BAT_LOAD_WXFILE", trdt, 1);	
		if(rt.getInt("respcd") != 0)
			return;
		//批次号，方便异常时清除数据
		String batno = PhioH.newKey();
		//获取微信主商户号
		List<Map<String, Object>> wxmchs = MPosBatDao.lstWxMchs();
		BatchSqlUpdate bsu = MPosBatDao.batWxTrans();
		BufferedReader br = null;
		FTPClient ftpc = new FTPClient();
		try {
			//按主商户号获取文件
			String wxFileFtpIp = Globle.config.get("wxFileFtpIp");	//TODO
			String wxFileFtpUser = Globle.config.get("wxFileFtpUser");	//TODO
			String wxFileFtpPass = Globle.config.get("wxFileFtpPass");	//TODO
			for(Map<String, Object> wxmchMap : wxmchs){
				String mchNo = (String)wxmchMap.get("MAIN_MCHT_NO");
				log.info("开始处理"+mchNo+"文件..."+batno);
				ftpc.connect(wxFileFtpIp);
				ftpc.login(wxFileFtpUser, wxFileFtpPass);
				//ftpc.setFileTransferMode(FTPClient.BINARY_FILE_TYPE);
				ftpc.setFileType(FTP.BINARY_FILE_TYPE);
				ftpc.changeWorkingDirectory("/in/wx/"+trdt);
				
				//gzip stream wrapper
				InputStream is = ftpc.retrieveFileStream(trdt+"-"+mchNo+".csv.gz");
				
				if(is == null){
					log.error("未找到渠道主商户号："+mchNo+" , "+trdt+" 的对账文件 ["+ trdt+"-"+mchNo+".csv.gz" +"]");
					throw new IOException("未找到渠道主商户号："+mchNo+" , "+trdt+" 的对账文件 ["+ trdt+"-"+mchNo+".csv.gz" +"]");
				}
				
				GZIPInputStream gis = null;
				try {
					gis = new GZIPInputStream(is);
				} catch (ZipException e) {
					//格式错误很可能是对账单无数据造成的（微信返回一个XML报文），此时通过查询我方数据，进行大致判断（由于是对账前数据，有可能出现误判）
					log.warn(mchNo+" file maybe empty:" + e.getMessage());
					int cnt = MPosBatDao.countMchOrders(mchNo);
					log.info("countMchOrders "+mchNo+":"+cnt);
					//对可能出现无交易的主商户建立了一个白名单
					String maybeEmptyMchNos = Globle.config.get("maybeEmptyMchNos"); 
					if(maybeEmptyMchNos.indexOf(mchNo)>=0  &&  cnt==0){
						log.info("mch order !!MAYBE!! empty, ignore it");
						WxNotifyService.offerMsg("微信文件"+mchNo+" maybe empty");
						is.close();
						ftpc.disconnect();
						continue;
					}
				}
				br = new BufferedReader(new InputStreamReader(gis,"UTF-8"));

				readWxFile(br,bsu,batno,trdt,mchNo);
				br.close();
				ftpc.disconnect();
			}
			WxNotifyService.offerMsg("微信文件处理完成");
		} catch (IOException e) {
			log.error("微信文件处理失败",e);
			WxNotifyService.offerMsg("微信文件处理失败:"+e.getMessage());
			throw new RuntimeException("微信文件处理失败",e);
		}finally {
			if(null != br)
				try {br.close();} catch (IOException ignore) {}
			bsu.flush();
			if(ftpc.isConnected())
				try {ftpc.disconnect();} catch (IOException ignore) {}
		}
	}

	/**
	 * 按主商户号获取微信对账文件(zip版)
	 * @param trdt
	 */
	public static void getWxZipFiles(String trdt){
		//一天只做一次
		JSONObject rt = CheckLimitService.checkLimit("MPOS_BAT_LOAD_WXFILE", trdt, 1);	
		if(rt.getInt("respcd") != 0)
			return;
		//批次号，方便异常时清除数据
		String batno = PhioH.newKey();
		//zip方式的文件名中日期格式
		String trdt10 = PhioH.cvtDateTime(trdt, "yyyyMMdd", "yyyy-MM-dd");
		
		//获取微信主商户号
		List<Map<String, Object>> wxmchs = MPosBatDao.lstWxMchs();
		BatchSqlUpdate bsu = MPosBatDao.batWxTrans();
		BufferedReader br = null;
		FTPClient ftpc = new FTPClient();
		try {
			//按主商户号获取文件
			String wxFileFtpIp = Globle.config.get("wxFileFtpIp");	//TODO
			String wxFileFtpUser = Globle.config.get("wxFileFtpUser");	//TODO
			String wxFileFtpPass = Globle.config.get("wxFileFtpPass");	//TODO
			for(Map<String, Object> wxmchMap : wxmchs){
				String mchNo = (String)wxmchMap.get("MAIN_MCHT_NO");
				log.info("开始处理"+mchNo+"文件..."+batno);
				ftpc.connect(wxFileFtpIp);
				ftpc.login(wxFileFtpUser, wxFileFtpPass);
				ftpc.setFileType(FTP.BINARY_FILE_TYPE);
				ftpc.changeWorkingDirectory("/in/wx/"+trdt);
				InputStream is = ftpc.retrieveFileStream(mchNo+"-All-"+trdt10+".zip");
				
				if(is == null){
					log.error("未找到渠道主商户号："+mchNo+" , "+trdt+" 的对账文件 ["+ mchNo+"-All-"+trdt10+".zip" +"]");
					throw new IOException("未找到渠道主商户号："+mchNo+" , "+trdt+" 的对账文件 ["+ mchNo+"-All-"+trdt10+".zip" +"]");
				}
				
				ZipInputStream zis = new ZipInputStream(is,Charset.forName("UTF-8"));
				zis.getNextEntry();
				br = new BufferedReader(new InputStreamReader(zis,"UTF-8"));
				readWxFile(br,bsu,batno,trdt,mchNo);
				br.close();
				ftpc.disconnect();
			}
			WxNotifyService.offerMsg("微信文件处理完成");
		} catch (IOException e) {
			log.error("微信文件处理失败",e);
			WxNotifyService.offerMsg("微信文件处理失败:"+e.getMessage());
			throw new RuntimeException("微信文件处理失败",e);
		}finally {
			if(null != br)
				try {br.close();} catch (IOException ignore) {}
			bsu.flush();
			if(ftpc.isConnected())
				try {ftpc.disconnect();} catch (IOException ignore) {}
		}
	}
	
	/**
	 * 按主商户号获取支付宝对账文件
	 * @param trdt //TODO
	 */
	public static void getZfbFiles(String trdt){
		JSONObject rt = CheckLimitService.checkLimit("MPOS_BAT_LOAD_ZFBFILE", trdt, 1);	//一天只做一次
		if(rt.getInt("respcd") != 0)
			return;

		//批次号，方便异常时清除数据
		String batno = PhioH.newKey();
		//获取支付宝主商户号
		List<Map<String, Object>> zfbmchs = MPosBatDao.lstZfbMchs();
		//复用微信的流水sql
		BatchSqlUpdate bsu = MPosBatDao.batWxTrans();
		BufferedReader br = null;
		FTPClient ftpc = new FTPClient();
		try {
			//按主商户号获取文件
			String wxFileFtpIp = Globle.config.get("wxFileFtpIp");	//TODO
			String wxFileFtpUser = Globle.config.get("wxFileFtpUser");	//TODO
			String wxFileFtpPass = Globle.config.get("wxFileFtpPass");	//TODO
			for(Map<String, Object> zfbmchMap : zfbmchs){
				String mchNo = (String)zfbmchMap.get("MAIN_MCHT_NO");
				log.info("开始处理"+mchNo+"文件..."+batno);
				ftpc.connect(wxFileFtpIp);
				ftpc.login(wxFileFtpUser, wxFileFtpPass);
				ftpc.setFileType(FTP.BINARY_FILE_TYPE);
				ftpc.changeWorkingDirectory("/in/zfbeq/"+trdt);
				InputStream is = ftpc.retrieveFileStream(mchNo+"0156_"+trdt+".csv.zip");
				
				if(is == null){
					log.error("未找到渠道主商户号："+mchNo+" , "+trdt+" 的对账文件 ["+ mchNo+"0156_"+trdt+".csv.zip" +"]");
					throw new IOException("未找到渠道主商户号："+mchNo+" , "+trdt+" 的对账文件 ["+ mchNo+"0156_"+trdt+".csv.zip" +"]");
				}
				
				ZipInputStream zis = new ZipInputStream(is,Charset.forName("GBK"));
				ZipEntry zipfile = null;
				while((zipfile = zis.getNextEntry())!=null){
					if(zipfile.getName().endsWith("DETAILS.csv"))	//只取明细文件
						break;	//定位到明细文件就退出循环
				}
				br = new BufferedReader(new InputStreamReader(zis,"GBK"));
				//根据文件计算的应收交易额，注意：支付宝是后返模式，应收不用扣除手续费
				BigDecimal recvable = new BigDecimal(0);
				String ln = null;
				while((ln = br.readLine()) != null){
					//处理每一行
					if(ln.startsWith("#交易合计") || ln.startsWith("#退款合计")){	//合计信息打印一下
						log.info(ln);
					}
					if(!ln.startsWith("20"))	//过滤文件头尾
						continue;
					String [] fs = ln.split(",",-1);
					if(fs.length != 27){
						log.error("异常行:" + ln);
						throw new RuntimeException("支付宝文件处理失败");
					}
					String trtm = fs[5];
					String chlNo = "10";	//TPAM_TYPE	10=支付宝二清
					String chlOrdId = fs[0].trim();
					String myOrdId = fs[1].trim();
					String secMchNo = fs[24].trim();
					String tram = fs[12];	//TODO 待确认
					String trtp = "01";	//01-正常 04-退款 21-撤销
					String trst = fs[2].trim();
					String relOrdId = "";
					if("退款".equals(trst)){
						trtp = "04";
						relOrdId = myOrdId;
						chlOrdId = chlOrdId + "RV";	//支付宝退款类似微信的撤销，支付宝流水号和正交易是一样的，加上RV以示区别	
						myOrdId = fs[21].trim();
					}
					String memo = fs[26].trim();
					String cldt = trtm.substring(0,10);	//YYYY-MM-DD
					
					//汇总应收
					recvable = recvable.add(new BigDecimal(tram));
					
					if(myOrdId.startsWith("9") && Env.ifProduction()){	//9开头的为测试订单
						log.debug("test order:"+Arrays.asList(myOrdId,trtp,tram,secMchNo,relOrdId));
						continue;
					}
					if(!Env.ifProduction() && myOrdId.startsWith("8"))	//8开头的为生产订单（用于测试）
						continue;
					
					bsu.update(trtm,chlNo,chlOrdId,myOrdId,mchNo,secMchNo,tram,trtp,relOrdId,memo,cldt,batno,"");
				}
				zis.close();
				br.close();
				ftpc.disconnect();

				if(Env.needRewrite){
					//记录每个主商户的应收
					DayReportDao.addK2V(trdt,"ZFB_FILE_RECVABLE_SUM",mchNo,recvable.toString(),"根据支付宝文件计算的应收额");
				}
			}
			WxNotifyService.offerMsg("支付宝文件处理完成");
		} catch (IOException e) {
			log.error("支付宝文件处理失败",e);
			WxNotifyService.offerMsg("支付宝文件处理失败:"+e.getMessage());
			throw new RuntimeException("支付宝文件处理失败",e);
		}finally {
			if(null != br)
				try {br.close();} catch (IOException ignore) {}
			bsu.flush();
			if(ftpc.isConnected())
				try {ftpc.disconnect();} catch (IOException ignore) {}
		}
	}


	/**
	 * 读取百付宝消费对账文件
	 * @param br
	 * @param bsu
	 * @param batno
	 * @param trdt
	 * @param mchNo
	 * @param recvable
	 * @throws DataAccessException
	 * @throws IOException
	 */
	private static void readBfbXfFile(BufferedReader br,BatchSqlUpdate bsu,String batno,
			String trdt,String mchNo,final List<BigDecimal> recvableBox) throws DataAccessException, IOException{
		String ln = null;
		int i = 0;
		BigDecimal recvable = recvableBox.get(0);
		while((ln = br.readLine()) != null){
			if(i++ == 0)
				log.debug("read file start...");
			//去除文件头
			if(ln.startsWith("商户"))
				continue;
			//处理每一行
			String [] fs = ln.split(" ",-1);
			if(fs.length != 8){
				log.error("异常行:" + ln);
				throw new RuntimeException("百付宝XF文件处理失败");
			}
			String trtm = PhioH.cvtDateTime(fs[3], "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss");	//百付宝支付日期
			String chlNo = "20";	//TPAM_TYPE	20=百付宝
			String chlOrdId = fs[1];
			String myOrdId = fs[0];
			String tram = new BigDecimal(fs[5]).movePointLeft(2).toString();	//转换金额格式 1->0.01
			String trtp = "01";	//01-正常
			String memo = fs[6];
			String cldt = trtm.substring(0,10);	//YYYY-MM-DD
			
			//汇总应收
			recvable = recvable.add(new BigDecimal(tram));
			
			if(myOrdId.startsWith("9") && Env.ifProduction()){	//9开头的为测试订单
				log.debug("test order:"+Arrays.asList(myOrdId,trtp,tram));
				continue;
			}
			if(!Env.ifProduction() && myOrdId.startsWith("8"))	//8开头的为生产订单（用于测试）
				continue;
			
			bsu.update(trtm,chlNo,chlOrdId,myOrdId,mchNo,"",tram,trtp,"",memo,cldt,batno,"");
		}
		recvableBox.set(0, recvable);
	}

	/**
	 * 读取百付宝退款对账文件
	 * @param br
	 * @param bsu
	 * @param batno
	 * @param trdt
	 * @param mchNo
	 * @param recvable
	 * @throws DataAccessException
	 * @throws IOException
	 */
	private static void readBfbTkFile(BufferedReader br,BatchSqlUpdate bsu,String batno,
			String trdt,String mchNo,final List<BigDecimal> recvableBox) throws DataAccessException, IOException{
		String ln = null;
		int i = 0;
		BigDecimal recvable = recvableBox.get(0);
		while((ln = br.readLine()) != null){
			if(i++ == 0)
				log.debug("read file start...");
			//去除文件头
			if(ln.startsWith("商户") || ln.startsWith("原商户"))
				continue;
			//处理每一行
			String [] fs = ln.split(" ",-1);
			if(fs.length != 8){	//实际字段是7个，但是百度有个bug，退款金额、付款方之间是两个空格，造成字段变成了8
				log.error("异常行:" + ln);
				throw new RuntimeException("百付宝TK文件处理失败");
			}
			String trtm = PhioH.cvtDateTime(fs[3], "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss");	//百付宝退款时间
			String chlNo = "20";	//TPAM_TYPE	20=百付宝
			String chlOrdId = fs[1]+"RV";	//百度没有退款的渠道方流水号，根据原流水号确定，//FIXME 如果有部分退款，不能这样做
			String myOrdId = fs[2].split("J",2)[1];	//百度的退款流水号是 <商户号>J<退款流水号>的格式，需要转换
			String tram = new BigDecimal(fs[5]).movePointLeft(2).negate().toString();	//转换金额格式 1->-0.01
			String trtp = "04";	//04-退款
			String cldt = trtm.substring(0,10);	//YYYY-MM-DD
			String relOrdId = fs[0];
			
			//汇总应收
			recvable = recvable.add(new BigDecimal(tram));
			
			if(myOrdId.startsWith("9") && Env.ifProduction()){	//9开头的为测试订单
				log.debug("test order:"+Arrays.asList(myOrdId,trtp,tram));
				continue;
			}
			if(!Env.ifProduction() && myOrdId.startsWith("8"))	//8开头的为生产订单（用于测试）
				continue;
			
			bsu.update(trtm,chlNo,chlOrdId,myOrdId,mchNo,"",tram,trtp,relOrdId,"",cldt,batno,"");
		}
		recvableBox.set(0, recvable);
	}

	/**
	 * 按主商户号获取百付宝对账文件
	 * @param trdt 
	 */
	public static void getBfbFiles(String trdt){
		JSONObject rt = CheckLimitService.checkLimit("MPOS_BAT_LOAD_BFBFILE", trdt, 1);	//一天只做一次
		if(rt.getInt("respcd") != 0)
			return;

		//批次号，方便异常时清除数据
		String batno = PhioH.newKey();
		//获取百付宝主商户号
		List<Map<String, Object>> bfbmchs = MPosBatDao.lstBfbMchs();
		//复用微信的流水sql
		BatchSqlUpdate bsu = MPosBatDao.batWxTrans();
		BufferedReader br = null;
		FTPClient ftpc = new FTPClient();
		try {
			//按主商户号获取文件
			String wxFileFtpIp = Globle.config.get("wxFileFtpIp");	//TODO
			String wxFileFtpUser = Globle.config.get("wxFileFtpUser");	//TODO
			String wxFileFtpPass = Globle.config.get("wxFileFtpPass");	//TODO
			for(Map<String, Object> bfbmchMap : bfbmchs){
				String mchNo = (String)bfbmchMap.get("MAIN_MCHT_NO");
				log.info("开始处理"+mchNo+"文件..."+batno);
				ftpc.connect(wxFileFtpIp);
				ftpc.login(wxFileFtpUser, wxFileFtpPass);
				ftpc.setFileType(FTP.BINARY_FILE_TYPE);
				ftpc.changeWorkingDirectory("/in/bfb/"+trdt);

				//根据百度宝文件计算的应收百度的交易额，注意：百度是后返模式，应收不用扣除手续费
				List<BigDecimal> recvableBox = new ArrayList<BigDecimal>(1);
				recvableBox.add(new BigDecimal(0));
				
				//先处理消费文件
				InputStream is = ftpc.retrieveFileStream(mchNo+"_xf_"+trdt+".txt");
				br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
				readBfbXfFile(br, bsu, batno, trdt, mchNo, recvableBox);
				is.close();
				br.close();
				ftpc.completePendingCommand();	//close first retrieveFileStream
				//再处理退款文件
				is = ftpc.retrieveFileStream(mchNo+"_tk_"+trdt+".txt");
				
				if(is == null){
					log.error("未找到渠道主商户号："+mchNo+" , "+trdt+" 的对账文件 ["+ mchNo+"_tk_"+trdt+".txt" +"]");
					throw new IOException("未找到渠道主商户号："+mchNo+" , "+trdt+" 的对账文件 ["+ mchNo+"_tk_"+trdt+".txt" +"]");
				}
				br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
				readBfbTkFile(br, bsu, batno, trdt, mchNo, recvableBox);
				is.close();
				br.close();
				ftpc.disconnect();

				if(Env.needRewrite){
					//记录每个主商户的应收
					DayReportDao.addK2V(trdt,"BFB_FILE_RECVABLE_SUM",mchNo,recvableBox.get(0).toString(),"根据百付宝文件计算的应收额");
				}
			}
			WxNotifyService.offerMsg("百付宝文件处理完成");
		} catch (IOException e) {
			log.error("百付宝文件处理失败",e);
			WxNotifyService.offerMsg("百付宝文件处理失败:"+e.getMessage());
			throw new RuntimeException("百付宝文件处理失败",e);
		}finally {
			if(null != br)
				try {br.close();} catch (IOException ignore) {}
			bsu.flush();
			if(ftpc.isConnected())
				try {ftpc.disconnect();} catch (IOException ignore) {}
		}
	}
	
	
	
	/**
	 * 导入mpos的订单信息
	 * @param trdt
	 */
	public static synchronized void loadMposDtl(String trdt){
		//先测试邦联连接
		CmpService.testFredLink();

		JSONObject rt = CheckLimitService.checkLimit("MPOS_BAT_LOADMPOSDTL", trdt, 1);	//一天只做一次
		if(rt.getInt("respcd") != 0)
			return;
		//批次号，方便异常时清除数据
		String batno = PhioH.newKey();
		log.info("开始导入MPOS流水 "+trdt+"|"+batno);
		int cnt = MPosBatDao.loadMposDtl(trdt,batno);
		log.info("导入MPOS流水完成 "+cnt);
		WxNotifyService.offerMsg("导入MPOS流水完成 "+cnt);
	}

	/**
	 * 导入mpos的订单信息（非成功的数据，包含疑似掉单的数据）
	 * @param trdt
	 */
	public static synchronized void loadMposDtlFail(String trdt){
		//先测试邦联连接
		CmpService.testFredLink();

		JSONObject rt = CheckLimitService.checkLimit("MPOS_BAT_LOADMPOSDTL_FAIL", trdt, 1);	//一天只做一次
		if(rt.getInt("respcd") != 0)
			return;
		//批次号，方便异常时清除数据
		String batno = PhioH.newKey();
		log.info("开始导入MPOS非成功流水 "+trdt+"|"+batno);
		int cnt = MPosBatDao.loadMposDtlFail(trdt,batno);
		log.info("导入MPOS非成功流水完成 "+cnt);
		WxNotifyService.offerMsg("导入MPOS非成功流水完成 "+cnt);
	}

	/**
	 * 导入相关的配置表
	 */
	public static synchronized void loadConfig(){
		//先测试邦联连接
		CmpService.testFredLink();
		
		String nowdt = PhioH.nowDate("yyyyMMdd");
		JSONObject rt = CheckLimitService.checkLimit("MPOS_BAT_LOADCONFIG", nowdt, 1);	//一天只做一次
		if(rt.getInt("respcd") != 0)
			return;
		log.info("开始导入IMP_MAIN_MCHT_INFO");
		MPosBatDao.loadMposTableReplace("NK_IMP_MAIN_MCHT_INFO", "IMP_MAIN_MCHT_INFO");
		log.info("开始导入IMP_SECD_FEE_INFO");
		MPosBatDao.loadMposTableReplace("NK_IMP_SECD_FEE_INFO", "IMP_SECD_FEE_INFO");
		log.info("开始导入IMP_SECD_SUB_INFO");
		MPosBatDao.loadMposTableReplace("NK_IMP_SECD_SUB_INFO", "IMP_SECD_SUB_INFO");
		log.info("开始导入IMP_SECD_PROFIT_RATE");
		MPosBatDao.loadProfitRate();
		log.info("开始导入IMP_SPLIT_SUB_MCHT");
		MPosBatDao.loadMposTableReplace("NK_IMP_SPLIT_SUB_MCHT", "IMP_SPLIT_SUB_MCHT");
		log.info("开始导入IMP_MCHT_BLACKLIST");
		MPosBatDao.loadMposTableReplace("NK_IMP_MCHT_BLACKLIST", "IMP_MCHT_BLACKLIST");
		log.info("开始导入IMP_ALONE_SUB_INFO");
		MPosBatDao.loadMposTableReplace("NK_IMP_ALONE_SUB_INFO", "IMP_ALONE_SUB_INFO");
		log.info("开始导入IMP_ALONE_CHANNEL_INFO");
		MPosBatDao.loadMposTableReplace("NK_IMP_ALONE_CHANNEL_INFO", "IMP_ALONE_CHANNEL_INFO");
		log.info("开始导入IMP_PLAT_FEE_INFO");
		MPosBatDao.loadMposTableReplace("NK_IMP_PLAT_FEE_INFO", "IMP_PLAT_FEE_INFO");
		log.info("导入配置表完成");
	}
	
	/**
	 * 用于手工读取微信文件，进行debug
	 * @param trdt
	 * @param mchNo
	 */
	public static void getWxZipFileDebug(String trdt,String mchNo){
		//批次号，方便异常时清除数据
		String batno = PhioH.newKey();
		//zip方式的文件名中日期格式
		String trdt10 = PhioH.cvtDateTime(trdt, "yyyyMMdd", "yyyy-MM-dd");
		
		BatchSqlUpdate bsu = MPosBatDao.batWxTransDebug();
		
		BufferedReader br = null;
		FTPClient ftpc = new FTPClient();
		try {
			//按主商户号获取文件
			String wxFileFtpIp = Globle.config.get("wxFileFtpIp");	//TODO
			String wxFileFtpUser = Globle.config.get("wxFileFtpUser");	//TODO
			String wxFileFtpPass = Globle.config.get("wxFileFtpPass");	//TODO
			log.info("DEBUG-开始处理"+mchNo+"文件..."+batno);
			ftpc.connect(wxFileFtpIp);
			ftpc.login(wxFileFtpUser, wxFileFtpPass);
			ftpc.setFileType(FTP.BINARY_FILE_TYPE);
			ftpc.changeWorkingDirectory("/in/wx/"+trdt);
			InputStream is = ftpc.retrieveFileStream(mchNo+"-All-"+trdt10+".zip");
			
			if(is == null){
				log.error("未找到渠道主商户号："+mchNo+" , "+trdt+" 的对账文件");
				throw new IOException("未找到渠道主商户号："+mchNo+" , "+trdt+" 的对账文件 ["+ mchNo+"-All-"+trdt10+".zip" +"]");
			}
			
			ZipInputStream zis = new ZipInputStream(is,Charset.forName("UTF-8"));
			zis.getNextEntry();
			br = new BufferedReader(new InputStreamReader(zis,"UTF-8"));
			readWxFile(br,bsu,batno,trdt,mchNo);
			br.close();
			ftpc.disconnect();
		} catch (IOException e) {
			log.error("DEBUG-微信文件处理失败",e);
			throw new RuntimeException("DEBUG-微信文件处理失败",e);
		}finally {
			if(null != br)
				try {br.close();} catch (IOException ignore) {}
			bsu.flush();
			if(ftpc.isConnected())
				try {ftpc.disconnect();} catch (IOException ignore) {}
		}
	}
	
	/**
	 * 按主商户号获取微信冻结文件(gzip版)
	 * @param trdt
	 */
	public static void getWxFrzGzFiles(String trdt,String batno){
		//一天只做一次
		JSONObject rt = CheckLimitService.checkLimit("MPOS_BAT_LOAD_WX_FRZ_FILE", trdt, 1);	
		if(rt.getInt("respcd") != 0)
			return;
		//获取微信主商户号
		List<Map<String, Object>> wxmchs = MPosBatDao.lstWxMchs();
		BatchSqlUpdate bsu = MPosBatDao.batWxFrzs();
		BufferedReader br = null;
		FTPClient ftpc = new FTPClient();
		try {
			//按主商户号获取文件
			String wxFileFtpIp = Globle.config.get("wxFileFtpIp");	//TODO
			String wxFileFtpUser = Globle.config.get("wxFileFtpUser");	//TODO
			String wxFileFtpPass = Globle.config.get("wxFileFtpPass");	//TODO
			for(Map<String, Object> wxmchMap : wxmchs){
				String mchNo = (String)wxmchMap.get("MAIN_MCHT_NO");
				log.info("开始处理"+mchNo+"冻结文件..."+batno);
				ftpc.connect(wxFileFtpIp);
				ftpc.login(wxFileFtpUser, wxFileFtpPass);
				ftpc.setFileType(FTP.BINARY_FILE_TYPE);
				ftpc.changeWorkingDirectory("/in/wx/"+trdt);
				
				//gzip stream wrapper
				InputStream is = ftpc.retrieveFileStream(trdt+"-"+mchNo+"_frozen.csv.gz");
				
				if(is == null){
					log.error("未找到渠道主商户号："+mchNo+" , "+trdt+" 的对账文件");
					throw new IOException("未找到渠道主商户号："+mchNo+" , "+trdt+" 的对账文件 ["+ trdt+"-"+mchNo+"_frozen.csv.gz" +"]");
				}
				
				GZIPInputStream gis = null;
				gis = new GZIPInputStream(is);
				br = new BufferedReader(new InputStreamReader(gis,"UTF-8"));

				readWxFrzFile(br,bsu,batno,trdt,mchNo);
				br.close();
				ftpc.disconnect();
			}
			WxNotifyService.offerMsg("微信冻结文件处理完成");
		} catch (IOException e) {
			log.error("微信冻结文件处理失败",e);
			WxNotifyService.offerMsg("微信冻结文件处理失败:"+e.getMessage());
			throw new RuntimeException("微信冻结文件处理失败",e);
		}finally {
			if(null != br)
				try {br.close();} catch (IOException ignore) {}
			bsu.flush();
			if(ftpc.isConnected())
				try {ftpc.disconnect();} catch (IOException ignore) {}
		}
	}
	
	/**
	 * 增加该步骤，将多余的撤销流水号改写
	 */
	public static synchronized void ovRevokeNo(){
		log.info("开始处理多余的撤销流水号");
		int cnt = MPosBatDao.ovRevokeNo();
		if(cnt > 0){
			WxNotifyService.offerCheckMsg("处理了多余的撤销流水号条数："+cnt);
		}
	}

	/**
	 * QQ对账单文件内容读取
	 * @param br
	 * @param bsu
	 * @param batno
	 * @param trdt
	 * @param mchNo
	 * @throws DataAccessException
	 * @throws IOException
	 */
	private static void readQQFile(BufferedReader br,BatchSqlUpdate bsu,String batno,
			String trdt,String mchNo) throws DataAccessException, IOException{
		//根据QQ文件计算的应收QQ的交易额，注意：QQ应收是扣除手续费后的
		BigDecimal recvable = new BigDecimal(0);
		String ln = null;
		int i = 0;
		while((ln = br.readLine()) != null){
			if(i++ == 0)
				log.debug("read file start...");
			//处理每一行
			if(!ln.startsWith("`"))
				continue;
			String [] fs = ln.split("^`|,`|,\"`|\",`",-1);
			if(fs.length < 30+1){
				if(fs.length == 9+1){	//文件尾
					log.info("(交易总笔数,订单总金额(元),商户优惠总金额(元),商户应收总金额(元),QQ钱包优惠总金额(元),用户支付总金额(元),退款总金额(元),退还QQ钱包优惠总金额(元),手续费总金额(元)) = ("+ln.replaceAll("`", "")+")");
					continue;
				}else{
					log.error("异常行:" + ln);
					throw new RuntimeException("QQ文件处理失败");
				}	
			}
			String trtm = fs[1];
			String chlNo = "30";	//TPAM_TYPE	30=QQ
			String chlOrdId = fs[10];
			String myOrdId = fs[9];
			String mchNo2 = fs[2];	//文件内容中的商户号
			String secMchNo = fs[4];
			String tram = fs[15];	//取了商户应收金额(元)，未取订单金额(元)
			String trtp = "01";	//01-正常 04-退款
			String trst = fs[18];
			String relOrdId = "";
			if("转入退款".equals(trst)){
				trtp = "04";
				tram = "-"+fs[22];
				relOrdId = myOrdId;
				chlOrdId = fs[21];
				myOrdId = fs[20];
			}
			String memo = fs[27];
			String cldt = trtm.substring(0,10);	//YYYY-MM-DD
			String paybk = fs[11];	//对账单中支付银行是中文的，暂时没有用处
			
			//汇总计算应收金额
			String costRateStr = fs[30].trim();
			BigDecimal costRate = null;
			if(costRateStr.endsWith("%"))	//TODO 需确定格式
				costRate = new BigDecimal(costRateStr.replaceAll("%", "")).movePointLeft(2);
			else
				costRate = new BigDecimal(costRateStr);
			BigDecimal tramt = new BigDecimal(tram);
			recvable = recvable.add(tramt.subtract(tramt.multiply(costRate).setScale(2, RoundingMode.HALF_UP)));
			
			if(myOrdId.startsWith("9") && Env.ifProduction()){	//9开头的为测试订单
				log.debug("test order:"+Arrays.asList(myOrdId,trtp,tram,secMchNo,relOrdId));
				continue;
			}
			if(!Env.ifProduction() && myOrdId.startsWith("8"))	//8开头的为生产订单（用于测试）
				continue;
			
			bsu.update(trtm,chlNo,chlOrdId,myOrdId,mchNo2,secMchNo,tram,trtp,relOrdId,memo,cldt,batno,paybk);
		}
		if(Env.needRewrite){
			//记录每个主商户的应收
			DayReportDao.addK2V(trdt,"QQ_FILE_RECVABLE_SUM",mchNo,recvable.toString(),"根据QQ文件计算的应收额(已扣手续费)");
		}
	}

	/**
	 * 按主商户号获取QQ对账文件(gzip版)
	 * @param trdt
	 */
	public static void getQQGzFiles(String trdt){
		//一天只做一次
		JSONObject rt = CheckLimitService.checkLimit("MPOS_BAT_LOAD_QQFILE", trdt, 1);	
		if(rt.getInt("respcd") != 0)
			return;
		//批次号，方便异常时清除数据
		String batno = PhioH.newKey();
		//获取QQ主商户号
		List<Map<String, Object>> qqmchs = MPosBatDao.lstQQMchs();
		BatchSqlUpdate bsu = MPosBatDao.batWxTrans();	//借用微信的SQL
		BufferedReader br = null;
		FTPClient ftpc = new FTPClient();
		try {
			//按主商户号获取文件
			String wxFileFtpIp = Globle.config.get("wxFileFtpIp");	//TODO
			String wxFileFtpUser = Globle.config.get("wxFileFtpUser");	//TODO
			String wxFileFtpPass = Globle.config.get("wxFileFtpPass");	//TODO
			for(Map<String, Object> qqmchMap : qqmchs){
				String mchNo = (String)qqmchMap.get("MAIN_MCHT_NO");
				log.info("开始处理"+mchNo+"文件..."+batno);
				ftpc.connect(wxFileFtpIp);
				ftpc.login(wxFileFtpUser, wxFileFtpPass);
				//ftpc.setFileTransferMode(FTPClient.BINARY_FILE_TYPE);
				ftpc.setFileType(FTP.BINARY_FILE_TYPE);
				ftpc.changeWorkingDirectory("/in/qq/"+trdt);
				
				//gzip stream wrapper
				InputStream is = ftpc.retrieveFileStream(trdt+"-"+mchNo+".csv.gz");
				
				if(is == null){
					log.error("未找到渠道主商户号："+mchNo+" , "+trdt+" 的对账文件 [" + trdt+"-"+mchNo+".csv.gz" + "]");
					throw new IOException("未找到渠道主商户号："+mchNo+" , "+trdt+" 的对账文件 ["+ trdt+"-"+mchNo+".csv.gz" +"]");
				}
				
				GZIPInputStream gis = null;
				try {
					gis = new GZIPInputStream(is);
				} catch (EOFException e) {
					//格式错误很可能是对账单无数据造成的（手Q返回一个空报文），此时通过查询我方数据，进行大致判断（由于是对账前数据，有可能出现误判）
					log.warn(mchNo+" file maybe empty:" + e.getMessage());
					int cnt = MPosBatDao.countMchOrders(mchNo);
					log.info("countMchOrders "+mchNo+":"+cnt);
					//对可能出现无交易的主商户建立了一个白名单
					String maybeEmptyMchNos = Globle.config.get("maybeEmptyMchNos"); 
					if(maybeEmptyMchNos.indexOf(mchNo)>=0  &&  cnt==0){
						log.info("mch order !!MAYBE!! empty, ignore it");
						WxNotifyService.offerMsg("QQ文件"+mchNo+" maybe empty");
						is.close();
						ftpc.disconnect();
						continue;
					}
				}
				br = new BufferedReader(new InputStreamReader(gis,"UTF-8"));

				readQQFile(br,bsu,batno,trdt,mchNo);
				br.close();
				ftpc.disconnect();
			}
			WxNotifyService.offerMsg("QQ文件处理完成");
		} catch (IOException e) {
			log.error("QQ文件处理失败",e);
			WxNotifyService.offerMsg("QQ文件处理失败:"+e.getMessage());
			throw new RuntimeException("QQ文件处理失败",e);
		}finally {
			if(null != br)
				try {br.close();} catch (IOException ignore) {}
			bsu.flush();
			if(ftpc.isConnected())
				try {ftpc.disconnect();} catch (IOException ignore) {}
		}
	}

	
	/**
	 * 按主商户号获取京东对账文件(zip版)
	 * @param trdt
	 */
	public static void getJdZipFiles(String trdt){
		//一天只做一次
		JSONObject rt = CheckLimitService.checkLimit("MPOS_BAT_LOAD_JDFILE", trdt, 1);	
		if(rt.getInt("respcd") != 0)
			return;
		//京东bankreturn文件日期为交易日期次日
		String bkdt = PhioH.compDate(trdt,"yyyyMMdd",1);
		//批次号，方便异常时清除数据
		String batno = PhioH.newKey();
		
		//获取微信主商户号
		List<Map<String, Object>> jdmchs = MPosBatDao.lstJdMchs();
		BatchSqlUpdate bsu = MPosBatDao.batWxTrans();
		BufferedReader br = null;
		FTPClient ftpc = new FTPClient();
		try {
			//按主商户号获取文件
			String wxFileFtpIp = Globle.config.get("wxFileFtpIp");	//TODO
			String wxFileFtpUser = Globle.config.get("wxFileFtpUser");	//TODO
			String wxFileFtpPass = Globle.config.get("wxFileFtpPass");	//TODO
			for(Map<String, Object> wxmchMap : jdmchs){
				String mchNo = (String)wxmchMap.get("MAIN_MCHT_NO");
				log.info("开始处理"+mchNo+"文件..."+batno);
				ftpc.connect(wxFileFtpIp);
				ftpc.login(wxFileFtpUser, wxFileFtpPass);
				ftpc.setFileType(FTP.BINARY_FILE_TYPE);
				ftpc.changeWorkingDirectory("/in/jd/"+trdt);
				InputStream is = ftpc.retrieveFileStream(bkdt+"bankreturn_"+mchNo+".zip");
				
				if(is == null){
					log.error("未找到渠道主商户号："+mchNo+" , "+trdt+" 的对账文件 [" + bkdt+"bankreturn_"+mchNo+".zip" + "]");
					throw new IOException("未找到渠道主商户号："+mchNo+" , "+trdt+" 的对账文件");
				}
				
				ZipInputStream zis = new ZipInputStream(is,Charset.forName("GBK"));
				zis.getNextEntry();
				br = new BufferedReader(new InputStreamReader(zis,"GBK"));
				readJdFile(br,bsu,batno,trdt,mchNo);
				br.close();
				ftpc.disconnect();
			}
			WxNotifyService.offerMsg("京东文件处理完成");
		} catch (IOException e) {
			log.error("京东文件处理失败",e);
			WxNotifyService.offerMsg("京东文件处理失败:"+e.getMessage());
			throw new RuntimeException("京东文件处理失败",e);
		}finally {
			if(null != br)
				try {br.close();} catch (IOException ignore) {}
			bsu.flush();
			if(ftpc.isConnected())
				try {ftpc.disconnect();} catch (IOException ignore) {}
		}
	}

	
	/**
	 * 京东对账单文件内容处理
	 * @param br
	 * @param bsu
	 * @param batno
	 * @param trdt
	 * @param mchNo
	 * @throws DataAccessException
	 * @throws IOException
	 */
	public static void readJdFile(BufferedReader br,BatchSqlUpdate bsu,String batno,
			String trdt,String mchNo) throws DataAccessException, IOException{
		//根据京东文件计算的应收京东的交易额，注意：京东应收是扣除手续费后的
		BigDecimal recvable = new BigDecimal(0);
		String ln = null;
		int i = 0;
		while((ln = br.readLine()) != null){
			if(i++ == 0)
				log.debug("read file start...");
			//处理每一行
			if(!ln.startsWith("="))
				continue;
			String [] fs = ln.split("^=\"|\",=\"|,=\"|\",\"|\",|,=\"|,|\"",-1);
			if(fs.length < 19){
				log.error("异常行:" + ln);
				throw new RuntimeException("京东文件处理失败");
			}else if(fs.length > 19){
				//备注2字段中可能出现逗号，造成之后的银行名称,业务订单号字段出现错位，由于这两个字段不太重要，暂不处理
				log.warn("错位行:" + ln);
			}
			String trtm = fs[9].trim();
			String chlNo = "40";	//TPAM_TYPE	40=京东
			String chlOrdId = "";	//京东不在对账单中提供渠道流水号
			String myOrdId = fs[1];
			String secMchNo = "";	//大商户模式没有子商户号
			String tram = fs[3];
			String trtp = "01";	//01-正常 04-退款
			String trst = fs[5];
			String relOrdId = "";
			if("退款单".equals(trst)){
				trtp = "04";
				relOrdId = fs[11];
			}
			String memo = fs[4]+"|"+fs[14].trim()+"|"+fs[15].trim();
			String cldt = trtm.substring(0,10);	//YYYY-MM-DD
			//String paybk = fs[16];
			String paybk = fs[fs.length - 3];	//错位兼容，取倒数第二个字段
			
			//汇总计算应收金额
			BigDecimal tramt = new BigDecimal(tram);
			BigDecimal cost = new BigDecimal(fs[12]);
			recvable = recvable.add(tramt.subtract(cost));
			
			if(myOrdId.startsWith("9") && Env.ifProduction()){	//9开头的为测试订单
				log.debug("test order:"+Arrays.asList(myOrdId,trtp,tram,secMchNo,relOrdId));
				continue;
			}
			if(!Env.ifProduction() && myOrdId.startsWith("8"))	//8开头的为生产订单（用于测试）
				continue;
			
			bsu.update(trtm,chlNo,chlOrdId,myOrdId,mchNo,secMchNo,tram,trtp,relOrdId,memo,cldt,batno,paybk);
		}
		
		if(Env.needRewrite){
			//记录每个主商户的应收
			DayReportDao.addK2V(trdt,"JD_FILE_RECVABLE_SUM",mchNo,recvable.toString(),"根据京东文件计算的应收额(已扣手续费)");
		}
	}

	/**
	 * 百度对账单新版文件内容读取  ：暂未启用
	 * @param br
	 * @param bsu
	 * @param batno
	 * @param trdt
	 * @param mchNo
	 * @throws DataAccessException
	 * @throws IOException
	 */
	private static void readBfbFile(BufferedReader br,BatchSqlUpdate bsu,String batno,
			String trdt,String mchNo) throws DataAccessException, IOException{
		//根据百度文件计算的应收百度的交易额
		BigDecimal recvable = new BigDecimal(0);
		String ln = null;
		int i = 0;
		while((ln = br.readLine()) != null){
			if(i++ == 0)
				log.debug("read file start...");
			//处理每一行
			if(!StringUtils.hasText(ln))
				continue;
			if(ln.substring(0, 1).getBytes("utf-8").length > 1)	//首字是中文
				continue;
			String [] fs = ln.split("&&",-1);
			if(fs.length < 38+1){
				if(fs.length == 7+1){	//文件尾
					log.info("(总交易单数,总交易金额(分),总退款单数,总退款金额(分),总退款退回单数,总退款退回金额(分),备注) = ("+ln.replaceAll("&&", ",")+")");
					continue;
				}else{
					log.error("异常行:" + ln);
					throw new RuntimeException("百度文件处理失败");
				}	
			}
			String trtm = PhioH.cvtDateTime(fs[4], "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss");	//百付宝支付交易发生时间
			String chlNo = "20";	//TPAM_TYPE	20=百付宝
			String chlOrdId = fs[1];
			String myOrdId = fs[0];
			//取了实际结算金额，未取订单金额，应该要一致，不一致会对账不成功
			String tram = new BigDecimal(fs[8]).movePointLeft(2).toString();	//转换金额格式 1->0.01
			String trtp = "01";	//01-正常 04-退款 
			String trst = fs[2];
			String relOrdId = "";
			if("退款".equals(trst)){
				trtp = "04";
				tram = new BigDecimal(fs[24]).movePointLeft(2).negate().toString();
				relOrdId = myOrdId;
				chlOrdId = fs[23].split("J",2)[1];	//百度的退款流水号是 <商户号>J<退款流水号>的格式，需要转换
				myOrdId = fs[22];
			}else if("退款退回".equals(trst)){
				throw new RuntimeException("不支持百度'退款退回'清算");
			}
			String memo = fs[3];
			String cldt = trtm.substring(0,10);	//YYYY-MM-DD
			String paybk = "";
			String secMchNo = "";
			
			//汇总计算应收金额
			recvable = recvable.add(new BigDecimal(tram));
			
			if(myOrdId.startsWith("9") && Env.ifProduction()){	//9开头的为测试订单
				log.debug("test order:"+Arrays.asList(myOrdId,trtp,tram,relOrdId));
				continue;
			}
			if(!Env.ifProduction() && myOrdId.startsWith("8"))	//8开头的为生产订单（用于测试）
				continue;
			
			bsu.update(trtm,chlNo,chlOrdId,myOrdId,mchNo,secMchNo,tram,trtp,relOrdId,memo,cldt,batno,paybk);
		}
		
		if(Env.needRewrite){
			//记录每个主商户的应收
			DayReportDao.addK2V(trdt,"BFB_FILE_RECVABLE_SUM",mchNo,recvable.toString(),"根据百付宝文件计算的应收额");
		}
	}
	
}
