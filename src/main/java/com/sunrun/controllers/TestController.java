package com.sunrun.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sunrun.mpos.common.utils.PropertyUtils;
import com.sunrun.util.SftpUtils;

/**
 * 测试类
 *
 * @author liuwen
 * @since V1.0.0
 */
@Controller
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @ResponseBody
    @RequestMapping("/testsftp")
    public void execute(HttpServletRequest req) {
        // http://localhost:8080/withholding-bill/testsftp?downloadPath=/upload/bill/20170101/yzf/&downloadFile=QYZH_0000000000015061_20170101.txt&saveFilePath=/data/&saveFileName=QYZH_0000000000015061_20170101.txt
        String downloadPath = PropertyUtils.getValue("downloadPath");// /upload/bill/20170101/yzf/
        String downloadFile = PropertyUtils.getValue("downloadFile");// QYZH_0000000000015061_20170101.txt
        String saveFilePath = PropertyUtils.getValue("saveFilePath");// E:\\
        String saveFileName = PropertyUtils.getValue("saveFileName");// QYZH_0000000000015061_20170101.txt
        if (StringUtils.isBlank(downloadPath) || StringUtils.isBlank(downloadFile) || StringUtils.isBlank(saveFilePath)
            || StringUtils.isBlank(saveFileName)) {
            logger.info("downloadPath,downloadFile,saveFilePath,saveFileName都不能为空");
            return;
        }
        logger.info("downloadPath={},downloadFile={},saveFilePath={},saveFileName={}", downloadPath, downloadFile,
            saveFilePath, saveFileName);
        String _sftpHost = PropertyUtils.getValue("com.sunrun.bill.sftp.host");
        String _sftpPort = PropertyUtils.getValue("com.sunrun.bill.sftp.port");
        String _sftpName = PropertyUtils.getValue("com.sunrun.bill.sftp.name");
        String _sftpPsw = PropertyUtils.getValue("com.sunrun.bill.sftp.psw");
        SftpUtils sftpUtils = new SftpUtils(_sftpHost, Integer.valueOf(_sftpPort), _sftpName, _sftpPsw);
        try {
            sftpUtils.connectRSA(90000);
            sftpUtils.downloadAndSave(downloadPath, downloadFile, saveFilePath, saveFileName);
        } catch (Exception e) {
            logger.error("测试sftp下载异常", e);
        } finally {
            sftpUtils.logOut();
        }

    }
}
