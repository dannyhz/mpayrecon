package com.sunrun.controllers;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sunrun.bill.model.MerchantDO;
import com.sunrun.bill.service.IMerchantService;
import com.sunrun.mpos.common.utils.DateUtils;
import com.sunrun.mpos.common.utils.FileUtils;
import com.sunrun.mpos.common.utils.PropertyUtils;
import com.sunrun.util.FtpClient;

/**
 * 通过外联接口将行内FTP服务器的对账单文件放到商户FTP服务器
 *
 * @author liuwen
 * @since V1.0.0
 */
@Controller
public class DkPutBillfileController {
    private static final Logger logger = LoggerFactory.getLogger(DkPutBillfileController.class);

    @Autowired
    private IMerchantService merchantService;

    @ResponseBody
    @RequestMapping("/putFtpFileApi")
    public void putFileToMerchantFTP(HttpServletRequest req) {
        String reqDateStr = req.getParameter("date");
        Date billDate = null;
        // 如果传入日期 则去对账传入日期的帐，如果不传入日期则默认去对昨天的帐
        if (!StringUtils.isBlank(reqDateStr)) {
            try {
                billDate = DateUtils.parse(reqDateStr, "yyyyMMdd");
            } catch (Exception e) {
                logger.error("对账日期解析异常,reqDateStr-{}", reqDateStr, e);
                return;
            }
        } else {
            billDate = DateUtils.minusDay(new Date(), 1);
        }

        // 所有渠道对完帐，才能向上推送
        Date billDateAddOneday = DateUtils.addDay(billDate, 1);// 对账日+1
        String yzfFlag = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.yzf.controlFlag"),
            DateUtils.formatDate(billDateAddOneday, "yyyyMMdd"));
        String zjFlag = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.zj.controlFlag"),
            DateUtils.formatDate(billDateAddOneday, "yyyyMMdd"));
        String tlFlag = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.tl.controlFlag"),
            DateUtils.formatDate(billDateAddOneday, "yyyyMMdd"));
        boolean yzfBool = FileUtils.checkLocalFileExist(yzfFlag);
        boolean zjBool = FileUtils.checkLocalFileExist(zjFlag);
        boolean tlBool = FileUtils.checkLocalFileExist(tlFlag);

        if (!yzfBool || !zjBool || !tlBool) {
            logger.info("对账日期,billDate-{},渠道未全部对账,不能上传商户sftp文件,不能外联推送,翼支付-{},中金-{},通联-{}",
                DateUtils.format(billDate, "yyyyMMdd"), yzfBool, zjBool, tlBool);
            return;
        }
        logger.info("对账日期,billDate-{}", DateUtils.format(billDate, "yyyyMMdd"));
        List<MerchantDO> merchantList = merchantService.queryAll();
        if (null == merchantList || merchantList.size() == 0) {
            logger.error("将行内FTP服务器的对账单文件放到商户FTP服务器前，查询商户为空");
            return;
        }
        String billDateStr = DateUtils.format(billDate, "yyyyMMdd");
        String billDateAddOneDayStr = DateUtils.formatDate(DateUtils.addDay(billDate, 1), "yyyyMMdd");
        for (MerchantDO merchantDO : merchantList) {
            // olpSwitch--- 商户的外联开关，控制向商户FTP服务器发送对账文件
            // olpFlag--- 调用外联本地标记 如果有此标记 表示当天已发起过外联请求 不用重复发送,唯一
            if (StringUtils.isBlank(merchantDO.getOlpFlag())) {
                logger.info("商户{},外联本地标记为空,不能向该商户发送对账文件", merchantDO.getMchtNo());
                continue;
            }
            String olpFlag = MessageFormat.format(
                String.format(PropertyUtils.getValue("com.sunrun.bill.olp.flag.merchant"), merchantDO.getOlpFlag()),
                billDateAddOneDayStr);
            // 本地商户对账文件目录
            String localMerchantFilePath = MessageFormat
                .format(PropertyUtils.getValue("com.sunrun.bill.bank.local.fqMergeFilePath"), billDateStr);
            String fileName = merchantDO.getMchtNo() + "_" + billDateStr + ".csv";
            if (StringUtils.isNotBlank(merchantDO.getOlpCode())
                && FileUtils.checkLocalFileExist(localMerchantFilePath + fileName)
                && !FileUtils.checkLocalFileExist(olpFlag)) {
                try {
                    FileUtils.createLocalFile(olpFlag);
                    Thread.sleep(45000);// 延迟45秒以防发起外联请求时对账文件没有上传完成
                    logger.info("-----本地文件{}存在,开始走FTP外联接口将行内FTP文件上传至商户{}-{}FTP服务器-----",
                        localMerchantFilePath + fileName, merchantDO.getMchtName(), merchantDO.getMchtNo());
                    String code = merchantDO.getOlpCode();
                    if (StringUtils.isBlank(code)) {
                        logger.error("商户{}-{}外联编码为空", merchantDO.getMchtName(), merchantDO.getMchtNo());
                        throw new Exception(
                            String.format("商户%s-%s外联编码为空", merchantDO.getMchtName(), merchantDO.getMchtNo()));
                    }
                    String code1 = code.substring(0, 1);
                    String code2 = code.substring(1);
                    logger.info(
                        "外联推送接口请求参数:std400trcd={},std400num={},fileName={},srcpath={},destpath={},ip={},port={},timeout={}",
                        code1, code2, fileName, billDateStr, billDateStr,
                        PropertyUtils.getValue("com.sunrun.bill.olp.ip"),
                        PropertyUtils.getValue("com.sunrun.bill.olp.port"),
                        PropertyUtils.getValue("com.sunrun.bill.olp.timeout"));
                    String resString = FtpClient.ftpTask(code1, code2, fileName, billDateStr, billDateStr,
                        PropertyUtils.getValue("com.sunrun.bill.olp.ip"),
                        Integer.valueOf(PropertyUtils.getValue("com.sunrun.bill.olp.port")),
                        PropertyUtils.getValue("com.sunrun.bill.olp.timeout"));
                    logger.info("-----外联接口返回报文 {}-----", resString);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    FileUtils.deleteLocalFile(olpFlag);
                }
            } else {
                logger.info("商户{},外联编码为空或者本地文件{}不存在或者调用外联本地标记{}已存在", merchantDO.getMchtNo(),
                    localMerchantFilePath + fileName, olpFlag);
            }
        }
    }
}
