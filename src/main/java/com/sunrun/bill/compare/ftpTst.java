package com.sunrun.bill.compare;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.sunrun.mpos.common.utils.FtpUtils;
import com.sunrun.mpos.common.utils.StringUtils;

public class ftpTst {
	public static void main1(String[] args) {
		
		FtpUtils ftpUtils = new FtpUtils("192.168.110.1", 222, "administrator", "132", "/");
		ftpUtils.ftpLogin();
		try{
//			ByteArrayOutputStream outputStream = ftpUtils.downloadFile("lgos/yzfFile.txt");//下载
//			System.out.println(outputStream.toString());
//			InputStream ins=null;  
//			BufferedReader br =null;  
//			ins=new FileInputStream(new File("D:\\logs\\yzfFile.txt"));  
//			System.out.println("111");
//			ftpUtils.uploadFile(ins, "lgos", "yzfFile.txt");
//			System.out.println("222");
		}catch(Exception e){
			System.out.println("eee");
		}
		
		ftpUtils.ftpLogOut();
		
	}
	
	public static void main(String[] args) {
		String _yzfFileName = MessageFormat.format("QYZH_0000000000015061_{0}.txt", "20161214");//根据日期解析文件名date
		String _bankYzfFtpHost = "192.168.48.1";
		String _bankYzfFtpPort = "21"; 
		String _bankYzfFtpName = "administrator";
		String _bankYzfFtpPsw  = "132";
		String _bankYzfFtpPath = MessageFormat.format("bank/{0}/yzf/", "20161214");
		
		String _yzfFtpHost = "192.168.110.1";
		String _yzfFtpPort = "222";
		String _yzfFtpName = "administrator";
		String _yzfFtpPsw  = "132";
		String _yzfFtpPath = MessageFormat.format("lgos/{0}/yzf/", "20161214");
		//从翼支付提供的FTP取文件
		FtpUtils bankFtpUtils = new FtpUtils(_bankYzfFtpHost, Integer.valueOf(_bankYzfFtpPort), 
				  _bankYzfFtpName, _bankYzfFtpPsw, "/");
		FtpUtils yzfFtpUtils = new FtpUtils(_yzfFtpHost,Integer.valueOf(_yzfFtpPort), 
											_yzfFtpName, _yzfFtpPsw, "/");
		List<byte[]> yzfFileList = null;
		Boolean uploadFlag=Boolean.FALSE;
		ByteArrayOutputStream outputStream=null;
		ByteArrayInputStream inputStream = null;
		try {
			bankFtpUtils.ftpLogin();
			//首先判断行内FTP服务器中是否已经有对账文件了
			if(!bankFtpUtils.checkFileExist(_bankYzfFtpPath,_yzfFileName)){
				//如果行内FTP不存在此文件，则从渠道FTP取下文件并且上传到行内FTP上 如果已经有此文件则不做下载上传操作
				yzfFtpUtils.ftpLogin();
				outputStream = yzfFtpUtils.downloadFile(_yzfFtpPath,_yzfFileName);
				if(outputStream!=null && outputStream.size()>0){
					//如果文件大小大于0则开始上传  ?outputStream=>inputStream
					inputStream = new ByteArrayInputStream(outputStream.toByteArray());
					bankFtpUtils.uploadFile(inputStream, _bankYzfFtpPath, _yzfFileName);
				}
			}
			 
			//从行内FTP下载文件 然后用下载到的outputStream来做解析
			if(outputStream==null||outputStream.size()<=0){
				outputStream = bankFtpUtils.downloadFile(_bankYzfFtpPath,_yzfFileName);
			}
			inputStream = new ByteArrayInputStream(outputStream.toByteArray());
			anaFtpFile(inputStream) ;
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(outputStream!=null){
					outputStream.close();
				}
				if(inputStream!=null){
					inputStream.close();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			yzfFtpUtils.ftpLogOut();
			bankFtpUtils.ftpLogOut();
		}
	}
	
	/**
	 * 解析文件
	 * @param fileName
	 * @return
	 */
	public static List<byte[]> anaFtpFile(ByteArrayInputStream inputStream) {
		BufferedReader br = null;
		List<byte[]> list =new ArrayList<byte[]>();
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
	 * @return
	 */
	public static byte[] handleLine(String unHandleStr){
        String _rString =  StringUtils.fatch(unHandleStr, 0, ",")+";"+StringUtils.fatch(unHandleStr, 1, ",")+","+StringUtils.fatch(unHandleStr, 3, ",");
        return _rString.getBytes();
    }

}
