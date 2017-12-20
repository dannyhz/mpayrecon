package com.sunrun.mpayrecon.bill.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sunrun.bill.model.BillControlDO;
import com.sunrun.bill.model.BillDetailDO;
import com.sunrun.bill.model.BillOrderDO;

import citic.hz.mpos.service.dao.po.BillingOrder;

public interface ITxnDao {

	List<BillingOrder> queryTxn(@Param("txnId")String txnId);

}
