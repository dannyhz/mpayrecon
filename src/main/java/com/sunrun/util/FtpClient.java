package com.sunrun.util;

import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunrun.mpos.common.utils.DateUtils;
import com.sunrun.mpos.common.utils.PropertyUtils;

/**
 * 外联FTP接口调用
 * @author linjh
 */
public class FtpClient {
	private static final Logger logger = LoggerFactory.getLogger(FtpClient.class);
 
	
	/**
	 * 外联调用ftp接口 从商户ftp 获取对账单到 本行服务器ftp
	 * @return
	 */
	public static String ftpTask(String std400trcd,String std400num,String filename,String srcpath,String destpath,String ip,int port,String timeout) throws Exception {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("root");
		Element e1 = root.addElement("std400trcd");
		e1.addText(std400trcd);
		Element e2 = root.addElement("std400num");
		e2.addText(std400num);
		Element e3 = root.addElement("filename");
		e3.addText(filename);
		Element e4 = root.addElement("srcpath");
		e4.addText(srcpath);
		Element e5 = root.addElement("destpath");
		e5.addText(destpath);
		Element e6 = root.addElement("ftpcode");
		e6.addText("fput");
		Element e7 = root.addElement("file-key");
		Element e8 = root.addElement("digest-algorithm");
		Element e9 = root.addElement("digest-value");
		Element e10 = root.addElement("clientip");
		e10.addText(PropertyUtils.getValue("com.sunrun.bill.localip"));//本机IP
		Element e11 = root.addElement("timestamp");
		e11.addText(DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));//发送时间
		return SocketUnit.makeSendMsg(document.asXML(), 6,ip,port,timeout);
	}
	
	public static void main(String[] args) {
		try {
			String jycCode ="Z0001";
			String jycCode1 = jycCode.substring(0,1);
			String jycCode2 = jycCode.substring(1);
//			ftpTask("F0002", "AAA.TXT", "/", "IN/BAF","22.32.102.60",41031,"5000");
			System.out.println(ftpTask(jycCode1, jycCode2,"6100000000000002_20170523.csv", "20170523", "","22.32.102.63",41031,"5000"));
//			System.out.println(DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
