package com.sunrun.bill.service;

import java.util.List;
import java.util.Map;

import com.sunrun.bill.model.BillControlDO;
import com.sunrun.bill.model.BillDetailDO;
import com.sunrun.bill.model.BillOrderDO;

public interface IBillService {

    List<BillOrderDO> queryData(String date);

    List<String> queryDataString(String channelCode, String dateStrMin, String dateStrMax, String trx_id_flag);

    boolean insertControl(BillControlDO controlDO);

    long insertDetailList(List<BillDetailDO> list, int foreign_key, String bat_id);

    void updateOrderBillStatus(List<BillOrderDO> list);

    void updateOrderStatus(List<BillOrderDO> list);

    boolean insertBillControlDetail(BillDetailDO billDetailDO);

    /**
     * 向数据库保存对账数据.
     * 
     * @param billControlMap
     * @param billDetailList
     * @param billOrderList
     */
    void saveBillData(Map<String, BillControlDO> billControlMap, List<BillDetailDO> billDetailList,
        List<BillOrderDO> billOrderList, List<BillOrderDO> billOrderListOfNotSure);

}
