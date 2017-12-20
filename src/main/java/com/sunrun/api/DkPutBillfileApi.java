package com.sunrun.api;

import java.text.MessageFormat;
import java.text.ParseException;
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

public class DkPutBillfileApi extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(DkPutBillfileApi.class);
	
	//网商/支付宝/聚有财 商户号
	private	static final String mchtNoWS = PropertyUtils.getValue("com.sunrun.bill.orders.mchtNo.ws");
	private static final String mchtNoZFB = PropertyUtils.getValue("com.sunrun.bill.orders.mchtNo.zfb");
	private static final String mchtNoJYC = PropertyUtils.getValue("com.sunrun.bill.orders.mchtNo.jyc");
	private static final String olpSwitchWS = PropertyUtils.getValue("com.sunrun.bill.olp.switch.ws");
	private static final String olpSwitchZFB = PropertyUtils.getValue("com.sunrun.bill.olp.switch.zfb");
	private static final String olpSwitchJYC = PropertyUtils.getValue("com.sunrun.bill.olp.switch.jyc");
			
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		putFile(req);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		putFile(req);
	}
	 
	/**
	 * 通过外联接口将行内FTP服务器的对账单文件放到渠道FTP服务器（网商、支付宝）
	 */
	private void putFile(HttpServletRequest req){
		String dateStr ="";
		String reqDateStr = req.getParameter("date");
		String flagDateStr = "";//标记日期  
		if(reqDateStr!=null){
			logger.info("传入的日期=["+reqDateStr+"]");
			dateStr=reqDateStr;
			try {
				flagDateStr=DateUtils.formatDate(DateUtils.addDay(DateUtils.parse(dateStr,"yyyyMMdd"),1),"yyyyMMdd");
			} catch (ParseException e) {
				logger.error(e.getMessage(),e);
			}
		}else{
			Date date = new Date();
			dateStr = DateUtils.formatDate(DateUtils.minusDay(date, 1), "yyyyMMdd");
			flagDateStr = DateUtils.formatDate(date, "yyyyMMdd");
		}
		
		//对账单文件本地备份
		String _mergeFilePath = 
				MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.bank.local.fqMergeFilePath"),dateStr);
		//合并后的文件名称 网商/支付宝/聚有财   例如 8100000000000001_20170101.csv
		String _mergeFileNameWS = mchtNoWS+"_"+dateStr+".csv";
		String _mergeFileNameZFB = mchtNoZFB+"_"+dateStr+".csv";
		String _mergeFileNameJYC = mchtNoJYC+"_"+dateStr+".csv";
//		String _mergeFileNameWS = mchtNoWS+dateStr+".csv";
//		String _mergeFileNameZFB = mchtNoZFB+dateStr+".csv";
//		String _mergeFileNameJYC = mchtNoJYC+dateStr+".csv";
		
		//调用外联本地标记 如果有此标记 表示当天已发起过外联请求 不用重复发送
		String _olpFlagWS = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.olp.flag.ws")
				,flagDateStr);
		String _olpFlagZFB = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.olp.flag.zfb")
				,flagDateStr);
		String _olpFlagJYC = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.olp.flag.jyc")
				,flagDateStr); 
		
		if(olpSwitchWS.equals("1")
				&&FileUtils.checkLocalFileExist(_mergeFilePath+_mergeFileNameWS) 
				&& !FileUtils.checkLocalFileExist(_olpFlagWS)){
			//（网商）如果本地对账文件存在并且外联调用标记不存在 则发送外联请求将文件从行内FTP服务器放到渠道方的FTP服务器
			try {
				FileUtils.createLocalFile(_olpFlagWS);
				Thread.sleep(45000);//延迟45秒以防发起外联请求时对账文件没有上传完成
				logger.info("-----(网商)本地文件["+_mergeFilePath+_mergeFileNameWS+"]不存在,开始走FTP外联接口将行内FTP文件上传至网商FTP服务器-----");
			 
				String wsCode =PropertyUtils.getValue("com.sunrun.bill.olp.ws.code");
				String wsCode1 = wsCode.substring(0,1);
				String wsCode2 = wsCode.substring(1);
				String resString = FtpClient.ftpTask(wsCode1, wsCode2,_mergeFileNameWS, dateStr, dateStr,
						PropertyUtils.getValue("com.sunrun.bill.olp.ip"),
						Integer.valueOf(PropertyUtils.getValue("com.sunrun.bill.olp.port")),
						PropertyUtils.getValue("com.sunrun.bill.olp.timeout"));
				logger.info("-----外联接口返回报文 ["+resString+"]-----");
			 
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				FileUtils.deleteLocalFile(_olpFlagWS);
			}
			
		}
		 
		if(olpSwitchZFB.equals("1")
				&&FileUtils.checkLocalFileExist(_mergeFilePath+_mergeFileNameZFB)
				&& !FileUtils.checkLocalFileExist(_olpFlagZFB)){
			//（支付宝）如果本地对账文件存在并且外联调用标记不存在 则发送外联请求将文件从行内FTP服务器放到渠道方的FTP服务器
			try {
				FileUtils.createLocalFile(_olpFlagZFB);
				Thread.sleep(45000);//延迟45秒以防发起外联请求时对账文件没有上传完成
				logger.info("-----(支付宝)本地文件["+_mergeFilePath+_mergeFileNameZFB+"]不存在,开始走FTP外联接口将行内FTP文件上传至支付宝FTP服务器-----");
				
				String zfbCode =PropertyUtils.getValue("com.sunrun.bill.olp.zfb.code");
				String zfbCode1 = zfbCode.substring(0,1);
				String zfbCode2 = zfbCode.substring(1);
				String resString = FtpClient.ftpTask(zfbCode1,zfbCode2, _mergeFileNameZFB, dateStr, dateStr,
						PropertyUtils.getValue("com.sunrun.bill.olp.ip"),
						Integer.valueOf(PropertyUtils.getValue("com.sunrun.bill.olp.port")),
						PropertyUtils.getValue("com.sunrun.bill.olp.timeout"));
				logger.info("-----外联接口返回报文 ["+resString+" ]-----");
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				FileUtils.deleteLocalFile(_olpFlagZFB);
			}
		}
		
		if(olpSwitchJYC.equals("1")
				&&FileUtils.checkLocalFileExist(_mergeFilePath+_mergeFileNameJYC)
				&& !FileUtils.checkLocalFileExist(_olpFlagJYC)){
			//（聚有财）如果本地对账文件存在并且外联调用标记不存在 则发送外联请求将文件从行内FTP服务器放到渠道方的FTP服务器
			try {
				FileUtils.createLocalFile(_olpFlagJYC);
				Thread.sleep(45000);//延迟45秒以防发起外联请求时对账文件没有上传完成
				logger.info("-----(聚有财)本地文件["+_mergeFilePath+_mergeFileNameJYC+"]不存在,开始走FTP外联接口将行内FTP文件上传至支付宝FTP服务器-----");
				String jycCode =PropertyUtils.getValue("com.sunrun.bill.olp.jyc.code");
				String jycCode1 = jycCode.substring(0,1);
				String jycCode2 = jycCode.substring(1);
				String resString = FtpClient.ftpTask(jycCode1,jycCode2, _mergeFileNameJYC, dateStr, dateStr,
						PropertyUtils.getValue("com.sunrun.bill.olp.ip"),
						Integer.valueOf(PropertyUtils.getValue("com.sunrun.bill.olp.port")),
						PropertyUtils.getValue("com.sunrun.bill.olp.timeout"));
				logger.info("-----外联接口返回报文 ["+resString+" ]-----");
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				FileUtils.deleteLocalFile(_olpFlagJYC);
			}
		}
		
	}
	 
}

