package com.sunrun.bill.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunrun.bill.dao.master.IBillWDao;
import com.sunrun.bill.dao.slave.IBillDao;
import com.sunrun.bill.model.BillControlDO;
import com.sunrun.bill.model.BillDetailDO;
import com.sunrun.bill.model.BillOrderDO;
import com.sunrun.bill.service.IBillService;

@Service
public class BillServiceImpl implements IBillService {

    protected static final Logger logger = LoggerFactory.getLogger(BillServiceImpl.class);

    @Resource
    IBillDao billDao;

    @Resource
    IBillWDao billWDao;

    @Override
    public List<BillOrderDO> queryData(String date) {
        return billDao.queryData();
    }

    @Override
    public List<String> queryDataString(String channelCode, String dateStrMin, String dateStrMax, String trxIdFlag) {

        // Date date = null;
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // try {
        // date = sdf.parse(dateStrMin);
        // } catch (ParseException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // Calendar calendar = new GregorianCalendar();
        // calendar.setTime(date);
        // calendar.set(Calendar.HOUR, -24);
        // dateStrMin = new SimpleDateFormat("yyyy-MM-dd
        // HH:mm:ss").format(calendar.getTime());
        return billDao.queryDataString(channelCode, dateStrMin, dateStrMax, trxIdFlag);
    }

    @Override
    public boolean insertControl(BillControlDO controlDO) {
        // TODO Auto-generated method stub
        // billWDao.insertControl(BillControlDO controlDO);
        billWDao.insertControl(controlDO);
        return Boolean.TRUE;
    }

    @Override
    public long insertDetailList(List<BillDetailDO> list, int foreign_key, String bat_id) {
        // TODO Auto-generated method stub
        billWDao.insertDetailList(list, foreign_key, bat_id);
        return 0;
    }

    @Override
    public void updateOrderBillStatus(List<BillOrderDO> list) {
        for (BillOrderDO billOrderDO : list) {
            billWDao.updateOrderBillStatus(billOrderDO);
        }
    }

    @Override
    public void updateOrderStatus(List<BillOrderDO> list) {
        for (BillOrderDO billOrderDO : list) {
            billWDao.updateOrderStatus(billOrderDO);
        }
    }

    @Override
    public boolean insertBillControlDetail(BillDetailDO billDetailDO) {
        billWDao.insertBillControlDetail(billDetailDO);
        return Boolean.TRUE;
    }

    @Override
    @Transactional
    public void saveBillData(Map<String, BillControlDO> billControlMap, List<BillDetailDO> billDetailList,
        List<BillOrderDO> billOrderList, List<BillOrderDO> billOrderListOfNotSure) {
        // 对账单表写入数据库
        Set<Entry<String, BillControlDO>> billControlSet = billControlMap.entrySet();
        Iterator<Entry<String, BillControlDO>> billControls = billControlSet.iterator();
        BillControlDO billControlDO = null;
        // <商户号,billControlDO的ID>
        Map<String, Long> billControlDOIdMap = new HashMap<String, Long>();

        while (billControls.hasNext()) {
            Entry<String, BillControlDO> entry = billControls.next();
            billControlDO = entry.getValue();
            billControlDO.setTotalBillsAccount(billControlDO.getBalanceBillsAccount()
                + billControlDO.getWrongBillsAccount() + billControlDO.getDbBillsAccount());
            // 显示插入前的记录
            logger.info("插入bill_control前:{}",
                ToStringBuilder.reflectionToString(billControlDO, ToStringStyle.SHORT_PREFIX_STYLE));
            billWDao.insertControl(billControlDO);
            billControlDOIdMap.put(billControlDO.getMchtNo(), billControlDO.getId());
        }

        // 错账记录写入数据库
        if (billDetailList != null && billDetailList.size() > 0) {
            for (BillDetailDO billDetailDO : billDetailList) {
                if (billControlDOIdMap.containsKey(billDetailDO.getMcht_no())) {
                    // 如果包含该商户号，则是数据库存在的错账记录,否则是文件存在，数据库不存在的错账记录
                    billDetailDO.setControl_id(billControlDOIdMap.get(billDetailDO.getMcht_no()) + "");
                }
                // 显示插入前的记录
                logger.info("插入bill_detail前:{}",
                    ToStringBuilder.reflectionToString(billDetailDO, ToStringStyle.SHORT_PREFIX_STYLE));
                billWDao.insertBillControlDetail(billDetailDO);
            }
        }

        // 更新交易流水对账状态
        if (null != billOrderList && billOrderList.size() > 0) {
            updateOrderBillStatus(billOrderList);
        }

        // 更新状态为未知的交易流水状态
        if (null != billOrderListOfNotSure && billOrderListOfNotSure.size() > 0) {
            updateOrderStatus(billOrderListOfNotSure);
        }

    }

}
