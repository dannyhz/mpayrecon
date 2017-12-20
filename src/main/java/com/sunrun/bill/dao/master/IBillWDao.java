package com.sunrun.bill.dao.master;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sunrun.bill.model.BillControlDO;
import com.sunrun.bill.model.BillDetailDO;
import com.sunrun.bill.model.BillOrderDO;

public interface IBillWDao {

    long insertDetailList(@Param("list") List<BillDetailDO> list, @Param("foreign_key") int foreign_key,
        @Param("bat_id") String bat_id);

    void insertControl(BillControlDO controlDO);

    void updateOrderBillStatus(BillOrderDO billOrderDO);

    void updateOrderStatus(BillOrderDO billOrderDO);

    void insertBillControlDetail(BillDetailDO billDetailDO);

}
