package com.sunrun.bill.compare.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.sunrun.bill.exception.BillCompareException;
import com.sunrun.bill.model.BillControlDO;
import com.sunrun.bill.model.BillDetailDO;
import com.sunrun.bill.model.BillOrderDO;
import com.sunrun.constant.BillStatus;
import com.sunrun.mpos.common.utils.DateUtils;
import com.sunrun.mpos.common.utils.PropertyUtils;
import com.sunrun.mpos.common.utils.StringUtils;
import com.sunrun.util.BillConstants;
import com.sunrun.util.MapBytesEntry;

/**
 * 翼支付渠道对账
 *
 * @author liuwen
 * @since V1.0.0
 */
@Component
public class YZFChannelBill extends AbstractChannelBill {

    @Override
    protected void doinit() {
        super.setChannelName("翼支付");
        super.setChannelCode(PropertyUtils.getValue("com.sunrun.bill.yzf.channelCode"));
        super.setControlFlag(PropertyUtils.getValue("com.sunrun.bill.yzf.controlFlag"));
        super.setLocalfilepath(PropertyUtils.getValue("com.sunrun.bill.bank.local.yzffilepath"));
        super.setFileName(PropertyUtils.getValue("com.sunrun.bill.yzf.fileName"));
        super.setSftpPath(PropertyUtils.getValue("com.sunrun.bill.sftp.yzf.filepath"));

    }

    @Override
    public List<byte[]> doParseChannelLocalFile(InputStream inputStream) throws Exception {
        try {
            return anaFtpFile(inputStream);
        } catch (Exception e) {
            throw new Exception("解析文件异常");
        }

    }

    /**
     * 解析文件
     * 
     * @param fileName
     * @return
     * @throws Exception
     */
    public List<byte[]> anaFtpFile(InputStream inputStream) throws Exception {
        BufferedReader br = null;
        List<byte[]> list = new ArrayList<byte[]>();
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = br.readLine()) != null) {
                byte[] bs = handleLine(line);
                if (bs != null) {
                    list.add(bs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("解析文件异常");
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new Exception("关闭IO流异常");
            }
        }
        return list;
    }

    /**
     * 对每一行String的处理
     * 
     * @param unHandleStr
     * @return
     * @throws Exception
     */
    public byte[] handleLine(String unHandleStr) throws Exception {
        byte[] _resStr = null;
        // 生产数据标记
        // 3表示生产数据
        // 其他表示测试数据
        String trxIdFlag = PropertyUtils.getValue("com.sunrun.bill.trxidflag");
        try {
            String _trx_id = StringUtils.fatch(unHandleStr, 1, ",");// 流水号trx_id
            if (!StringUtils.isEmpty(trxIdFlag) && trxIdFlag.equals(_trx_id.substring(0, 1))
                && org.springframework.util.StringUtils.countOccurrencesOf(unHandleStr, ",") > 10) {// 排除汇总这行
                _resStr = (_trx_id + // 流水号trx_id
                    "," + StringUtils.fatch(unHandleStr, 9, ",") + "," + StringUtils.fatch(unHandleStr, 14, ","))
                        .getBytes();// 金额
            }
        } catch (Exception e) {
            logger.error("字符解析失败[" + unHandleStr + "]");
            throw new Exception("字符解析失败[" + unHandleStr + "]");
        }
        return _resStr;

    }

    @Override
    protected void reconciliation(List<byte[]> fileList, Map<MapBytesEntry, byte[]> dbMap, Date billDate)
        throws BillCompareException {

        BillOrderDO billOrderDO = null;// 交易流水对象
        BillControlDO billControlDO = null;// 对账单表对象
        BillDetailDO billDetailDO = null;// 错账记录对象
        Long bat_id = new Date().getTime();// 批次号
        try {
            if (fileList != null && fileList.size() > 0) {
                for (byte[] bill : fileList) {
                    // 默认错账记录
                    billDetailDO = new BillDetailDO(super.getChannelCode(), DateUtils.formatDate(billDate, "yyyyMMdd"),
                        getBillDateFileName(billDate));
                    billDetailDO.setBat_id(bat_id + "");
                    // 文件数据
                    String billStr[] = new String(bill, "utf-8").split(",", -1);
                    String _trx_id = billStr[0];// 流水号
                    String _file_amount = billStr[1];// 金额
                    String _yzf_status = billStr[2];// 状态
                    logger.info("文件数据:流水号{},金额{},状态{}", _trx_id, _file_amount, _yzf_status);

                    // 添加文件数据进错账记录
                    billDetailDO.setFile_trx_id(_trx_id);
                    billDetailDO.setFile_amount(_file_amount);
                    billDetailDO.setChannelOrderStatus(_yzf_status);

                    MapBytesEntry keyObj = new MapBytesEntry(_trx_id.getBytes());
                    // 判断数据库是否包含该条文件数据
                    if (dbMap.containsKey(keyObj)) {
                        // 数据库数据
                        String mapValue = new String(dbMap.get(keyObj), "utf-8");
                        String mapValueList[] = mapValue.split(";", -1);
                        String _cust_card_no = mapValueList[0];
                        String _amount = mapValueList[1];
                        String _mcht_no = mapValueList[2];
                        String _channel_time = mapValueList[3];
                        String _mcht_seq_no = mapValueList[4];
                        String _status = mapValueList[5];
                        logger.info("数据库数据:银行卡号{},金额{},商户号{},渠道时间{},商户订单号{},渠道状态{}", _cust_card_no, _amount, _mcht_no,
                            _channel_time, _mcht_seq_no, _status);
                        // 添加数据库数据进错账记录
                        billDetailDO.setCust_card_no(_cust_card_no);
                        billDetailDO.setDb_amount(_amount);
                        billDetailDO.setMcht_no(_mcht_no);
                        billDetailDO.setChannel_time(_channel_time);
                        billDetailDO.setDbOrderStatus(_status);
                        billDetailDO.setDatabase_trx_id(_trx_id);
                        // 从dbMap中删除数据
                        dbMap.remove(keyObj);

                        // 交易流水，状态更改对象
                        billOrderDO = new BillOrderDO();
                        billOrderList.add(billOrderDO);
                        billOrderDO.setTrx_id(_trx_id);// 交易流水号

                        billControlDO = getOrPutBillControlDO(_mcht_no, billDate, bat_id);
                        // 数据库交易金额累加
                        addTotalBillsAmount(billControlDO, _amount);

                        // 比对状态,认为状态成功:翼支付状态成功30,订单状态00-成功，30-未知
                        if ((("00".equals(_status) && ("0000").equals(_yzf_status))
                            || ("30".equals(_status)) && "0000".equals(_yzf_status))) {
                            // 比对金额，相同，则认为成功
                            if (_file_amount.equals(_amount)) {

                                // 平账金额累加
                                addBalanceAmount(billControlDO, _amount);
                                // 平账笔数累加
                                addBalanceCount(billControlDO);

                                // 对账成功
                                billDetailDO.setWrong_reason_flag(BillConstants.SUCESS_FLAG);
                                // 添加进平账记录
                                addBalanceContent(_mcht_no, _channel_time + "," + _trx_id + "," + _mcht_seq_no + ","
                                    + _cust_card_no + "," + _amount + "\n");
                                // 交易流水对账成功
                                billOrderDO.setReconciliationStatus(BillStatus.BILL_SUCCESS.getStatus());
                            } else {
                                // 金额不同,错账处理---两边金额不一致
                                billDetailDO.setWrong_reason_flag(BillConstants.WRONG_FLAG_W);
                                billDetailList.add(billDetailDO);

                                // 交易流水对账失败
                                billOrderDO.setReconciliationStatus(BillStatus.BILL_FAIL.getStatus());
                                // 错账笔数累加
                                addWrongCount(billControlDO);
                            }
                            // 如果交易流水状态未知,订单状态00-成功，30-未知
                            if ("30".equals(_status)) {
                                // 更新交易流水记录,为成功-00
                                billOrderDO.setStatus("00");
                                billOrderListOfNotSure.add(billOrderDO);
                            }
                        } // 状态不对，错账处理--状态不一致
                        else if (("99".equals(_status) && "0000".equals(_yzf_status))
                            || ("00".equals(_status) && (!"0000".equals(_yzf_status)))) {
                            // 错账处理--状态不一致
                            billDetailDO.setWrong_reason_flag(BillConstants.WRONG_FLAG_N);
                            billDetailList.add(billDetailDO);

                            // 交易流水对账失败
                            billOrderDO.setReconciliationStatus(BillStatus.BILL_FAIL.getStatus());
                            // 错账笔数累加
                            addWrongCount(billControlDO);

                        } else if (("30".equals(_status)) && (!"0000".equals(_yzf_status))) {
                            // 渠道失败，系统未知状态,以渠道状态为准,但不进错账
                            billDetailDO.setWrong_reason_flag(BillConstants.WRONG_FLAG_N);
                            // 流水记录标记对账失败
                            billOrderDO.setReconciliationStatus(BillStatus.BILL_FAIL.getStatus());
                            // 更新交易流水记录,为失败-99
                            billOrderDO.setStatus("99");
                            billOrderListOfNotSure.add(billOrderDO);
                        } else {
                            // 其他情况不处理,错账记录默认成功
                            billDetailDO.setWrong_reason_flag(BillConstants.SUCESS_FLAG);
                            // 不处理记录，流水记录标记对账失败
                            billOrderDO.setReconciliationStatus(BillStatus.BILL_FAIL.getStatus());
                        }
                    } else {
                        // 文件存在而数据库不存在
                        // 因为无法区分文件中存在而数据库中不存在的数据的商户类型， 所以不统计_fileDiffCount
                        // 错账处理---渠道多记录
                        billDetailDO.setWrong_reason_flag(BillConstants.WRONG_FLAG_F);
                        billDetailList.add(billDetailDO);
                    }

                }
            }
            // 最后map中留下的是 数据库存在而对账文件中缺失的数据 迭代map 并且置入最后的数据库存在而文件不存在的数据
            Set set = dbMap.entrySet();
            Iterator<Map.Entry<MapBytesEntry, byte[]>> iterator = set.iterator();
            while (iterator.hasNext()) {
                Map.Entry<MapBytesEntry, byte[]> entry = iterator.next();
                // 默认错账记录
                billDetailDO = new BillDetailDO(super.getChannelCode(), DateUtils.formatDate(billDate, "yyyyMMdd"),
                    getBillDateFileName(billDate));
                billDetailDO.setBat_id(bat_id + "");
                billDetailDO.setWrong_reason_flag(BillConstants.WRONG_FLAG_D);
                billDetailList.add(billDetailDO);

                // 数据库数据
                String _dbMapKey = new String(entry.getKey().getValue(), "utf-8");
                String billStrDetail[] = _dbMapKey.split(";", -1);
                String _trx_id = billStrDetail[0];
                String _mapValue = new String(entry.getValue(), "utf-8");
                String _mapValueList[] = _mapValue.split(";", -1);
                String _cust_card_no = _mapValueList[0];// 银行卡号
                String _amount = _mapValueList[1];// 金额
                String _mcht_no = _mapValueList[2];// 商户号
                String _channel_time = _mapValueList[3];// 渠道时间
                String _status = _mapValueList[5];// 渠道状态

                // 添加数据库数据进错账记录
                billDetailDO.setDatabase_trx_id(_trx_id);
                billDetailDO.setCust_card_no(_cust_card_no);
                billDetailDO.setDb_amount(_amount);
                billDetailDO.setMcht_no(_mcht_no);
                billDetailDO.setChannel_time(_channel_time);
                billDetailDO.setDbOrderStatus(_status);

                // 交易流水，状态更改对象
                billOrderDO = new BillOrderDO();
                billOrderList.add(billOrderDO);
                billOrderDO.setTrx_id(_trx_id);// 交易流水号
                billOrderDO.setReconciliationStatus(BillStatus.BILL_FAIL.getStatus());

                // 获取该商户，对账对象
                billControlDO = getOrPutBillControlDO(_mcht_no, billDate, bat_id);
                // 数据库交易金额累加
                addTotalBillsAmount(billControlDO, _amount);
                // 数据库存在而文件不存在数量累加
                addSurplusDbCount(billControlDO);
            }
        } catch (Exception e) {
            throw new BillCompareException(e);
        } finally {
            fileList = null;
            dbMap = null;
        }

    }

}
