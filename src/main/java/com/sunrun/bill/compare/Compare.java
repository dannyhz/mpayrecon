package com.sunrun.bill.compare;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sunrun.bill.db.DefaultDbOpt;
import com.sunrun.bill.exception.BillCompareException;
import com.sunrun.bill.file.YzfFileOpt;
import com.sunrun.bill.file.ZjFileOpt;
import com.sunrun.bill.holder.CompareDataParamHolder;
import com.sunrun.bill.model.BillControlDO;
import com.sunrun.bill.model.BillDetailDO;
import com.sunrun.bill.model.BillOrderDO;
import com.sunrun.bill.thread.ThreadExecute;
import com.sunrun.bill.thread.ThreadOpt;
import com.sunrun.constant.BillStatus;
import com.sunrun.mpos.common.utils.DateUtils;
import com.sunrun.mpos.common.utils.FileUtils;
import com.sunrun.mpos.common.utils.PropertyUtils;
import com.sunrun.mpos.common.utils.StringUtils;
import com.sunrun.util.BillConstants;
import com.sunrun.util.MapBytesEntry;

@Component
public class Compare {
    private static final Logger logger = LoggerFactory.getLogger(Compare.class);

    /**
     * 核心对账类
     * 
     * @param bh
     * @throws BillCompareException
     */
    public void core(CompareDataParamHolder bh, Date date) throws BillCompareException {
        // 本地文件标记位(记录最终是打了哪个标记，便于后面删除标记)
        String _localCreateFlag = "";
        try {
            // 中金控制文件FLAG
            String _zjFlag = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.zj.controlFlag"),
                DateUtils.formatDate(date, "yyyyMMdd"));
            // 翼支付控制文件FLAG
            String _yzfFlag = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.yzf.controlFlag"),
                DateUtils.formatDate(date, "yyyyMMdd"));
            // 翼支付渠道号
            String _yzfChannelCode = PropertyUtils.getValue("com.sunrun.bill.yzf.channelCode");
            // 中金渠道号
            String _zjChannelCode = PropertyUtils.getValue("com.sunrun.bill.zj.channelCode");
            // 渠道号
            String _channelCode = "";

            // 翼支付文件名称
            String _yzfFileName = PropertyUtils.getValue("com.sunrun.bill.yzf.fileName");
            // 中金文件名称
            String _zjFileName = PropertyUtils.getValue("com.sunrun.bill.zj.fileName");
            // 文件名称
            String _fileName = "";

            CountDownLatch latch = new CountDownLatch(2);
            ThreadExecute fileThread = null;
            // ThreadExecute dbThread = null;
            if (bh.getFileOpt() instanceof YzfFileOpt) {
                // 如果是翼支付文件的实例
                _channelCode = _yzfChannelCode;
                _fileName = _yzfFileName;
                if (FileUtils.checkLocalFileExist(_yzfFlag)) {
                    // 如果翼支付实例已经进行过对账则不做对账，直接return
                    logger.info("=====翼支付标记文件[" + _yzfFlag + "]已存在,无需对账！=====");
                    return;
                } else {
                    // 创建翼支付标记并开始对账操作
                    FileUtils.createLocalFile(_yzfFlag);
                    _localCreateFlag = _yzfFlag;
                    logger.info("=====开始对账操作(翼支付)=====");
                }

                fileThread = new ThreadExecute(latch, (ThreadOpt) bh.getFileOpt(), date, _channelCode);
                fileThread.start();
                if (null != fileThread.getThrowable()) {
                    throw new BillCompareException(fileThread.getThrowable());
                }

            } else if (bh.getFileOpt() instanceof ZjFileOpt) {
                // 如果是中金文件实例
                _channelCode = _zjChannelCode;
                _fileName = _zjFileName;
                if (FileUtils.checkLocalFileExist(_zjFlag)) {
                    // 如果中金实例已经进行过对账则不做对账，直接return
                    logger.info("=====中金标记文件[" + _zjFlag + "]已存在,无需对账！=====");
                    // return;
                } else {
                    FileUtils.createLocalFile(_zjFlag);
                    // 创建中金标记并开始对账操作
                    _localCreateFlag = _zjFlag;
                    logger.info("=====开始对账操作(中金)=====");
                }

                fileThread = new ThreadExecute(latch, (ThreadOpt) bh.getFileOpt(), date, _channelCode);
                fileThread.start();
                if (null != fileThread.getThrowable()) {
                    throw new BillCompareException(fileThread.getThrowable());
                }

            }

            // 如果第三方(中金、翼支付)的文件已生成则开始对账
            ThreadExecute dbThread = new ThreadExecute(latch, (ThreadOpt) bh.getDbOpt(), date, _channelCode);
            dbThread.start();
            latch.await();
            if (null != dbThread.getThrowable()) {
                throw new BillCompareException(dbThread.getThrowable());
            }

            Map<String, Object> tmpMap = (Map<String, Object>) fileThread.getReturnObj();
            tmpMap.get("");
            System.out.println("文件存在标记:" + tmpMap.get("fileExist"));
            System.out.println("文件data:" + tmpMap.get("dataList"));
            if (tmpMap.get("fileExist").equals("0")) {
                logger.info("对账文件为空,无需发起对账请求!");
                FileUtils.deleteLocalFile(_localCreateFlag);
                return;
            }

            compareFileAndDb(dbThread.getReturnObj(), tmpMap.get("dataList"), _fileName, _zjFlag, _yzfFlag, date,
                _channelCode);// Channel_code注入进来

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            FileUtils.deleteLocalFile(_localCreateFlag);
        }
    }

    /**
     * 对账
     * 
     * @param dbObject
     * @param fileObject
     */
    private void compareFileAndDb(Object dbObject, Object fileObject, String fileName, String _zjFlag, String _yzfFlag,
        Date date, String _channelCode) throws BillCompareException {

        logger.info("===== channelCode=[" + _channelCode + "] 文件获取、数据库抽取全部完成，开始进行对账操作=====");
        long _comparestarttime = System.currentTimeMillis();
        // 正向比较数据，以行内数据库为准做循环
        long _fileDiffCount_zfb = 0;// 文件存在而数据库不存在(支付宝)
        long _fileDiffCount_ws = 0;// 文件存在而数据库不存在(网商)
        long _fileDiffCount_jyc = 0;// 文件存在而数据库不存在(聚有财)

        long _dbDiffCount_zfb = 0;// 数据库存在而文件不存在(支付宝)
        long _dbDiffCount_ws = 0;// 数据库存在而文件不存在(网商)
        long _dbDiffCount_jyc = 0;// 数据库存在而文件不存在(聚有财)

        long _xtCount_zfb = 0;// 平账笔数(支付宝)
        long _xtCount_ws = 0;// 平账笔数(网商)
        long _xtCount_jyc = 0;// 平账笔数(聚有财)

        long _czCount_zfb = 0;// 错账笔数(支付宝)
        long _czCount_ws = 0;// 错账笔数(网商)
        long _czCount_jyc = 0;// 错账笔数(聚有财)

        long _balanceAmount_zfb = 0;// 平账总金额(支付宝)
        long _balanceAmount_ws = 0;// 平账总金额(网商)
        long _balanceAmount_jyc = 0;// 平账总金额(聚有财)

        long _tradeAmount_zfb = 0;// 交易总金额(支付宝)不包含文件存在而数据库不存在的数据
        long _tradeAmount_ws = 0;// 交易总金额(网商)不包含文件存在而数据库不存在的数据
        long _tradeAmount_jyc = 0;// 交易总金额(聚有财)不包含文件存在而数据库不存在的数据

        String _yesterdayStr = DateUtils.formatDate(DateUtils.minusDay(date, 1), "yyyyMMdd");// 昨天
        String _yzfFqFilePath = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.bank.local.yzffilepath"),
            _yesterdayStr);
        String _zjFqFilePath = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.bank.local.zjfilepath"),
            _yesterdayStr);
        String _fileName = MessageFormat.format(fileName, _yesterdayStr);
        String _mchtNoWS = PropertyUtils.getValue("com.sunrun.bill.orders.mchtNo.ws");
        String _mchtNoZFB = PropertyUtils.getValue("com.sunrun.bill.orders.mchtNo.zfb");
        String _mchtNoJYC = PropertyUtils.getValue("com.sunrun.bill.orders.mchtNo.jyc");

        List<byte[]> fileList = (List<byte[]>) fileObject;
        Map<MapBytesEntry, byte[]> dbMap = (Map<MapBytesEntry, byte[]>) dbObject;
        // 回写主表业务数据置入
        BillControlDO billControlWsDO = new BillControlDO();
        BillControlDO billControlZfbDO = new BillControlDO();
        BillControlDO billControlJycDO = new BillControlDO();

        // billControlWsDO.setChannel_code(_channelCode);// 渠道号
        // billControlWsDO.setBill_date(_yesterdayStr);
        // billControlWsDO.setFile_name(_fileName);
        // billControlZfbDO.setChannel_code(_channelCode);// 渠道号
        // billControlZfbDO.setBill_date(_yesterdayStr);
        // billControlZfbDO.setFile_name(_fileName);
        // billControlJycDO.setChannel_code(_channelCode);// 渠道号
        // billControlJycDO.setBill_date(_yesterdayStr);
        // billControlJycDO.setFile_name(_fileName);
        List<BillDetailDO> billDetailWsList = new ArrayList<BillDetailDO>();// 回写业务明细dataList(网商)
        List<BillDetailDO> billDetailZfbList = new ArrayList<BillDetailDO>();// 回写业务明细dataList(支付宝)
        List<BillDetailDO> billDetailJycList = new ArrayList<BillDetailDO>();// 回写业务明细dataList(聚有财)
        // 交易流水更新状态
        List<BillOrderDO> wsbillOrderDOlist = new ArrayList<BillOrderDO>();
        List<BillOrderDO> zfbbillOrderDOlist = new ArrayList<BillOrderDO>();
        List<BillOrderDO> jycbillOrderDOlist = new ArrayList<BillOrderDO>();

        BillOrderDO billOrderDO = null;
        try {
            // 在对账之前先生成三个平帐空文件（网商、支付宝、聚有财）
            String _fqFilePath = "";
            if (PropertyUtils.getValue("com.sunrun.bill.zj.channelCode").equals(_channelCode)) {
                // 渠道号=中金
                _fqFilePath = _zjFqFilePath;
            } else if (PropertyUtils.getValue("com.sunrun.bill.yzf.channelCode").equals(_channelCode)) {
                _fqFilePath = _yzfFqFilePath;
            }
            FileUtils.writeFile(_fqFilePath, PropertyUtils.getValue("com.sunrun.bill.filename.ws") + _fileName, "");
            FileUtils.writeFile(_fqFilePath, PropertyUtils.getValue("com.sunrun.bill.filename.zfb") + _fileName, "");
            FileUtils.writeFile(_fqFilePath, PropertyUtils.getValue("com.sunrun.bill.filename.jyc") + _fileName, "");// 渠道号判断所要对账的渠道
            if (_channelCode.equals(PropertyUtils.getValue("com.sunrun.bill.yzf.channelCode"))) { //
                logger.info("翼支付渠道_channelCode：" + _channelCode + " 开始对账----------------");
                if (fileList != null && fileList.size() > 0) {
                    for (byte[] bill : fileList) {
                        // 默认错账记录
                        BillDetailDO tmp_detailDO = new BillDetailDO(_channelCode, _yesterdayStr, _fileName);

                        // 文件数据
                        String billStr[] = new String(bill, "utf-8").split(",");
                        String _trx_id = billStr[0];// 流水号
                        String _file_amount = billStr[1];// 金额
                        logger.info("文件数据:流水号{},金额{}", _trx_id, _file_amount);

                        // 添加文件数据进错账记录
                        tmp_detailDO.setFile_trx_id(_trx_id);
                        tmp_detailDO.setFile_amount(_file_amount);

                        MapBytesEntry keyObj = new MapBytesEntry(_trx_id.getBytes());
                        // 判断数据库是否包含该条文件数据
                        if (dbMap.containsKey(keyObj)) {
                            // 交易流水，状态更改对象
                            billOrderDO = new BillOrderDO();
                            billOrderDO.setTrx_id(_trx_id);// 交易流水号
                            // 数据库数据
                            String mapValue = new String(dbMap.get(keyObj), "utf-8");
                            String mapValueList[] = mapValue.split(";");
                            String _cust_card_no = mapValueList[0];
                            String _amount = mapValueList[1];
                            String _mcht_no = mapValueList[2];
                            String _channel_time = mapValueList[3];
                            String _mcht_seq_no = mapValueList[4];
                            String _status = mapValueList[5];
                            logger.info("数据库数据:银行卡号{},金额{},商户号{},渠道时间{},商户订单号{},渠道状态{}", _cust_card_no, _amount,
                                _mcht_no, _channel_time, _mcht_seq_no, _status);
                            // 添加数据库数据进错账记录
                            tmp_detailDO.setCust_card_no(_cust_card_no);
                            tmp_detailDO.setDb_amount(_amount);
                            tmp_detailDO.setMcht_no(_mcht_no);
                            tmp_detailDO.setChannel_time(_channel_time);
                            tmp_detailDO.setDbOrderStatus(_status);
                            tmp_detailDO.setDatabase_trx_id(_trx_id);

                            // 从dbMap中删除数据
                            dbMap.remove(keyObj);

                            // 分商户,数据库交易金额累加
                            if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoWS)) {
                                _tradeAmount_ws += Long.valueOf(_amount);// 交易金额累加
                            } else if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoZFB)) {
                                _tradeAmount_zfb += Long.valueOf(_amount);// 交易金额累加
                            } else if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoJYC)) {
                                _tradeAmount_jyc += Long.valueOf(_amount);// 交易金额累加
                            }

                            // 比对金额，相同，则认为成功
                            if (_file_amount.equals(_amount)) {
                                // 分商户处理
                                if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoWS)) {
                                    // 网商
                                    _balanceAmount_ws += Long.valueOf(_file_amount);// 平账金额累加
                                    _xtCount_ws++;// 平账笔数累加

                                    // 写入商户的对账文件
                                    String _content = _channel_time + "," + _trx_id + "," + _mcht_seq_no + ","
                                        + _cust_card_no + "," + _amount + "\n";
                                    FileUtils.writeFile(_fqFilePath,
                                        PropertyUtils.getValue("com.sunrun.bill.filename.ws") + _fileName, _content);
                                } else if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoZFB)) {
                                    // 支付宝
                                    _balanceAmount_zfb += Long.valueOf(_file_amount);// 平账金额累加
                                    _xtCount_zfb++;// 平账笔数累加

                                    // 写入商户的对账文件
                                    String _content = _channel_time + "," + _trx_id + "," + _mcht_seq_no + ","
                                        + _cust_card_no + "," + _amount + "\n";
                                    FileUtils.writeFile(_fqFilePath,
                                        PropertyUtils.getValue("com.sunrun.bill.filename.zfb") + _fileName, _content);
                                } else if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoJYC)) {
                                    _balanceAmount_jyc += Long.valueOf(_file_amount);// 平账金额累加
                                    _xtCount_jyc++;// 平账金额累加

                                    // 写入商户的对账文件
                                    String _content = _channel_time + "," + _trx_id + "," + _mcht_seq_no + ","
                                        + _cust_card_no + "," + _amount + "\n";
                                    FileUtils.writeFile(_fqFilePath,
                                        PropertyUtils.getValue("com.sunrun.bill.filename.jyc") + _fileName, _content);
                                }
                                // 对账成功
                                tmp_detailDO.setWrong_reason_flag(BillConstants.SUCESS_FLAG);
                                // 交易流水对账成功
                                billOrderDO.setReconciliationStatus(BillStatus.BILL_SUCCESS.getStatus());
                            } else {
                                // 金额不同,错账处理---两边金额不一致
                                tmp_detailDO.setWrong_reason_flag(BillConstants.WRONG_FLAG_W);
                                // 交易流水对账失败
                                billOrderDO.setReconciliationStatus(BillStatus.BILL_FAIL.getStatus());
                                // 分商户,统计错账笔数
                                if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoWS)) {
                                    _czCount_ws++;// 错账笔数累加
                                } else if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoZFB)) {
                                    _czCount_zfb++;// 错账笔数累加
                                } else if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoJYC)) {
                                    _czCount_jyc++;// 错账笔数累加
                                }

                            }

                            // 分商户，添加更新的交易流水
                            if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoWS)) {
                                wsbillOrderDOlist.add(billOrderDO);
                            } else if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoZFB)) {
                                zfbbillOrderDOlist.add(billOrderDO);
                            } else if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoJYC)) {
                                jycbillOrderDOlist.add(billOrderDO);
                            }
                        } else {
                            // 文件存在而数据库不存在
                            // 因为无法区分文件中存在而数据库中不存在的数据的商户类型， 所以不统计_fileDiffCount
                            // 错账处理---渠道多记录
                            tmp_detailDO.setWrong_reason_flag(BillConstants.WRONG_FLAG_F);
                        }

                        // 分商户，添加错账记录
                        if (!BillConstants.SUCESS_FLAG.equals(tmp_detailDO.getWrong_reason_flag())) {
                            if (!StringUtils.isEmpty(tmp_detailDO.getMcht_no())
                                && tmp_detailDO.getMcht_no().equals(_mchtNoWS)) {
                                billDetailWsList.add(tmp_detailDO);
                            } else if (!StringUtils.isEmpty(tmp_detailDO.getMcht_no())
                                && tmp_detailDO.getMcht_no().equals(_mchtNoZFB)) {
                                billDetailZfbList.add(tmp_detailDO);
                            } else if (!StringUtils.isEmpty(tmp_detailDO.getMcht_no())
                                && tmp_detailDO.getMcht_no().equals(_mchtNoJYC)) {
                                billDetailJycList.add(tmp_detailDO);
                            } else {
                                // 找不到商户号的数据(文件存在而数据库不存在的数据)
                                // 都添加进去
                                billDetailWsList.add(tmp_detailDO);
                                billDetailZfbList.add(tmp_detailDO);
                                billDetailJycList.add(tmp_detailDO);
                            }
                        }

                    }
                }
            } else if (_channelCode.equals(PropertyUtils.getValue("com.sunrun.bill.zj.channelCode"))) {
                logger.info("中金渠道_channelCode：" + _channelCode + " 开始对账");
                if (fileList != null && fileList.size() > 0) {
                    for (byte[] bill : fileList) {

                        // 默认错账记录
                        BillDetailDO tmp_detailDO = new BillDetailDO(_channelCode, _yesterdayStr, _fileName);

                        // 文件数据
                        String billStr[] = new String(bill, "utf-8").split(",");
                        String _trx_id = billStr[0];// 流水号
                        String _file_amount = billStr[1];// 金额
                        String _zj_status = billStr[2];// 状态
                        logger.info("文件数据:流水号{},金额{},状态{}", _trx_id, _file_amount, _zj_status);

                        // 添加文件数据进错账记录
                        tmp_detailDO.setFile_trx_id(_trx_id);
                        tmp_detailDO.setFile_amount(_file_amount);
                        tmp_detailDO.setChannelOrderStatus(_zj_status);

                        MapBytesEntry keyObj = new MapBytesEntry(_trx_id.getBytes());
                        // 判断数据库是否包含该条文件数据
                        if (dbMap.containsKey(keyObj)) {
                            // 交易流水，状态更改对象
                            billOrderDO = new BillOrderDO();
                            billOrderDO.setTrx_id(_trx_id);// 交易流水号
                            billOrderDO.setReconciliationStatus(BillStatus.BILL_FAIL.getStatus());

                            // 数据库数据
                            String mapValue = new String(dbMap.get(keyObj), "utf-8");
                            String mapValueList[] = mapValue.split(";");
                            String _cust_card_no = mapValueList[0];// 银行卡号
                            String _amount = mapValueList[1];// 金额
                            String _mcht_no = mapValueList[2];// 商户号
                            String _channel_time = mapValueList[3];// 渠道时间
                            String _mcht_seq_no = mapValueList[4];// 商户订单号
                            String _status = mapValueList[5];// 渠道状态
                            logger.info("数据库数据:银行卡号{},金额{},商户号{},渠道时间{},商户订单号{},渠道状态{}", _cust_card_no, _amount,
                                _mcht_no, _channel_time, _mcht_seq_no, _status);
                            // 添加数据库数据进错账记录
                            tmp_detailDO.setCust_card_no(_cust_card_no);
                            tmp_detailDO.setDb_amount(_amount);
                            tmp_detailDO.setMcht_no(_mcht_no);
                            tmp_detailDO.setChannel_time(_channel_time);
                            tmp_detailDO.setDbOrderStatus(_status);
                            tmp_detailDO.setDatabase_trx_id(_trx_id);

                            // 从dbMap中删除数据
                            dbMap.remove(keyObj);

                            // 分商户,数据库交易金额累加
                            if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoWS)) {
                                _tradeAmount_ws += Long.valueOf(_amount);// 交易金额累加
                            } else if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoZFB)) {
                                _tradeAmount_zfb += Long.valueOf(_amount);// 交易金额累加
                            } else if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoJYC)) {
                                _tradeAmount_jyc += Long.valueOf(_amount);// 交易金额累加
                            }

                            // 比对状态,认为状态成功:中金状态成功30,订单状态00-成功，30-未知
                            if (((_status.equals("00") && ("30").equals(_zj_status))
                                || (_status.equals("30")) && _zj_status.equals("30"))) {

                                // 比对金额,金额一样，成功处理
                                if (_file_amount.equals(_amount)) {
                                    // 分商户处理
                                    if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoWS)) {
                                        // 网商
                                        _balanceAmount_ws += Long.valueOf(_file_amount);// 平账金额累加
                                        _xtCount_ws++;// 平账笔数累加

                                        // 写入商户的对账文件
                                        String _content = _channel_time + "," + _trx_id + "," + _mcht_seq_no + ","
                                            + _cust_card_no + "," + _amount + "\n";
                                        FileUtils.writeFile(_fqFilePath,
                                            PropertyUtils.getValue("com.sunrun.bill.filename.ws") + _fileName,
                                            _content);
                                    } else if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoZFB)) {
                                        // 支付宝
                                        _balanceAmount_zfb += Long.valueOf(_file_amount);// 平账金额累加
                                        _xtCount_zfb++;// 平账笔数累加

                                        // 写入商户的对账文件
                                        String _content = _channel_time + "," + _trx_id + "," + _mcht_seq_no + ","
                                            + _cust_card_no + "," + _amount + "\n";
                                        FileUtils.writeFile(_fqFilePath,
                                            PropertyUtils.getValue("com.sunrun.bill.filename.zfb") + _fileName,
                                            _content);
                                    } else if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoJYC)) {
                                        _balanceAmount_jyc += Long.valueOf(_file_amount);// 平账金额累加
                                        _xtCount_jyc++;// 平账金额累加

                                        // 写入商户的对账文件
                                        String _content = _channel_time + "," + _trx_id + "," + _mcht_seq_no + ","
                                            + _cust_card_no + "," + _amount + "\n";
                                        FileUtils.writeFile(_fqFilePath,
                                            PropertyUtils.getValue("com.sunrun.bill.filename.jyc") + _fileName,
                                            _content);
                                    }
                                    // 对账成功
                                    tmp_detailDO.setWrong_reason_flag(BillConstants.SUCESS_FLAG);
                                    // 交易流水对账成功
                                    billOrderDO.setReconciliationStatus(BillStatus.BILL_SUCCESS.getStatus());
                                } else {
                                    // 金额不同,错账处理---两边金额不一致
                                    tmp_detailDO.setWrong_reason_flag(BillConstants.WRONG_FLAG_W);
                                    // 交易流水对账失败
                                    billOrderDO.setReconciliationStatus(BillStatus.BILL_FAIL.getStatus());
                                    // 分商户,统计错账笔数
                                    if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoWS)) {
                                        _czCount_ws++;// 错账笔数累加
                                    } else if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoZFB)) {
                                        _czCount_zfb++;// 错账笔数累加
                                    } else if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoJYC)) {
                                        _czCount_jyc++;// 错账笔数累加
                                    }

                                }

                            }
                            // 状态不对，错账处理--状态不一致
                            else if ((_status.equals("99") && _zj_status.equals("30"))
                                || (_status.equals("00") && _zj_status.equals("40"))) {
                                // 错账处理--状态不一致
                                tmp_detailDO.setWrong_reason_flag(BillConstants.WRONG_FLAG_N);
                                // 交易流水对账失败
                                billOrderDO.setReconciliationStatus(BillStatus.BILL_FAIL.getStatus());
                                // 分商户,统计错账笔数
                                if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoWS)) {
                                    _czCount_ws++;// 错账笔数累加
                                } else if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoZFB)) {
                                    _czCount_zfb++;// 错账笔数累加
                                } else if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoJYC)) {
                                    _czCount_jyc++;// 错账笔数累加
                                }

                            } else {
                                // 其他情况不处理,错账记录默认成功
                                tmp_detailDO.setWrong_reason_flag(BillConstants.SUCESS_FLAG);
                                // 不处理记录，流水记录标记对账失败
                                billOrderDO.setReconciliationStatus(BillStatus.BILL_FAIL.getStatus());
                            }

                            // 分商户，添加更新的交易流水
                            if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoWS)) {
                                wsbillOrderDOlist.add(billOrderDO);
                            } else if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoZFB)) {
                                zfbbillOrderDOlist.add(billOrderDO);
                            } else if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoJYC)) {
                                jycbillOrderDOlist.add(billOrderDO);
                            }
                        } else {
                            // 文件存在而数据库不存在
                            // 因为无法区分文件中存在而数据库中不存在的数据的商户类型， 所以不统计_fileDiffCount
                            // 错账处理---渠道多记录
                            tmp_detailDO.setWrong_reason_flag(BillConstants.WRONG_FLAG_F);
                        }

                        // 分商户，添加错账记录
                        if (!BillConstants.SUCESS_FLAG.equals(tmp_detailDO.getWrong_reason_flag())) {
                            if (!StringUtils.isEmpty(tmp_detailDO.getMcht_no())
                                && tmp_detailDO.getMcht_no().equals(_mchtNoWS)) {
                                billDetailWsList.add(tmp_detailDO);
                            } else if (!StringUtils.isEmpty(tmp_detailDO.getMcht_no())
                                && tmp_detailDO.getMcht_no().equals(_mchtNoZFB)) {
                                billDetailZfbList.add(tmp_detailDO);
                            } else if (!StringUtils.isEmpty(tmp_detailDO.getMcht_no())
                                && tmp_detailDO.getMcht_no().equals(_mchtNoJYC)) {
                                billDetailJycList.add(tmp_detailDO);
                            } else {
                                // 找不到商户号的数据(文件存在而数据库不存在的数据)
                                // 都添加进去
                                billDetailWsList.add(tmp_detailDO);
                                billDetailZfbList.add(tmp_detailDO);
                                billDetailJycList.add(tmp_detailDO);
                            }
                        }
                    }
                    logger.info("中金渠道_channelCode：" + _channelCode + " 开始成功");
                }
            }
            // 最后map中留下的是 数据库存在而对账文件中缺失的数据 迭代map 并且置入最后的数据库存在而文件不存在的数据
            Set set = dbMap.entrySet();
            Iterator<Map.Entry<MapBytesEntry, byte[]>> iterator = set.iterator();
            while (iterator.hasNext()) {
                Map.Entry<MapBytesEntry, byte[]> entry = iterator.next();
                // 默认错账记录
                BillDetailDO tmp_detailDO = new BillDetailDO(_channelCode, _yesterdayStr, _fileName);
                tmp_detailDO.setWrong_reason_flag("D");

                // 数据库数据
                String _dbMapKey = new String(entry.getKey().getValue(), "utf-8");
                String billStrDetail[] = _dbMapKey.split(";");
                String _trx_id = billStrDetail[0];
                String _mapValue = new String(entry.getValue(), "utf-8");
                String _mapValueList[] = _mapValue.split(";");
                String _cust_card_no = _mapValueList[0];// 银行卡号
                String _amount = _mapValueList[1];// 金额
                String _mcht_no = _mapValueList[2];// 商户号
                String _channel_time = _mapValueList[3];// 渠道时间
                String _status = _mapValueList[5];// 渠道状态

                // 添加数据库数据进错账记录
                tmp_detailDO.setDatabase_trx_id(_trx_id);
                tmp_detailDO.setCust_card_no(_cust_card_no);
                tmp_detailDO.setDb_amount(_amount);
                tmp_detailDO.setMcht_no(_mcht_no);
                tmp_detailDO.setChannel_time(_channel_time);
                tmp_detailDO.setDbOrderStatus(_status);

                // 交易流水，状态更改对象
                billOrderDO = new BillOrderDO();
                billOrderDO.setTrx_id(_trx_id);// 交易流水号
                billOrderDO.setReconciliationStatus(BillStatus.BILL_FAIL.getStatus());

                // 分商户
                if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoWS)) {
                    _tradeAmount_ws += Long.valueOf(_amount);// 交易金额累加
                    _dbDiffCount_ws++;
                    billDetailWsList.add(tmp_detailDO);// 添加错账记录
                    wsbillOrderDOlist.add(billOrderDO);
                } else if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoZFB)) {
                    _tradeAmount_zfb += Long.valueOf(_amount);// 交易金额累加
                    _dbDiffCount_zfb++;
                    billDetailZfbList.add(tmp_detailDO);// 添加错账记录
                    zfbbillOrderDOlist.add(billOrderDO);
                } else if (!StringUtils.isEmpty(_mcht_no) && _mcht_no.equals(_mchtNoJYC)) {
                    _tradeAmount_jyc += Long.valueOf(_amount);// 交易金额累加
                    _dbDiffCount_jyc++;
                    billDetailJycList.add(tmp_detailDO);// 添加错账记录
                    jycbillOrderDOlist.add(billOrderDO);
                }

            }

            // 回写主表业务数据置入
            //
            String bat_id = new Date().getTime() + "";
            // 网商
            // billControlWsDO.setBat_id(bat_id);// 批次号
            // billControlWsDO.setMcht_no(_mchtNoWS);// 商户号
            // billControlWsDO.setBalance_amount(String.valueOf(_balanceAmount_ws));//
            // 平账⾦额
            // billControlWsDO.setTotal_bills_amount(String.valueOf(_tradeAmount_ws));//
            // 交易总金额
            // billControlWsDO.setTotal_bills_account(
            // String.valueOf(_dbDiffCount_ws + _fileDiffCount_ws + _czCount_ws
            // + _xtCount_ws));// 交易总笔数
            // billControlWsDO.setBalance_bills_account(String.valueOf(_xtCount_ws));//
            // 平账笔数
            // billControlWsDO.setWrong_bills_account(String.valueOf(_czCount_ws));//
            // 错账笔数
            // billControlWsDO.setDb_bills_account(String.valueOf(_dbDiffCount_ws));
            // billControlWsDO.setFile_bills_account(String.valueOf(_fileDiffCount_ws));
            // // 支付宝
            // billControlZfbDO.setBat_id(bat_id);// 批次号
            // billControlZfbDO.setMcht_no(_mchtNoZFB);// 商户号
            // billControlZfbDO.setBalance_amount(String.valueOf(_balanceAmount_zfb));//
            // 平账⾦额
            // billControlZfbDO.setTotal_bills_amount(String.valueOf(_tradeAmount_zfb));//
            // 交易总金额
            // billControlZfbDO.setTotal_bills_account(
            // String.valueOf(_dbDiffCount_zfb + _fileDiffCount_zfb +
            // _czCount_zfb + _xtCount_zfb));// 交易总笔数
            // billControlZfbDO.setBalance_bills_account(String.valueOf(_xtCount_zfb));//
            // 平账笔数
            // billControlZfbDO.setWrong_bills_account(String.valueOf(_czCount_zfb));//
            // 错账笔数
            // billControlZfbDO.setDb_bills_account(String.valueOf(_dbDiffCount_zfb));
            // billControlZfbDO.setFile_bills_account(String.valueOf(_fileDiffCount_zfb));
            // // 聚有财
            // billControlJycDO.setBat_id(bat_id);// 批次号
            // billControlJycDO.setMcht_no(_mchtNoJYC);// 商户号
            // billControlJycDO.setBalance_amount(String.valueOf(_balanceAmount_jyc));//
            // 平账⾦额
            // billControlJycDO.setTotal_bills_amount(String.valueOf(_tradeAmount_jyc));//
            // 交易总金额
            // billControlJycDO.setTotal_bills_account(
            // String.valueOf(_dbDiffCount_jyc + _fileDiffCount_jyc +
            // _czCount_jyc + _xtCount_jyc));// 交易总笔数
            // billControlJycDO.setBalance_bills_account(String.valueOf(_xtCount_jyc));//
            // 平账笔数
            // billControlJycDO.setWrong_bills_account(String.valueOf(_czCount_jyc));//
            // 错账笔数
            // billControlJycDO.setDb_bills_account(String.valueOf(_dbDiffCount_jyc));
            // billControlJycDO.setFile_bills_account(String.valueOf(_fileDiffCount_jyc));

        } catch (Exception e) {
            throw new BillCompareException(e);
        } finally {
            fileList = null;
            dbMap = null;
        }
        if (StringUtils.isNotEmpty(_channelCode)
            && _channelCode.equals(PropertyUtils.getValue("com.sunrun.bill.yzf.channelCode"))) {
            // 如果渠道是翼支付，则生成翼支付FQ回写标记(此时本地平账文件已经回写完毕)
            logger.info("=====创建翼支付回写文件完成标记[" + _yzfFlag + "/FQ]=====");
            FileUtils.createLocalFile(_yzfFlag + "/FQ");
        }
        if (StringUtils.isNotEmpty(_channelCode)
            && _channelCode.equals(PropertyUtils.getValue("com.sunrun.bill.zj.channelCode"))) {
            // 如果渠道是中金，则生成翼支付FQ回写标记(此时本地平账文件已经回写完毕)
            logger.info("=====创建中金回写文件完成标记[" + _zjFlag + "/FQ]=====");
            FileUtils.createLocalFile(_zjFlag + "/FQ");
        }
        logger.info("=====对账完成！=====");
        logger.info("=====对账操作总耗时=" + (System.currentTimeMillis() - _comparestarttime));

        try {

            new DefaultDbOpt(billDetailWsList, billControlWsDO, wsbillOrderDOlist).updateBillResult();// 回写
            new DefaultDbOpt(billDetailZfbList, billControlZfbDO, zfbbillOrderDOlist).updateBillResult();// 回写
            new DefaultDbOpt(billDetailJycList, billControlJycDO, jycbillOrderDOlist).updateBillResult();// 回写

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }
}
