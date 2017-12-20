package com.sunrun.bill.compare.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.sunrun.bill.compare.ChannelBill;
import com.sunrun.bill.exception.BillCompareException;
import com.sunrun.bill.exception.BillDbOptException;
import com.sunrun.bill.exception.InitChannelException;
import com.sunrun.bill.model.BillControlDO;
import com.sunrun.bill.model.BillDetailDO;
import com.sunrun.bill.model.BillOrderDO;
import com.sunrun.bill.service.IBillService;
import com.sunrun.mpos.common.utils.DateUtils;
import com.sunrun.mpos.common.utils.FileUtils;
import com.sunrun.mpos.common.utils.PropertyUtils;
import com.sunrun.util.MapBytesEntry;
import com.sunrun.util.SftpUtils;

/**
 * 渠道对账抽象类
 *
 * @author liuwen
 * @since V1.0.0
 */
public abstract class AbstractChannelBill implements ChannelBill, InitializingBean {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractChannelBill.class);

    /**
     * ============================渠道本地数据start==================================
     */
    // 控制文件FLAG
    private String controlFlag;

    /** 渠道号 */
    private String channelCode;

    /** 渠道名称,用于日志显示 */
    private String channelName;

    /** 渠道本地目录 */
    private String localfilepath;

    /** 渠道对账文件名 */
    private String fileName;
    /**
     * ============================渠道本地数据end====================================
     * 
     */
    /**
     * ============================渠道sftp数据start================================
     */
    /** sftp上渠道目录 */
    private String sftpPath;
    // 行内SFTP配置
    private String sftpHost = PropertyUtils.getValue("com.sunrun.bill.sftp.host");
    private String sftpPort = PropertyUtils.getValue("com.sunrun.bill.sftp.port");
    private String sftpName = PropertyUtils.getValue("com.sunrun.bill.sftp.name");
    private String sftpPsw = PropertyUtils.getValue("com.sunrun.bill.sftp.psw");
    /**
     * ============================渠道sftp数据end==================================
     * 
     */

    /**
     * ============================渠道对账数据start================================
     */
    /** <商户号,对账单表> */
    protected Map<String, BillControlDO> billControlMap = new HashMap<String, BillControlDO>();

    /** 错账记录 */
    protected List<BillDetailDO> billDetailList = new ArrayList<BillDetailDO>();

    /** 数据库流水记录 */
    protected List<BillOrderDO> billOrderList = new ArrayList<BillOrderDO>();

    /** 平账记录 */
    protected Map<String, List<String>> balanceMap = new HashMap<String, List<String>>();

    /** 数据库流水记录 ，状态为未知,需要根据渠道做更新 */
    protected List<BillOrderDO> billOrderListOfNotSure = new ArrayList<BillOrderDO>();

    /**
     * ============================渠道对账数据end================================
     */
    private ExecutorService exc = Executors.newCachedThreadPool();

    @Autowired
    private IBillService iBillService;

    @Override
    public void core(final Date billDate) {

        try {

            // 根据渠道标记目录是否存在,判断是否要对账
            if (FileUtils.checkLocalFileExist(getBillDateControlFlag(billDate))) {
                logger.info("====={}-{}标记目录{}已存在,无需对账！=====", channelName, channelCode,
                    getBillDateControlFlag(billDate));
                return;
            } else {
                FileUtils.createLocalFile(getBillDateControlFlag(billDate));
                logger.info("====={}-{}标记目录{}创建成功,开始对账=====", channelName, channelCode,
                    getBillDateControlFlag(billDate));
            }
            // 获取文件和数据库数据
            final CountDownLatch latch = new CountDownLatch(2);

            Future<List<byte[]>> fileTask = (Future<List<byte[]>>) exc.submit(new Callable<List<byte[]>>() {
                @Override
                public List<byte[]> call() throws Exception {
                    List<byte[]> fileData = null;
                    try {
                        fileData = getAndParseFile(billDate);
                        logger.info("渠道{}-{},获取和解析对账文件成功", channelName, channelCode);
                    } catch (Exception e) {
                        logger.error("渠道{}-{},获取和解析对账文件异常", channelName, channelCode, e);
                        throw new Exception("获取和解析对账文件异常", e);
                    } finally {
                        latch.countDown();
                    }
                    return fileData;
                }

            });
            Future<Map<MapBytesEntry, byte[]>> dbTask = (Future<Map<MapBytesEntry, byte[]>>) exc
                .submit(new Callable<Map<MapBytesEntry, byte[]>>() {
                    @Override
                    public Map<MapBytesEntry, byte[]> call() throws Exception {
                        Map<MapBytesEntry, byte[]> dbData = null;
                        try {
                            dbData = queryDbMap(billDate, channelCode);
                            logger.info("渠道{}-{},获取数据库交易流水成功", channelName, channelCode);
                        } catch (Exception e) {
                            logger.error("渠道{}-{},获取数据库交易流水异常", channelName, channelCode, e);
                            throw new Exception("获取数据库交易流水异常", e);
                        } finally {
                            latch.countDown();
                        }
                        return dbData;
                    }

                });

            latch.await();
            logger.info("渠道{}-{},开始对账,==={}===", channelName, channelCode, System.currentTimeMillis());
            // 文件数据
            List<byte[]> fileData = fileTask.get();
            // 数据库数据
            Map<MapBytesEntry, byte[]> dbData = dbTask.get();

            if (null == fileData) {
                logger.info("渠道{}-{},对账文件解析为空.不对账，删除本地控制文件", channelName, channelCode);
                FileUtils.deleteLocalFile(getBillDateControlFlag(billDate));
                return;
            }
            // 对账
            reconciliation(fileData, dbData, billDate);

            // 往商户对账单文件写数据
            writeToFile(billDate);

            // 回写入数据库
            writeToDb();
        } catch (Exception e) {
            logger.error("渠道{}-{}对账异常,本地标记目录{}被删除,异常信息:{}", channelName, channelCode, getBillDateControlFlag(billDate),
                e.getMessage(), e);
            FileUtils.deleteLocalFile(getBillDateControlFlag(billDate));
        } finally {
            // 清空对账数据
            billControlMap.clear();
            billDetailList.clear();
            billOrderList.clear();
            balanceMap.clear();
            billOrderListOfNotSure.clear();
            logger.info("渠道{}-{}，清空billControl<商户号,对账单表>,billDetailList错账记录 ,billOrderList数据库流水记录", channelName,
                channelCode);
        }
    }

    /**
     * 对账
     *
     * @param fileData
     * @param dbData
     */
    protected abstract void reconciliation(List<byte[]> fileData, Map<MapBytesEntry, byte[]> dbMap, Date billDate)
        throws BillCompareException;

    /**
     * 对BillControlDO,累加数据库存在而文件不存在数量
     *
     * @param billControlDO
     */
    protected void addSurplusDbCount(BillControlDO billControlDO) {
        if (null == billControlDO.getDbBillsAccount()) {
            billControlDO.setDbBillsAccount(1l);
        } else {
            Long surplusDbCount = billControlDO.getDbBillsAccount();
            billControlDO.setDbBillsAccount(surplusDbCount + 1);
        }
    }

    /**
     * 对BillControlDO,累加错账笔数
     *
     * @param billControlDO
     */
    protected void addWrongCount(BillControlDO billControlDO) {
        if (null == billControlDO.getWrongBillsAccount()) {
            billControlDO.setWrongBillsAccount(1l);
        } else {
            Long wrongCount = billControlDO.getWrongBillsAccount();
            billControlDO.setWrongBillsAccount(wrongCount + 1);
        }
    }

    /**
     * 对BillControlDO,累加平账笔数
     *
     * @param billControlDO
     */
    protected void addBalanceCount(BillControlDO billControlDO) {
        if (null == billControlDO.getBalanceBillsAccount()) {
            billControlDO.setBalanceBillsAccount(1l);
        } else {
            Long balanceCount = billControlDO.getBalanceBillsAccount();
            billControlDO.setBalanceBillsAccount(balanceCount + 1);
        }
    }

    /**
     * 对BillControlDO,累加平账金额
     *
     * @param billControlDO
     * @param _amount
     */
    protected void addBalanceAmount(BillControlDO billControlDO, String _amount) {
        if (null == billControlDO.getBalanceAmount()) {
            billControlDO.setBalanceAmount(Long.valueOf(_amount));
        } else {
            Long balanceAmountStr = billControlDO.getBalanceAmount();
            billControlDO.setBalanceAmount(balanceAmountStr + Long.valueOf(_amount));
        }
    }

    /**
     * 对BillControlDO,累加交易金额
     *
     * @param billControlDO
     * @param _amount
     */
    protected void addTotalBillsAmount(BillControlDO billControlDO, String _amount) {
        if (null == billControlDO.getTotalBillsAmount()) {
            billControlDO.setTotalBillsAmount(Long.valueOf(_amount));
        } else {
            Long tradeAmount = billControlDO.getTotalBillsAmount();
            billControlDO.setTotalBillsAmount(tradeAmount + Long.valueOf(_amount));
        }
    }

    protected String getBillDateControlFlag(Date billDate) {
        Date billDateAddOneday = DateUtils.addDay(billDate, 1);// 对账日+1
        return MessageFormat.format(controlFlag, DateUtils.formatDate(billDateAddOneday, "yyyyMMdd"));
    }

    protected String getBillDateLocalfilepath(Date billDate) {
        return MessageFormat.format(localfilepath, DateUtils.formatDate(billDate, "yyyyMMdd"));
    }

    protected String getBillDateFileName(Date billDate) {
        return MessageFormat.format(fileName, DateUtils.formatDate(billDate, "yyyyMMdd"));
    }

    protected String getBillDateSftpPath(Date billDate) {
        return MessageFormat.format(sftpPath, DateUtils.formatDate(billDate, "yyyyMMdd"));
    }

    protected BillControlDO getOrPutBillControlDO(String _mcht_no, Date billDate, Long bat_id) {
        // 对账单表对象
        // 从billControlMap获取对账单表对象,没有则新建
        BillControlDO billControlDO = billControlMap.get(_mcht_no);
        if (null == billControlDO) {
            billControlDO = new BillControlDO();
            billControlDO.setMchtNo(_mcht_no);
            billControlDO.setBatId(bat_id);
            billControlDO.setChannelCode(channelCode);// 渠道号
            billControlDO.setBillDate(DateUtils.formatDate(billDate, "yyyyMMdd"));
            billControlDO.setFileName(getBillDateFileName(billDate));
            // 初始化数值为0
            billControlDO.setBalanceAmount(0l);
            billControlDO.setBalanceBillsAccount(0l);
            billControlDO.setDbBillsAccount(0l);
            billControlDO.setFileBillsAccount(0l);
            billControlDO.setTotalBillsAccount(0l);
            billControlDO.setTotalBillsAmount(0l);
            billControlDO.setWrongBillsAccount(0l);
            billControlMap.put(_mcht_no, billControlDO);
        } else {
            billControlDO = billControlMap.get(_mcht_no);
        }
        return billControlDO;
    }

    /**
     * 获取和解析对账文件
     * 
     * @throws Exception
     * 
     */
    protected List<byte[]> getAndParseFile(Date billDate) throws Exception {
        // 从sftp下载渠道对账文件到本地
        downloadFiletoLocal(billDate);
        // 解析渠道本地文件
        return parseChannelLocalFile(billDate);

    }

    /**
     * 从sftp下载渠道对账文件到本地.
     * 
     * @throws Exception
     */
    private void downloadFiletoLocal(Date billDate) throws Exception {
        SftpUtils sftpUtils = new SftpUtils(sftpHost, Integer.valueOf(sftpPort), sftpName, sftpPsw);
        logger.info("sftp信息,sftpHost={},sftpPort={},sftpName={},sftpPsw={}", sftpHost, sftpPort, sftpName, sftpPsw);
        try {

            String channelLocalFilePath = getBillDateLocalfilepath(billDate) + getBillDateFileName(billDate);
            String channelSftpFilePath = getBillDateSftpPath(billDate) + getBillDateFileName(billDate);
            if (!FileUtils.checkLocalFileExist(channelLocalFilePath)) {
                logger.info("=====渠道{}-{},从行内SFTP服务器{},下载文件到本地{}=====", channelName, channelCode, channelSftpFilePath,
                    channelLocalFilePath);
                sftpUtils.connectRSA(90000);
                if (!sftpUtils.downloadAndSave(getBillDateSftpPath(billDate), getBillDateFileName(billDate),
                    getBillDateLocalfilepath(billDate), getBillDateFileName(billDate))) {
                    FileUtils.deleteLocalFile(channelLocalFilePath);
                    logger.info("=====渠道{}-{},从行内SFTP服务器{},下载文件到本地{}失败,删除本地文件=====", channelName, channelCode,
                        channelSftpFilePath, channelLocalFilePath);
                    throw new Exception("从行内SFTP服务器,下载文件到本地失败");
                }
                logger.info("=====渠道{}-{},从行内SFTP服务器{},下载文件到本地{}成功=====", channelName, channelCode, channelSftpFilePath,
                    channelLocalFilePath);
            } else {
                logger.info("=====渠道{}-{},本地对账文件{}已存在，无需到sftp下载=====", channelName, channelCode, channelLocalFilePath);
            }

        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            sftpUtils.logOut();
        }

    }

    private List<byte[]> parseChannelLocalFile(Date billDate) throws Exception

    {
        InputStream inputStream = new FileInputStream(
            new File(getBillDateLocalfilepath(billDate) + getBillDateFileName(billDate)));
        if (inputStream.available() != 0) {
            return doParseChannelLocalFile(inputStream);
        } else {
            inputStream.close();
        }
        return new ArrayList<byte[]>();
    }

    /**
     * 解析渠道本地对账文件.
     *
     */
    protected abstract List<byte[]> doParseChannelLocalFile(InputStream inputStream) throws Exception;

    /**
     * 查询渠道数据库流水
     */
    protected Map<MapBytesEntry, byte[]> queryDbMap(Date billDate, String channelCode) throws BillDbOptException {
        HashMap<MapBytesEntry, byte[]> dbMap = null;
        List<String> billList = null;
        String _dateStr = DateUtils.formatDate(billDate, "yyyy-MM-dd");
        String _dateStrMin = _dateStr + " 00:00:00";
        String _dateStrMax = _dateStr + " 23:59:59";
        // 银行流水号标记
        // 3表示生产数据
        // 1表示非生产数据
        String trxIdFlag = PropertyUtils.getValue("com.sunrun.bill.trxidflag");
        try {
            logger.info("=====开始抽取数据库数据[date=" + _dateStr + ",channelCode=" + channelCode + "]=====");
            billList = iBillService.queryDataString(channelCode, _dateStrMin, _dateStrMax, trxIdFlag);// channelCode:
            dbMap = new HashMap<MapBytesEntry, byte[]>(billList.size());
            for (String billStr : billList) {
                if (StringUtils.isNotEmpty(billStr)) {
                    String tmpStr[] = billStr.split(",");
                    dbMap.put(new MapBytesEntry(tmpStr[0].getBytes("UTF-8")), tmpStr[1].getBytes("UTF-8"));
                }
            }
            logger.info("===== 数据库记录数=[ " + dbMap.size() + " ]条=====");
            logger.info("=====数据库数据抽取全部完成[date=" + _dateStr + ",channelCode=" + channelCode + "]=====");
        } catch (Exception e) {
            throw new BillDbOptException(e.getMessage(), e);
        } finally {
            billList = null;
        }
        return dbMap;
    };

    private void writeToFile(Date billDate) throws Exception {
        Set<Entry<String, List<String>>> mchtNosSet = balanceMap.entrySet();
        Iterator<Entry<String, List<String>>> mchtNos = mchtNosSet.iterator();
        // 循环平账记录，写进商户文件
        while (mchtNos.hasNext()) {
            Entry<String, List<String>> entry = mchtNos.next();
            String mchtNo = entry.getKey();
            List<String> contentList = entry.getValue();
            // 合并之后的文件路径
            String _mergeFilePath = MessageFormat.format(
                PropertyUtils.getValue("com.sunrun.bill.bank.local.fqMergeFilePath"),
                DateUtils.formatDate(billDate, "yyyyMMdd"));
            // 写进文件
            for (String content : contentList) {
                logger.info("渠道{}-{}对账,向平账文件写入商户{},内容{}", channelName, channelCode, mchtNo, content);
                FileUtils.writeFile(_mergeFilePath, mchtNo + "_" + DateUtils.formatDate(billDate, "yyyyMMdd") + ".csv",
                    content);
            }
        }

    }

    private void writeToDb() throws BillDbOptException {

        logger.info("=====渠道{}-{},开始往数据库回写对账数据 ,=====", channelName, channelCode);

        try {
            iBillService.saveBillData(billControlMap, billDetailList, billOrderList, billOrderListOfNotSure);
            logger.info("=====渠道{}-{},数据库回写完毕  =====", channelName, channelCode);
        } catch (Exception e) {
            logger.error("渠道{}-{}数据库回写异常", channelName, channelCode, e);
            throw new BillDbOptException(e.getMessage());
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化渠道参数
        doinit();
        logger.info("渠道初始化参数,channelName-{},channelCode-{},controlFlag-{},localfilepath-{},fileName-{},sftpPath-{}",
            channelName, channelCode, controlFlag, localfilepath, fileName, sftpPath);
        // 初始化校验
        doinitCheck();
    }

    /**
     * 渠道初始化校验
     */
    private void doinitCheck() throws Exception {
        if (StringUtils.isBlank(channelName) || StringUtils.isBlank(channelCode)) {
            throw new InitChannelException("渠道名称或渠道号不能为空");
        }
        if (StringUtils.isBlank(controlFlag)) {
            throw new InitChannelException(String.format("%s渠道初始化,控制文件FLAG不能为空", channelName));
        }
        if (StringUtils.isBlank(localfilepath)) {
            throw new InitChannelException(String.format("%s渠道初始化,渠道本地目录不能为空", localfilepath));
        }
        if (StringUtils.isBlank(fileName)) {
            throw new InitChannelException(String.format("%s渠道初始化,渠道对账文件名不能为空", fileName));
        }
        if (StringUtils.isBlank(sftpPath)) {
            throw new InitChannelException(String.format("%s渠道初始化,sftp上渠道目录不能为空", sftpPath));
        }
    }

    /**
     * 必须初始化参数.
     * 
     * channelName-渠道名称
     * ;channelCode-渠道号;controlFlag-控制文件FLAG;localfilepath-渠道本地目录;fileName-
     * 渠道对账文件名;sftpPath-sftp上渠道目录
     */
    protected abstract void doinit();

    /**
     * 添加平账数据.
     *
     * @param _mcht_no
     * @param string
     */
    protected void addBalanceContent(String _mcht_no, String content) {
        List<String> contentList = balanceMap.get(_mcht_no);
        if (null == contentList) {
            contentList = new ArrayList<String>();
            contentList.add(content);
            balanceMap.put(_mcht_no, contentList);
        } else {
            contentList.add(content);
        }

    }

    public String getControlFlag() {
        return controlFlag;
    }

    public void setControlFlag(String controlFlag) {
        this.controlFlag = controlFlag;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getLocalfilepath() {
        return localfilepath;
    }

    public void setLocalfilepath(String localfilepath) {
        this.localfilepath = localfilepath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getSftpPath() {
        return sftpPath;
    }

    public void setSftpPath(String sftpPath) {
        this.sftpPath = sftpPath;
    }

}
