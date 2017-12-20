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

import com.sunrun.bill.compare.ChannelBill;
import com.sunrun.bill.model.MerchantDO;
import com.sunrun.bill.service.IMerchantService;
import com.sunrun.mpos.common.utils.DateUtils;
import com.sunrun.mpos.common.utils.FileUtils;
import com.sunrun.mpos.common.utils.PropertyUtils;
import com.sunrun.util.FtpClient;
import com.sunrun.util.SftpUtils;

/**
 * 对账控制类.
 *
 * @author liuwen
 * @since V1.0.0
 */
@Controller
public class BillJobController {
    private static final Logger logger = LoggerFactory.getLogger(BillJobController.class);

    @Autowired
    private List<ChannelBill> channelBillList;

    @Autowired
    private IMerchantService merchantService;

    @ResponseBody
    @RequestMapping("/billJobApi")
    public void execute(HttpServletRequest req) {
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
        // 判断sftp上的渠道对账文件是否全部齐全,不齐全不进行对账
        // 行内SFTP配置
        String _sftpHost = PropertyUtils.getValue("com.sunrun.bill.sftp.host");
        String _sftpPort = PropertyUtils.getValue("com.sunrun.bill.sftp.port");
        String _sftpName = PropertyUtils.getValue("com.sunrun.bill.sftp.name");
        String _sftpPsw = PropertyUtils.getValue("com.sunrun.bill.sftp.psw");
        SftpUtils sftpUtils = new SftpUtils(_sftpHost, Integer.valueOf(_sftpPort), _sftpName, _sftpPsw);
        String yzfChannelSftpFilePath = getBillDateSftpPath(PropertyUtils.getValue("com.sunrun.bill.sftp.yzf.filepath"),
            billDate);
        String yzfFileName = getBillDateFileName(PropertyUtils.getValue("com.sunrun.bill.yzf.fileName"), billDate);

        String zjChannelSftpFilePath = getBillDateSftpPath(PropertyUtils.getValue("com.sunrun.bill.sftp.zj.filepath"),
            billDate);
        String zjFileName = getBillDateFileName(PropertyUtils.getValue("com.sunrun.bill.zj.fileName"), billDate);

        // 通联
        String tlChannelSftpFilePath = getBillDateSftpPath(PropertyUtils.getValue("com.sunrun.bill.sftp.tl.filepath"),
            billDate);
        String tlFileName = getBillDateFileName(PropertyUtils.getValue("com.sunrun.bill.tl.fileName"), billDate);

        logger.info("对账日期,billDate-{},检查渠道翼支付对账文件在sftp-{}上是否存在", DateUtils.format(billDate, "yyyyMMdd"),
            yzfChannelSftpFilePath + yzfFileName);

        // 检查翼支付对账文件在sftp是否存在
        try {
            sftpUtils.connectRSA(90000);
            if (!sftpUtils.checkFileExists(yzfChannelSftpFilePath, yzfFileName)) {
                logger.error("对账日期,billDate-{},渠道翼支付对账文件在sftp-{}上不存在!", DateUtils.format(billDate, "yyyyMMdd"),
                    yzfChannelSftpFilePath + yzfFileName);
                return;
            }
        } catch (Exception e) {
            logger.error("对账日期,billDate-{},检查渠道翼支付对账文件在sftp-{}上是否存在,发生异常", DateUtils.format(billDate, "yyyyMMdd"),
                yzfChannelSftpFilePath + yzfFileName, e);
            return;
        } finally {
            sftpUtils.logOut();
        }

        logger.info("对账日期,billDate-{},检查渠道中金对账文件在sftp-{}上是否存在", DateUtils.format(billDate, "yyyyMMdd"),
            zjChannelSftpFilePath + zjFileName);

        // 检查中金对账文件在sftp是否存在
        try {
            sftpUtils.connectRSA(90000);
            if (!sftpUtils.checkFileExists(zjChannelSftpFilePath, zjFileName)) {
                logger.error("对账日期,billDate-{},渠道中金对账文件在sftp-{}上不存在!", DateUtils.format(billDate, "yyyyMMdd"),
                    zjChannelSftpFilePath + zjFileName);
                return;
            }
        } catch (Exception e) {
            logger.error("对账日期,billDate-{},检查渠道中金对账文件在sftp-{}上是否存在,发生异常", DateUtils.format(billDate, "yyyyMMdd"),
                zjChannelSftpFilePath + zjFileName, e);
            return;
        } finally {
            sftpUtils.logOut();
        }
        logger.info("对账日期,billDate-{},检查渠道通联对账文件在sftp-{}上是否存在", DateUtils.format(billDate, "yyyyMMdd"),
            tlChannelSftpFilePath + tlFileName);

        // 检查通联对账文件在sftp是否存在
        try {
            sftpUtils.connectRSA(90000);
            if (!sftpUtils.checkFileExists(tlChannelSftpFilePath, tlFileName)) {
                logger.error("对账日期,billDate-{},渠道通联对账文件在sftp-{}上不存在!", DateUtils.format(billDate, "yyyyMMdd"),
                    tlChannelSftpFilePath + tlFileName);
                return;
            }
        } catch (Exception e) {
            logger.error("对账日期,billDate-{},检查渠道通联对账文件在sftp-{}上是否存在,发生异常", DateUtils.format(billDate, "yyyyMMdd"),
                tlChannelSftpFilePath + tlFileName, e);
            return;
        } finally {
            sftpUtils.logOut();
        }

        logger.info("对账日期,billDate-{},当前需要对账的渠道个数-{}", DateUtils.format(billDate, "yyyyMMdd"), channelBillList.size());
        List<MerchantDO> merchantList = merchantService.queryAll();
        if (null == merchantList || merchantList.size() == 0) {
            logger.error("渠道对账前，查询商户为空");
            return;
        }
        try {
            // 查询商户，给所有商户创建平账文件，生成平账对账单;
            createAllMerchantFile(billDate, merchantList);
        } catch (Exception e) {
            logger.error("渠道对账前，给所有商户创建平账文件异常", e);
            return;
        }
        // 循环渠道对账
        for (ChannelBill channel : channelBillList) {
            channel.core(billDate);
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

        // 上传商户对账文件到sftp
        uploadMerchantFileToSftp(billDate, merchantList);
        // 将sftp上的文件 通过外联分别推送到商户
        putFileToMerchantFTP(billDate, merchantList);

    }

    /**
     * 将sftp上的文件 通过外联分别推送到商户
     *
     * @param billDate
     */
    private void putFileToMerchantFTP(Date billDate, List<MerchantDO> merchantList) {

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
                    logger.info("商户{}-{},外联编码为{}", merchantDO.getMchtName(), merchantDO.getMchtNo(), code);
                    String resString = FtpClient.ftpTask(code1, code2, fileName, billDateStr, billDateStr,
                        PropertyUtils.getValue("com.sunrun.bill.olp.ip"),
                        Integer.valueOf(PropertyUtils.getValue("com.sunrun.bill.olp.port")),
                        PropertyUtils.getValue("com.sunrun.bill.olp.timeout"));
                    logger.info("-----外联接口返回报文 {}-----", resString);
                } catch (Exception e) {
                    logger.error("-----外联推送文件{}异常,走FTP外联接口将行内FTP文件上传至商户{}-{}FTP服务器-----",
                        localMerchantFilePath + fileName, merchantDO.getMchtName(), merchantDO.getMchtNo(), e);
                    FileUtils.deleteLocalFile(olpFlag);
                }
            } else {
                logger.info("商户{},外联编码为空或者本地文件{}不存在或者调用外联本地标记{}已存在", merchantDO.getMchtNo(),
                    localMerchantFilePath + fileName, olpFlag);
            }
        }
    }

    /**
     * 上传商户对账文件
     * 
     */
    private void uploadMerchantFileToSftp(Date billDate, List<MerchantDO> merchantList) {
        // 行内SFTP配置
        String _sftpHost = PropertyUtils.getValue("com.sunrun.bill.sftp.host");
        String _sftpPort = PropertyUtils.getValue("com.sunrun.bill.sftp.port");
        String _sftpName = PropertyUtils.getValue("com.sunrun.bill.sftp.name");
        String _sftpPsw = PropertyUtils.getValue("com.sunrun.bill.sftp.psw");
        SftpUtils sftpUtils = new SftpUtils(_sftpHost, Integer.valueOf(_sftpPort), _sftpName, _sftpPsw);

        String billDateStr = DateUtils.format(billDate, "yyyyMMdd");
        // 本地商户对账文件目录
        String localMerchantFilePath = MessageFormat
            .format(PropertyUtils.getValue("com.sunrun.bill.bank.local.fqMergeFilePath"), billDateStr);

        for (MerchantDO merchantDO : merchantList) {
            // 判断商户是否有sftp路径配置
            if (StringUtils.isEmpty(merchantDO.getSftpFilePath())) {
                logger.info("商户{}-{},对账地址为空", merchantDO.getMchtName(), merchantDO.getMchtNo());
                continue;
            }

            // 替换日期占位符,例如./result/ws/{0}/
            String sftpFilePath = MessageFormat.format(
                String.format(PropertyUtils.getValue("com.sunrun.bill.sftp.merchant"), merchantDO.getSftpFilePath()),
                billDateStr);
            String fileName = merchantDO.getMchtNo() + "_" + billDateStr + ".csv";
            // 先检查是否有待上传的本地文件 再检查sftp上是否已存在
            if (FileUtils.checkLocalFileExist(localMerchantFilePath + fileName)) {
                try {
                    sftpUtils.connectRSA(90000);
                    if (!sftpUtils.checkFileExists(sftpFilePath, fileName)) {
                        logger.info("===== sftp服务器不存在平账文件,开始上传 [ " + sftpFilePath + fileName + " ]=====");
                        sftpUtils.upload(sftpFilePath, localMerchantFilePath + fileName);
                    }
                } catch (Exception e) {
                    logger.error("向sftp服务器上传商户-{}对账文件异常", merchantDO.getMchtNo(), e);
                } finally {
                    sftpUtils.logOut();
                }
            }
        }

    }

    /**
     * 给所有商户创建平账文件，生成平账对账单;
     * 
     * @throws Exception
     */
    private void createAllMerchantFile(Date billDate, List<MerchantDO> merchantList) throws Exception {
        for (MerchantDO merchantDO : merchantList) {
            // 合并之后的文件路径
            String _mergeFilePath = MessageFormat.format(
                PropertyUtils.getValue("com.sunrun.bill.bank.local.fqMergeFilePath"),
                DateUtils.format(billDate, "yyyyMMdd"));
            // 写进文件
            FileUtils.writeFile(_mergeFilePath,
                merchantDO.getMchtNo() + "_" + DateUtils.format(billDate, "yyyyMMdd") + ".csv", "");
        }
        logger.info("对账日期,billDate-{},生成商户平账文件的个数-{}", DateUtils.format(billDate, "yyyyMMdd"), merchantList.size());

    }

    private String getBillDateSftpPath(String sftpPath, Date billDate) {
        return MessageFormat.format(sftpPath, DateUtils.formatDate(billDate, "yyyyMMdd"));
    }

    protected String getBillDateFileName(String fileName, Date billDate) {
        return MessageFormat.format(fileName, DateUtils.formatDate(billDate, "yyyyMMdd"));
    }
}
