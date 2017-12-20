package com.sunrun.api;

import java.text.MessageFormat;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunrun.mpos.common.utils.DateUtils;
import com.sunrun.mpos.common.utils.FileUtils;
import com.sunrun.mpos.common.utils.PropertyUtils;
import com.sunrun.util.FtpClient;

public class DkGetBillfileApi extends HttpServlet{
	private static final Logger logger = LoggerFactory.getLogger(DkGetBillfileApi.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		getFile(req);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		getFile(req);
	}
	 
	/**
	 * 通过外联接口将商户的对账单文件获取到行内ftp服务器（中金、翼支付）
	 */
	private void getFile(HttpServletRequest req){
		String dateStr ="";
		String reqDateStr = req.getParameter("date");
		if(reqDateStr!=null){
			logger.info("传入的日期=["+reqDateStr+"]");
			dateStr=reqDateStr;
		}else{
			Date date = new Date();
			dateStr = DateUtils.formatDate(DateUtils.minusDay(date, 1), "yyyyMMdd");
		}
		//本地翼支付对账文件存放路径
		String _bankLocalYzfFilePath = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.bank.local.yzffilepath"),dateStr);
		//翼支付文件名
		String _yzfFileName = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.yzf.fileName"),dateStr);//根据日期解析文件名date
				
		//本地中金对账文件存放路径
		String _bankLocalZjFilePath = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.bank.local.zjfilepath"),dateStr);
		//中金文件名
		String _zjFileName = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.zj.fileName"),dateStr);//根据日期解析文件名date
		logger.info("-----判断本地文件["+_bankLocalYzfFilePath+_yzfFileName+"]是否存在-----");
		if(!FileUtils.checkLocalFileExist(_bankLocalYzfFilePath+_yzfFileName)){
			//如果本地对账文件不存在 则调用外联接口取翼支付ftp文件至本行ftp服务器
			logger.info("-----本地文件["+_bankLocalYzfFilePath+_yzfFileName+"]不存在,开始走FTP外联接口获取翼支付FTP文件-----");
			try {
				String yzfCode =PropertyUtils.getValue("com.sunrun.bill.olp.yzf.code");
				String yzfCode1 = yzfCode.substring(0,1);
				String yzfCode2 = yzfCode.substring(1);
				String resString = FtpClient.ftpTask(yzfCode1,yzfCode2, _yzfFileName, dateStr, "",
						PropertyUtils.getValue("com.sunrun.bill.olp.ip"),
						Integer.valueOf(PropertyUtils.getValue("com.sunrun.bill.olp.port")),
						PropertyUtils.getValue("com.sunrun.bill.olp.timeout"));
				logger.info("-----外联接口返回报文 ["+resString+" ]-----");
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		 
		if(!FileUtils.checkLocalFileExist(_bankLocalZjFilePath+_zjFileName)){
			//如果本地对账文件不存在 则调用外联接口取中金ftp文件至本行ftp服务器
			logger.info("-----本地文件["+_bankLocalZjFilePath+_zjFileName+"]不存在,开始走FTP外联接口获取中金FTP文件-----");
			try {
				String zjCode =PropertyUtils.getValue("com.sunrun.bill.olp.zj.code");
				String zjCode1 = zjCode.substring(0,1);
				String zjCode2 = zjCode.substring(1);
				String resString = FtpClient.ftpTask(zjCode1,zjCode2, _zjFileName, dateStr, dateStr,
						PropertyUtils.getValue("com.sunrun.bill.olp.ip"),
						Integer.valueOf(PropertyUtils.getValue("com.sunrun.bill.olp.port")),
						PropertyUtils.getValue("com.sunrun.bill.olp.timeout"));
				logger.info("-----外联接口返回报文 ["+resString+"]-----");
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		
	}
	
}

