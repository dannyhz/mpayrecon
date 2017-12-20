package com.sunrun.bill.fileOpr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunrun.bill.model.BillFiledataDO;
import com.sunrun.mpos.common.utils.DateUtils;
import com.sunrun.mpos.common.utils.FileUtils;
import com.sunrun.mpos.common.utils.PropertyUtils;
import com.sunrun.mpos.common.utils.SftpUtils;
import com.sunrun.mpos.common.utils.StringUtils;

/**
 * 文件合并并且上传到SFTP
 * @author Administrator
 *
 */
public class BillFileOpr {
	private static final Logger logger = LoggerFactory.getLogger(BillFileOpr.class);
	
	private Date date ;//日期
	
	//网商/支付宝/聚有财 商户号
	private	static final String mchtNoWS = PropertyUtils.getValue("com.sunrun.bill.orders.mchtNo.ws");
	private static final String mchtNoZFB = PropertyUtils.getValue("com.sunrun.bill.orders.mchtNo.zfb");
	private static final String mchtNoJYC = PropertyUtils.getValue("com.sunrun.bill.orders.mchtNo.jyc");
	
	public BillFileOpr(Date date){
		this.date=date;
	}
	
	
	
	/**
	 * 将支付宝与网商的本地平账文件合并成一个
	 * 判断并合并支付宝与中金的平账文件 分别合并成 网商与支付宝的平账文件
	 * @param date
	 */
	public void mergeFileControl(){
		String _yesterday = DateUtils.formatDate(DateUtils.minusDay(date, 1),"yyyyMMdd");
		//上传到sftp的文件路径 网商
		String _sftpFilePathWS = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.sftp.write.ws.filepath"),_yesterday);
		String _sftpFilePathZFB = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.sftp.write.zfb.filepath"),_yesterday);
		String _sftpFilePathJYC = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.sftp.write.jyc.filepath"),_yesterday);
		//合并之后的文件路径
		String _mergeFilePath = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.bank.local.fqMergeFilePath"),_yesterday);
		 
		
		
		//合并后的文件名称 网商/支付宝   例如 8100000000000001_20170101.csv
		String _mergeFileNameWS = mchtNoWS+"_"+_yesterday+".csv";
		String _mergeFileNameZFB = mchtNoZFB+"_"+_yesterday+".csv";
		String _mergeFileNameJYC = mchtNoJYC+"_"+_yesterday+".csv";
//		String _mergeFileNameWS = mchtNoWS+_yesterday+".csv";
//		String _mergeFileNameZFB = mchtNoZFB+_yesterday+".csv";
//		String _mergeFileNameJYC = mchtNoJYC+_yesterday+".csv";
		
		//中金控制文件FLAG
		String _zjFlag = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.zj.controlFlag"), 
												DateUtils.formatDate(date, "yyyyMMdd"));
		//翼支付控制文件FLAG
		String _yzfFlag = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.yzf.controlFlag"),
												DateUtils.formatDate(date, "yyyyMMdd"));
		
		//判断FQ标记文件是否存在
		if(FileUtils.checkLocalFileExist(_zjFlag+"/FQ") 
				&& FileUtils.checkLocalFileExist(_yzfFlag+"/FQ")){
			//如果两个FQ标记文件都存在 说明两个平账文件都已生成,开始合并文件操作
			//判断本地是否存在合并之后的平账文件,若不存在则合并一下
			if(!FileUtils.checkLocalFileExist(_mergeFilePath+_mergeFileNameWS)){
				logger.info("===== (网商)合并后的平账文件 ["+_mergeFilePath+_mergeFileNameWS+" ]不存在,开始合并");
				mergeFile(_yesterday,mchtNoWS);
			}
			if(!FileUtils.checkLocalFileExist(_mergeFilePath+_mergeFileNameZFB)){
				logger.info("===== (支付宝)合并后的平账文件 ["+_mergeFilePath+_mergeFileNameZFB+" ]不存在,开始合并");
				mergeFile(_yesterday,mchtNoZFB);
			}
			if(!FileUtils.checkLocalFileExist(_mergeFilePath+_mergeFileNameJYC)){
				logger.info("===== (聚有财)合并后的平账文件 ["+_mergeFilePath+_mergeFileNameJYC+" ]不存在,开始合并");
				mergeFile(_yesterday,mchtNoJYC);
			}
		//判断sftp上是否已经存在平账文件 如果不存在则上传到sftp
		 uploadToSftp(_mergeFilePath,_mergeFileNameWS,_sftpFilePathWS);
		 uploadToSftp(_mergeFilePath,_mergeFileNameZFB,_sftpFilePathZFB);
		 uploadToSftp(_mergeFilePath,_mergeFileNameJYC,_sftpFilePathJYC);
		}
		 
	}
	
	/**
	 * 合并文件
	 */
	public static void mergeFile(String date,String mcht_no){
		//合并后的文件名称
		String _mergeFileNameWS = mchtNoWS+"_"+date+".csv";
		String _mergeFileNameZFB = mchtNoZFB+"_"+date+".csv";
		String _mergeFileNameJYC = mchtNoJYC+"_"+date+".csv";
//		String _mergeFileNameWS = mchtNoWS+date+".csv";
//		String _mergeFileNameZFB = mchtNoZFB+date+".csv";
//		String _mergeFileNameJYC = mchtNoJYC+date+".csv";
		
		//合并后的文件路径
		//合并之后的文件路径
		String _mergeFilePath = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.bank.local.fqMergeFilePath"),date);
		
		//合并前的文件路径 中金/翼支付 例如
		String _beforeMergePathZJ = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.bank.local.zjfilepath"),date);
		String _beforeMergePathYZF = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.bank.local.yzffilepath"),date);
		
		//合并前的文件名称  中金/翼支付
		String _beforeMergeFileNameZJ = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.zj.fileName"),date);
		String _beforeMergeFileNameYZF = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.yzf.fileName"),date);
		
		//前缀
		String _prefixZFB = PropertyUtils.getValue("com.sunrun.bill.filename.zfb");
		String _prefixWS = PropertyUtils.getValue("com.sunrun.bill.filename.ws");
		String _prefixJYC = PropertyUtils.getValue("com.sunrun.bill.filename.jyc");
		
		if(mchtNoWS.equals(mcht_no)){
			//获取网商的两个文件(网商中金、网商翼支付)准备合并
			String _fileNameWSZJ = _beforeMergePathZJ+_prefixWS+_beforeMergeFileNameZJ;//合并前的网商 中金文件 
			String _fileNameWSYZF = _beforeMergePathYZF+_prefixWS+_beforeMergeFileNameYZF;//合并前的网商 翼支付文件
			writeFile(_fileNameWSZJ,_fileNameWSYZF,_mergeFilePath,_mergeFileNameWS);
		}else if(mchtNoZFB.equals(mcht_no)){
			//获取支付宝的两个文件(支付宝中金、支付宝翼支付)准备合并
			String _fileNameZFBZJ = _beforeMergePathZJ+_prefixZFB+_beforeMergeFileNameZJ;//合并前的支付宝 中金文件 
			String _fileNameZFBYZF = _beforeMergePathYZF+_prefixZFB+_beforeMergeFileNameYZF;//合并前的支付宝 翼支付文件
			writeFile(_fileNameZFBZJ, _fileNameZFBYZF,_mergeFilePath,_mergeFileNameZFB); 
		}else if(mchtNoJYC.equals(mcht_no)){
			//获取聚有财的两个文件(支付宝中金、支付宝翼支付)准备合并
			String _fileNameJYCZJ = _beforeMergePathZJ+_prefixJYC+_beforeMergeFileNameZJ;//合并前的聚有财 中金文件 
			String _fileNameJYCYZF = _beforeMergePathYZF+_prefixJYC+_beforeMergeFileNameYZF;//合并前的聚有财 翼支付文件
			writeFile(_fileNameJYCZJ, _fileNameJYCYZF,_mergeFilePath,_mergeFileNameJYC); 
		}
		
		
	}
	
	/**
	 * 将业务内容fileName1,filename2 写入到内存并排序后写入到本地文件writeFileName
	 * @param fileName1 平账文件1
	 * @param fileName2 平账文件2
	 * @param writeFileName 生成的文件
	 */
	public static void writeFile(String fileName1,String fileName2,String writeFilePath,String writeFileName){
		//如果取不到其中一个 是因为某一个在这天没有发起交易，用另一个当做合并后的文件  
		InputStream is1=null;
		InputStream is2=null;
		List<BillFiledataDO> list1= new ArrayList<BillFiledataDO>();
		List<BillFiledataDO> list2= new ArrayList<BillFiledataDO>();
		if(!FileUtils.checkLocalFileExist(writeFileName)){
			//如果不存在则开始解析两个文件并写入
			File file1 = new File(fileName1);
			File file2 = new File(fileName2);
			if(FileUtils.checkLocalFileExist(fileName1)){
				try {
					is1 = new FileInputStream(file1);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			if(FileUtils.checkLocalFileExist(fileName2)){
				try {
					is2 = new FileInputStream(file2);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			try {
				if(null!=is1){
					list1= analocalFile(is1);
				}
				if(null!=is2){
					list2= analocalFile(is2);
				}
				list1.addAll(list2);
				if(null!=list1&&list1.size()>0){
					 Collections.sort(list1);
				        for(BillFiledataDO bfdDO:list1) {
				        	String _content = bfdDO.getChannel_time()+","+
				        						bfdDO.getTrx_id()+","+
				        						 bfdDO.getMcht_seq_no()+","+//2017-4-26 add
				        						  bfdDO.getCust_cast_no()+","+
				        						   bfdDO.getTrx_amt()+"\n";
				        	FileUtils.writeFile(writeFilePath,writeFileName, _content);
				        }
				        logger.info("===== 文件[ "+writeFilePath+writeFileName+" ] 写入完毕=====");
				}else{
//					logger.info("===== 合并前数据为空，文件[ "+writeFilePath+writeFileName+" ] 无需写入=====");
					logger.info("===== 合并前数据为空，文件[ "+writeFilePath+writeFileName+" ] 写入空文件=====");
					FileUtils.writeFile(writeFilePath,writeFileName, "");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error(e.getMessage(),e);
			}
		}
	}
	

	/**
	 * 解析文件
	 * @param fileName
	 * @return
	 */
	public static List<BillFiledataDO> analocalFile(InputStream inputStream) {
		BufferedReader br = null;
		List<BillFiledataDO> list =new ArrayList<BillFiledataDO>();
		try {
			br = new BufferedReader(new InputStreamReader(inputStream));
			String line = "";
			while((line=br.readLine()) != null){
				list.add(handleLine(line));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(br != null){
					br.close();
				} 
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		return list;
	}
	
	/**
	 * 对每一行String的处理 
	 * @param unHandleStr
	 * @return BillFiledataDO
	 */
	public static BillFiledataDO handleLine(String unHandleStr){
        String _channel_time = "";
        String _trx_id ="";
        String _mcht_seq_no = "";//2017-4-26 add
        String _cust_cast_no = "";
        String _trx_amt = "";
        try {
        	_channel_time = StringUtils.fatch(unHandleStr, 0, ",");
        	_trx_id = StringUtils.fatch(unHandleStr, 1, ",");
        	_mcht_seq_no = StringUtils.fatch(unHandleStr, 2, ",");
        	_cust_cast_no = StringUtils.fatch(unHandleStr, 3, ",");
        	_trx_amt = StringUtils.fatch(unHandleStr, 4, ",");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
        BillFiledataDO _tmpDO = new BillFiledataDO();
        _tmpDO.setChannel_time(_channel_time);
        _tmpDO.setCust_cast_no(_cust_cast_no);
        _tmpDO.setTrx_amt(_trx_amt);
        _tmpDO.setTrx_id(_trx_id);
        _tmpDO.setMcht_seq_no(_mcht_seq_no);//2017-4-26 add
        return _tmpDO;
    }
	
	/**
	 * 把合并后的平账文件上传到SFTP
	 */
	public static void uploadToSftp(String filePath,String fileName,String sftpFilePath){
		//行内SFTP配置
		String _sftpHost = PropertyUtils.getValue("com.sunrun.bill.sftp.host");
		String _sftpPort = PropertyUtils.getValue("com.sunrun.bill.sftp.port");
		String _sftpName = PropertyUtils.getValue("com.sunrun.bill.sftp.name");
		String _sftpPsw  = PropertyUtils.getValue("com.sunrun.bill.sftp.psw");
		SftpUtils sftpUtils = new SftpUtils(_sftpHost, Integer.valueOf(_sftpPort), _sftpName, _sftpPsw);
		//先检查是否有待上传的本地文件 再检查sftp上是否已存在
		if(FileUtils.checkLocalFileExist(filePath+fileName)){
			try{
//				sftpUtils.connect();
				sftpUtils.connectRSA();
				if(!sftpUtils.checkFileExists(sftpFilePath, fileName)){
					logger.info("===== sftp服务器不存在平账文件,开始上传 [ "+sftpFilePath+fileName+" ]=====");
					sftpUtils.upload(sftpFilePath,filePath+fileName);
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}finally {
				sftpUtils.logOut();
			}
		}
	}
	 
}


