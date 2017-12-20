package com.sunrun.bill.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunrun.bill.exception.BillFileOptException;
import com.sunrun.mpos.common.utils.DateUtils;
import com.sunrun.mpos.common.utils.FileUtils;
import com.sunrun.mpos.common.utils.PropertyUtils;
import com.sunrun.mpos.common.utils.SftpUtils;
import com.sunrun.mpos.common.utils.StringUtils;

public class ZjFileOpt extends AbstractFileOpt{
	
	private static final Logger logger = LoggerFactory.getLogger(ZjFileOpt.class);
	private String date;
	private String fileName;

	public ZjFileOpt(){
	}
	
	public Map<String,Object> queryFileList(Date date) throws BillFileOptException{
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap.put("fileExist", "1");//zj对账文件存在标识符 默认存在
		resultMap.put("dataList", null);//zj对账文件数据 默认空
		List<byte[]> zjFileList = null;
		InputStream inputStream = null;

		String dateStr = DateUtils.formatDate(DateUtils.minusDay(date, 1), "yyyyMMdd");
		
		//中金文件名
		String _zjFileName = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.zj.fileName"),dateStr);//根据日期解析文件名date
		
		//本地存放路径
		String _bankLocalZjFilePath = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.bank.local.zjfilepath"),dateStr);
		
		//行内SFTP配置
		String _sftpHost = PropertyUtils.getValue("com.sunrun.bill.sftp.host");
		String _sftpPort = PropertyUtils.getValue("com.sunrun.bill.sftp.port");
		String _sftpName = PropertyUtils.getValue("com.sunrun.bill.sftp.name");
		String _sftpPsw  = PropertyUtils.getValue("com.sunrun.bill.sftp.psw");
		String _sfptPath = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.sftp.zj.filepath"),dateStr);
		SftpUtils sftpUtils = new SftpUtils(_sftpHost, Integer.valueOf(_sftpPort), _sftpName, _sftpPsw);
		
		try{
			logger.info("=====开始判断本地文件 ["+_bankLocalZjFilePath+_zjFileName+"](中金)是否存在=====");
			//判断本地文件是否存在
			if(!FileUtils.checkLocalFileExist(_bankLocalZjFilePath+_zjFileName)){
				 //本地文件不存在 开始从sftp服务器下载到本地
				logger.info("=====本地文件 ["+_bankLocalZjFilePath+_zjFileName+"](中金)不存在,开始从行内SFTP服务器下载=====");
				logger.info("=====开始将文件 ["+_sfptPath+_zjFileName+"](中金) 上传到本地=====");
				sftpUtils.connectRSA();
//				sftpUtils.connect();
				if(!sftpUtils.downloadAndSave(_sfptPath, _zjFileName, _bankLocalZjFilePath,_zjFileName)){
					 FileUtils.deleteLocalFile(_bankLocalZjFilePath+_zjFileName);
					 resultMap.put("fileExist", "0");//zj对账文件存在标识符  不存在
				}
			}else{
				//本地文件存在 不做操作
				logger.info("=====本地文件 ["+_bankLocalZjFilePath+_zjFileName+"](中金)已存在!=====");
			}
			//开始解析本地文件
			inputStream = new FileInputStream(new File(_bankLocalZjFilePath+_zjFileName));
			if(inputStream.available()!=0){
				zjFileList=anaFtpFile(inputStream) ;
				resultMap.put("dataList", zjFileList);
				logger.info("=====文件记录数=["+zjFileList.size()+"](中金)=====");
			}
		}catch (FileNotFoundException e) {
//			logger.error(e.getMessage(),e);
			resultMap.put("fileExist", "0");//zj对账文件存在标识符  不存在
		}catch (IOException e2) {
			resultMap.put("fileExist", "0");//zj对账文件存在标识符  不存在
			logger.error(e2.getMessage(),e2);
		} catch (Exception e3) {
			resultMap.put("fileExist", "0");//zj对账文件存在标识符  不存在
			// TODO Auto-generated catch block
			logger.error(e3.getMessage(),e3);
		}finally {
			sftpUtils.logOut();
		}
		return resultMap;
	}
	
	/**
	 * 解析文件
	 * @param fileName
	 * @return
	 */
	public List<byte[]> anaFtpFile(InputStream inputStream) {
		BufferedReader br = null;
		List<byte[]> list =new ArrayList<byte[]>();
		try {
			br = new BufferedReader(new InputStreamReader(inputStream));
			String line = "";
			while((line=br.readLine()) != null){
				byte[] bs = handleLine(line);
				if(bs!=null){
					list.add(bs);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(br != null){
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
//		if(list.size()>0){//去掉第一行汇总数据
//			list.remove(0);
//		}
		return list;
	}
	
	/**
	 * 对每一行String的处理 
	 * @param unHandleStr
	 * @return
	 */
	public byte[] handleLine(String unHandleStr){
		byte[] resStr = null;
		String trxIdFlag=PropertyUtils.getValue("com.sunrun.bill.trxidflag");//生产数据标记 3表示生产数据 其他表示测试数据 2017-4-26 add
		try {
			String _trx_id = StringUtils.fatch(unHandleStr, 1, ",");
			if(!StringUtils.isEmpty(trxIdFlag) && trxIdFlag.equals(_trx_id.substring(0,1)) && 
					 StringUtils.fatch(unHandleStr, 1, ",").length()==32){//排除汇总这行
				resStr = (_trx_id+//流水号trx_id
		        		","+
		        		StringUtils.fatch(unHandleStr, 3, ",")+","+StringUtils.fatch(unHandleStr, 4, ",")).getBytes();//金额
			}
		} catch (Exception e) {
			logger.error("字符解析失败["+unHandleStr+"]");
		}
		return resStr;
    }

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public static void main(String[] args) {
		try{
//			anaFtpFile(new FileInputStream("d:/zj/CommissionPaymentBill_20170501.csv")) ;
		}catch(Exception e){
			
		}
	}

	
}
