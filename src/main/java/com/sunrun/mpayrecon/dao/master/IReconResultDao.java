package com.sunrun.mpayrecon.dao.master;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sunrun.mpayrecon.model.ReconSuccessRecord;

public interface IReconResultDao {

    void insertReconSuccessRecordList(@Param("list") List<ReconSuccessRecord> list);

    void insertReconSuccessRecord(ReconSuccessRecord successRecord);

//    void insertControl(BillControlDO controlDO);
//
//    void updateOrderBillStatus(BillOrderDO billOrderDO);
//
//    void updateOrderStatus(BillOrderDO billOrderDO);
//
//    void insertBillControlDetail(BillDetailDO billDetailDO);

    
    public ReconSuccessRecord querySuccessRecord(@Param("order_id")String orderId);
}
