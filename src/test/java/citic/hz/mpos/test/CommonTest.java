package citic.hz.mpos.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.object.BatchSqlUpdate;

import citic.hz.mpos.kit.ApxLoaderListener;
import citic.hz.mpos.service.CmpGetDtlService;
import citic.hz.mpos.service.CustomSpecialService;
import citic.hz.mpos.service.dao.MPosBatDao;
import citic.hz.mpos.service.event.Event;
import citic.hz.mpos.service.event.EventProcessor;
import citic.hz.mpos.service.event.EventService;
import citic.hz.phio.kit.PhioH;

public class CommonTest {

	private static ApxLoaderListener apx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		apx = new ApxLoaderListener();
		apx.contextInitialized(null);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		apx.contextDestroyed(null);
	}

	@Test
	public void testZfbFile() throws IOException {
		File f = new File("d:\\tmp\\11\\20884213993539390156_20160925_DETAILS.csv");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f),"GBK"));
		String ln = null;
		int i = 0;
		while((ln = br.readLine()) != null){
			//处理每一行
			if(!ln.startsWith("20"))	//过滤文件头尾
				continue;
			String [] fs = ln.split(",",-1);
			if(fs.length != 27){
				System.out.println("异常行:" + ln);
				throw new RuntimeException("支付宝文件处理失败");
			}
			i++;
		}
		br.close();
		System.out.println(i);
		
	}

	
	@Test
	public void testZfbZipFile() throws IOException {
		File f = new File("C:/danny/document/tmp/20889112124162010156_20160727.csv.zip");
		ZipInputStream zis = new ZipInputStream(new FileInputStream(f),Charset.forName("GBK"));
		ZipEntry zipfile = null;
		while((zipfile = zis.getNextEntry())!=null){
			if(zipfile.getName().endsWith("DETAILS.csv"))	//只取明细文件
				break;	//定位到明细文件就退出循环
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(zis,"GBK"));
		String ln = null;
		int i = 0;
		while((ln = br.readLine()) != null){
			//处理每一行
			if(!ln.startsWith("20"))	//过滤文件头尾
				continue;
			String [] fs = ln.split(",",-1);
			if(fs.length != 27){
				System.out.println("异常行:" + ln);
				throw new RuntimeException("支付宝文件处理失败");
			}
			i++;
		}
		br.close();
		System.out.println(i);
		
	}

	
	@Test
	public void testZfbFtpZipFile() throws IOException {
		FTPClient ftpc = new FTPClient();
		ftpc.connect("22.32.2.227");
		ftpc.login("phio", "iamphio");
		ftpc.changeWorkingDirectory("tmp");
		ftpc.setFileTransferMode(FTPClient.BINARY_FILE_TYPE);
		InputStream is = ftpc.retrieveFileStream("20884213993539390156_20160925.csv.zip");
		ZipInputStream zis = new ZipInputStream(is,Charset.forName("GBK"));
		ZipEntry zipfile = null;
		while((zipfile = zis.getNextEntry())!=null){
			if(zipfile.getName().endsWith("DETAILS.csv"))	//只取明细文件
				break;	//定位到明细文件就退出循环
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(zis,"GBK"));
		String ln = null;
		int i = 0;
		while((ln = br.readLine()) != null){
			//处理每一行
			if(!ln.startsWith("20"))	//过滤文件头尾
				continue;
			String [] fs = ln.split(",",-1);
			if(fs.length != 27){
				System.out.println("异常行:" + ln);
				throw new RuntimeException("支付宝文件处理失败");
			}
			if(ln.indexOf("8042016092515241003862239")>0){
				System.out.println("异常行:" + ln);
				System.out.println(PhioH.hexString(ln.getBytes("GBK")));
			}
			i++;
		}
		br.close();
		ftpc.disconnect();
		System.out.println(i);
		
	}

	
	@Test
	public void testWxFtpGzFile() throws IOException {
		FTPClient ftpc = new FTPClient();
		ftpc.connect("22.32.2.243");
		ftpc.login("anzh", "anzh");
		ftpc.setFileType(FTP.BINARY_FILE_TYPE);
		//gzip stream wrapper
		InputStream is = ftpc.retrieveFileStream("20161020-1343548801.csv.gz");
		GZIPInputStream gis = new GZIPInputStream(is);
		BufferedReader br = new BufferedReader(new InputStreamReader(gis,"UTF-8"));

		String ln = null;
		int c=0;
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
			c++;
			if(c<10)
				System.out.println(Arrays.toString(new String[]{trtm,chlNo,chlOrdId,myOrdId,mchNo2,secMchNo,tram,trtp,relOrdId,memo,cldt,paybk}));
		}
		System.out.println(c);
		br.close();
		ftpc.disconnect();
	}

	
	@Test
	public void testWxFtpZipFile() throws IOException {
		FTPClient ftpc = new FTPClient();
		ftpc.connect("22.32.2.243");
		ftpc.login("anzh", "anzh");
		ftpc.setFileType(FTP.BINARY_FILE_TYPE);
		
		InputStream is = ftpc.retrieveFileStream("1343548801-All-2016-10-27.zip");
		ZipInputStream zis = new ZipInputStream(is,Charset.forName("UTF-8"));
		zis.getNextEntry();
		BufferedReader br = new BufferedReader(new InputStreamReader(zis,"UTF-8"));
		String ln = null;
		int c=0;
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
			c++;
			if(c<10)
				System.out.println(Arrays.toString(new String[]{trtm,chlNo,chlOrdId,myOrdId,mchNo2,secMchNo,tram,trtp,relOrdId,memo,cldt,paybk}));
		}
		System.out.println(c);
		br.close();
		ftpc.disconnect();
	}
	
	@Test
	public void testLoadReset() throws IOException {
		MPosBatDao.loadReset("mpos.BAT2_CMP_MPOS_DTL_FAIL");
	}
	
	@Test
	public void testEventService(){
		EventService.sub("/rewrite/all/finish", new EventProcessor() {
			public void process(Event event) {
				CustomSpecialService.yxSum();
			}
		});
		EventService.pub("/rewrite/all/finish", null);
		try{Thread.sleep(3000);}catch(Exception e){};
	}
	
	@Test public void testJdFileSplit() throws IOException{
		File f = new File("d:/tmp/20/20160823bankreturn_22294531001.csv");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f),"GBK"));
		String ln = null;
		while((ln = br.readLine()) != null){
			//处理每一行
			if(!ln.startsWith("="))
				continue;
			String [] fs = ln.split("^=\"|\",=\"|,=\"|\",\"|\",|,=\"|,|\"",-1);
			if(fs.length < 19){
				throw new RuntimeException("京东文件处理失败");
			}else if(fs.length > 19){
				//备注2字段中可能出现逗号，造成之后的银行名称,业务订单号字段出现错位，由于这两个字段不太重要，暂不处理
				System.out.println("错位行:" + ln);
			}
			String trtm = fs[9].trim();
			String chlNo = "40";	//TPAM_TYPE	40=京东
			String chlOrdId = fs[17].trim();	//退款可能为-1，需确认
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
			System.out.println(Arrays.asList(trtm,chlNo,chlOrdId,myOrdId,secMchNo,tram,trtp,relOrdId,memo,cldt,paybk,tramt,cost));
		}
		br.close();
	}
	
	
	@Test public void testQQFileSplit() throws IOException{
		File f = new File("C:/danny/code_base/20171031_mpos_bill/MPayBill/src/test/resources/order/qq_order.xlsx");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
		String ln = null;
		int i = 0;
		while((ln = br.readLine()) != null){
			//处理每一行
			if(!ln.startsWith("`"))
				continue;
			String [] fs = ln.split("^`|,`|,\"`|\",`",-1);
			if(fs.length < 30+1){
				if(fs.length == 9+1){	//文件尾
					continue;
				}else{
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
			System.out.println(Arrays.asList(trtm,chlNo,chlOrdId,myOrdId,secMchNo,tram,trtp,relOrdId,memo,cldt,paybk,mchNo2));
		}
		br.close();
		
	}
	
	@Test public void testJdZipFile() throws SocketException, IOException{
		String trdt = "20170929";
		String mchNo = "110426699009";
		String batno = PhioH.newKey();
		FTPClient ftpc = new FTPClient();
		ftpc.connect("22.32.102.70");
		ftpc.login("fuser", "fuser");
		ftpc.setFileType(FTP.BINARY_FILE_TYPE);
		ftpc.changeWorkingDirectory("in/jd/"+trdt);
		InputStream is = ftpc.retrieveFileStream(trdt+"bankreturn_"+mchNo+".zip");
		ZipInputStream zis = new ZipInputStream(is,Charset.forName("GBK"));
		zis.getNextEntry();
		BufferedReader br = new BufferedReader(new InputStreamReader(zis,"GBK"));
		BatchSqlUpdate bsu = MPosBatDao.batWxTrans();
		CmpGetDtlService.readJdFile(br,bsu,batno,trdt,mchNo);
		br.close();
		ftpc.disconnect();
		bsu.flush();
	}
}
