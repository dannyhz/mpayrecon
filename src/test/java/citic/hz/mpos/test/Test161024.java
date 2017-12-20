package citic.hz.mpos.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.simple.JSONObject;
import org.springframework.jdbc.object.BatchSqlUpdate;

import citic.hz.mpos.kit.Globle;
import citic.hz.mpos.service.MPosDataSource;
import citic.hz.mpos.service.dao.MPosBatDao;
import citic.hz.phio.kit.PhioH;

public class Test161024 {
	
	public static JSONObject tagmers = new JSONObject()
			.append("15383840", 1)
			.append("15383817", 1)
			.append("15383810", 1)
			.append("15383801", 1)
			.append("15383697", 1)
			.append("15361729", 1)
			.append("15149619", 1)
			.append("15149558", 1);
	
	
	public static void run(){
		filterWxFiles("20161010");
		filterWxFiles("20161011");
		filterWxFiles("20161012");
		filterWxFiles("20161013");
		filterWxFiles("20161014");
		filterWxFiles("20161019");
		filterWxFiles("20161020");
		filterWxFiles("20161021");
		filterWxFiles("20161022");
		filterWxFiles("20161023");
		//filterWxFiles("20161024");
	}
	
	public static void filterWxFiles(String trdt){
		//批次号，方便异常时清除数据
		String batno = PhioH.newKey();
		//获取微信主商户号
		List<Map<String, Object>> wxmchs = MPosBatDao.lstWxMchs();
		BatchSqlUpdate bsu = batWxTrans();
		BufferedReader br = null;
		FTPClient ftpc = new FTPClient();
		try {
			//按主商户号获取文件
			String wxFileFtpIp = Globle.config.get("wxFileFtpIp");	//TODO
			String wxFileFtpUser = Globle.config.get("wxFileFtpUser");	//TODO
			String wxFileFtpPass = Globle.config.get("wxFileFtpPass");	//TODO
			for(Map<String, Object> wxmchMap : wxmchs){
				String mchNo = (String)wxmchMap.get("MAIN_MCHT_NO");
				ftpc.connect(wxFileFtpIp);
				ftpc.login(wxFileFtpUser, wxFileFtpPass);
				ftpc.setFileType(FTP.BINARY_FILE_TYPE);
				ftpc.changeWorkingDirectory("/in/wx/"+trdt);
				InputStream is = ftpc.retrieveFileStream(trdt+"-"+mchNo+".csv");
				br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
				String ln = null;
				while((ln = br.readLine()) != null){
					//处理每一行
					if(!ln.startsWith("`"))
						continue;
					String [] fs = ln.split("`|,`|,\"`|\",`",-1);
					if(fs.length < 25){
						if(fs.length == 6)	//文件尾
							continue;
						else{
							throw new RuntimeException("微信文件处理失败");
						}	
					}
					String trtm = fs[1];
					String chlNo = "05";	//TPAM_TYPE	05=微信
					String chlOrdId = fs[6];
					String myOrdId = fs[7];
					if(myOrdId.startsWith("9"))	//9开头的为测试订单
						continue;
					String mchNo2 = fs[3];	//文件内容中的商户号
					String secMchNo = fs[4];
					
					if(!tagmers.containsKey(secMchNo)){	//非目标商户，放弃
						continue;
					}
					
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
					bsu.update(trtm,chlNo,chlOrdId,myOrdId,mchNo2,secMchNo,tram,trtp,relOrdId,memo,cldt,batno,paybk);
				}
				br.close();
				ftpc.disconnect();
			}
		} catch (IOException e) {
			throw new RuntimeException("微信文件处理失败",e);
		}finally {
			if(null != br)
				try {br.close();} catch (IOException ignore) {}
			bsu.flush();
			if(ftpc.isConnected())
				try {ftpc.disconnect();} catch (IOException ignore) {}
		}
	}
	
	
	public static BatchSqlUpdate batWxTrans(){
		String sqlstr = "insert into MPOS.TEMP161024(TRTM,CHANNEL_NO, CHL_ORDER_ID, MY_ORDER_ID, MCH_NO, SEC_MCH_NO, TRAM, TRTP,REL_ORDER_ID, MEMO,CLDT,BNO,PAYBANK) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		int[] ps=new int[13];
		for(int i=0;i<ps.length;i++){
			ps[i] = Types.VARCHAR;
		}
		BatchSqlUpdate bsu = new BatchSqlUpdate(MPosDataSource.getInstance(), sqlstr, ps);
		bsu.setBatchSize(7000);
		return bsu;
	}

}
