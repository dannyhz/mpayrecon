package com.sunrun.controllers;

import java.text.MessageFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sunrun.mpos.common.utils.DateUtils;
import com.sunrun.mpos.common.utils.FileUtils;
import com.sunrun.mpos.common.utils.PropertyUtils;
import com.sunrun.util.FtpClient;

/**
 * 通过外联接口将渠道的对账单文件获取到行内ftp服务器
 *
 * @author liuwen
 * @since V1.0.0
 */
@Controller
public class DkGetBillfileController {
    private static final Logger logger = LoggerFactory.getLogger(DkGetBillfileController.class);

    @ResponseBody
    @RequestMapping("/getFtpFileApi")
    public void getChannelFile(HttpServletRequest req) {
        String dateStr = "";
        String reqDateStr = req.getParameter("date");
        if (reqDateStr != null) {
            logger.info("传入的日期=[" + reqDateStr + "]");
            dateStr = reqDateStr;
        } else {
            Date date = new Date();
            dateStr = DateUtils.formatDate(DateUtils.minusDay(date, 1), "yyyyMMdd");
        }
        // 本地翼支付对账文件存放路径
        String _bankLocalYzfFilePath = MessageFormat
            .format(PropertyUtils.getValue("com.sunrun.bill.bank.local.yzffilepath"), dateStr);
        // 翼支付文件名
        String _yzfFileName = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.yzf.fileName"), dateStr);// 根据日期解析文件名date

        // 本地中金对账文件存放路径
        String _bankLocalZjFilePath = MessageFormat
            .format(PropertyUtils.getValue("com.sunrun.bill.bank.local.zjfilepath"), dateStr);
        // 中金文件名
        String _zjFileName = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.zj.fileName"), dateStr);// 根据日期解析文件名date
        // 通联
        // 本地通联对账文件存放路径
        String _bankLocalTlFilePath = MessageFormat
            .format(PropertyUtils.getValue("com.sunrun.bill.bank.local.tlfilepath"), dateStr);
        // 通联文件名
        String _tlFileName = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.tl.fileName"), dateStr);// 根据日期解析文件名date

        logger.info("-----判断本地文件[" + _bankLocalYzfFilePath + _yzfFileName + "]是否存在-----");
        if (!FileUtils.checkLocalFileExist(_bankLocalYzfFilePath + _yzfFileName)) {
            // 如果本地对账文件不存在 则调用外联接口取翼支付ftp文件至本行ftp服务器
            logger.info("-----本地文件[" + _bankLocalYzfFilePath + _yzfFileName + "]不存在,开始走FTP外联接口获取翼支付FTP文件-----");
            try {
                String yzfCode = PropertyUtils.getValue("com.sunrun.bill.olp.yzf.code");
                String yzfCode1 = yzfCode.substring(0, 1);
                String yzfCode2 = yzfCode.substring(1);
                logger.info(
                    "外联获取渠道对账文件接口请求参数:std400trcd={},std400num={},fileName={},srcpath={},destpath={},ip={},port={},timeout={}",
                    yzfCode1, yzfCode2, _yzfFileName, dateStr, "", PropertyUtils.getValue("com.sunrun.bill.olp.ip"),
                    PropertyUtils.getValue("com.sunrun.bill.olp.port"),
                    PropertyUtils.getValue("com.sunrun.bill.olp.timeout"));
                String resString = FtpClient.ftpTask(yzfCode1, yzfCode2, _yzfFileName, dateStr, "",
                    PropertyUtils.getValue("com.sunrun.bill.olp.ip"),
                    Integer.valueOf(PropertyUtils.getValue("com.sunrun.bill.olp.port")),
                    PropertyUtils.getValue("com.sunrun.bill.olp.timeout"));
                logger.info("-----外联接口返回报文 [" + resString + " ]-----");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            logger.info("翼支付对账文件本地{}已存在", _bankLocalYzfFilePath + _yzfFileName);
        }

        if (!FileUtils.checkLocalFileExist(_bankLocalZjFilePath + _zjFileName)) {
            // 如果本地对账文件不存在 则调用外联接口取中金ftp文件至本行ftp服务器
            logger.info("-----本地文件[" + _bankLocalZjFilePath + _zjFileName + "]不存在,开始走FTP外联接口获取中金FTP文件-----");
            try {
                String zjCode = PropertyUtils.getValue("com.sunrun.bill.olp.zj.code");
                String zjCode1 = zjCode.substring(0, 1);
                String zjCode2 = zjCode.substring(1);
                logger.info(
                    "外联获取渠道对账文件接口请求参数:std400trcd={},std400num={},fileName={},srcpath={},destpath={},ip={},port={},timeout={}",
                    zjCode1, zjCode2, _zjFileName, dateStr, dateStr, PropertyUtils.getValue("com.sunrun.bill.olp.ip"),
                    PropertyUtils.getValue("com.sunrun.bill.olp.port"),
                    PropertyUtils.getValue("com.sunrun.bill.olp.timeout"));
                String resString = FtpClient.ftpTask(zjCode1, zjCode2, _zjFileName, dateStr, dateStr,
                    PropertyUtils.getValue("com.sunrun.bill.olp.ip"),
                    Integer.valueOf(PropertyUtils.getValue("com.sunrun.bill.olp.port")),
                    PropertyUtils.getValue("com.sunrun.bill.olp.timeout"));
                logger.info("-----外联接口返回报文 [" + resString + "]-----");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            logger.info("中金对账文件本地{}已存在", _bankLocalZjFilePath + _zjFileName);
        }

        // 通联
        if (!FileUtils.checkLocalFileExist(_bankLocalTlFilePath + _tlFileName)) {
            // 如果本地对账文件不存在 则调用外联接口取通联ftp文件至本行ftp服务器
            logger.info("-----本地文件[" + _bankLocalTlFilePath + _tlFileName + "]不存在,开始走FTP外联接口获取通联FTP文件-----");
            try {
                String tlCode = PropertyUtils.getValue("com.sunrun.bill.olp.tl.code");
                String tlCode1 = tlCode.substring(0, 1);
                String tlCode2 = tlCode.substring(1);
                logger.info(
                    "外联获取渠道对账文件接口请求参数:std400trcd={},std400num={},fileName={},srcpath={},destpath={},ip={},port={},timeout={}",
                    tlCode1, tlCode2, _tlFileName, dateStr, dateStr, PropertyUtils.getValue("com.sunrun.bill.olp.ip"),
                    PropertyUtils.getValue("com.sunrun.bill.olp.port"),
                    PropertyUtils.getValue("com.sunrun.bill.olp.timeout"));
                String resString = FtpClient.ftpTask(tlCode1, tlCode2, _tlFileName, dateStr, dateStr,
                    PropertyUtils.getValue("com.sunrun.bill.olp.ip"),
                    Integer.valueOf(PropertyUtils.getValue("com.sunrun.bill.olp.port")),
                    PropertyUtils.getValue("com.sunrun.bill.olp.timeout"));
                logger.info("-----外联接口返回报文 [" + resString + "]-----");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            logger.info("通联对账文件本地{}已存在", _bankLocalTlFilePath + _tlFileName);
        }
    }
}
