package citic.hz.mpos.test.ftp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.object.BatchSqlUpdate;

import citic.hz.mpos.kit.ApxLoaderListener;
import citic.hz.mpos.kit.Env;
import citic.hz.mpos.kit.Globle;
import citic.hz.mpos.service.CmpGetDtlService;
import citic.hz.mpos.service.dao.DayReportDao;
import citic.hz.mpos.service.dao.MPosBatDao;
import citic.hz.phio.kit.PhioH;

public class FtpAccessTest {
	
	String wxFileFtpId = "192.168.8.138";
	String wxFileFtpUser = "guest";
	String wxFileFtpPass = "guest";
	
	private static final Logger log = Logger.getLogger(FtpAccessTest.class);
	
	@Test
	public void accessFTP() throws SocketException, IOException{
	//	String wxFileFtpIp = Globle.config.get("wxFileFtpIp");	//TODO
	//	String wxFileFtpUser = Globle.config.get("wxFileFtpUser");	//TODO
	//	String wxFileFtpPass = Globle.config.get("wxFileFtpPass");	//TODO
	
		//filename = 20160823bankreturn_22294531001.csv
		
		ApxLoaderListener apxLoader = new ApxLoaderListener();
		apxLoader.contextInitialized(null);
		
		
		String jdMerchantNo = "22294531001";
		String trdt = "20171120"; 
		FTPClient ftpc = new FTPClient();
		BatchSqlUpdate bsu = MPosBatDao.batWxTrans();
		BufferedReader br = null;
		//京东bankreturn文件日期为交易日期次日
		String bkdt = PhioH.compDate(trdt,"yyyyMMdd",1);
		//批次号，方便异常时清除数据
		String batno = PhioH.newKey();
		
		
		ftpc.connect(wxFileFtpId);
		
		ftpc.login(wxFileFtpUser, wxFileFtpPass);
		ftpc.setFileType(FTP.BINARY_FILE_TYPE);
		ftpc.changeWorkingDirectory("/in/jd/"+trdt);
		InputStream is = ftpc.retrieveFileStream(bkdt+"bankreturn_"+jdMerchantNo+".zip");
		ZipInputStream zis = new ZipInputStream(is,Charset.forName("GBK"));
		zis.getNextEntry();
		
		br = new BufferedReader(new InputStreamReader(zis,"GBK"));
		readJdFile(br,bsu,batno,trdt,jdMerchantNo);
		br.close();
		ftpc.disconnect();
		
	}
	
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
			
			//bsu.update(trtm,chlNo,chlOrdId,myOrdId,mchNo,secMchNo,tram,trtp,relOrdId,memo,cldt,batno,paybk);
		}
		
		//记录每个主商户的应收
	//	DayReportDao.addK2V(trdt,"JD_FILE_RECVABLE_SUM",mchNo,recvable.toString(),"根据京东文件计算的应收额(已扣手续费)");

	}

}
