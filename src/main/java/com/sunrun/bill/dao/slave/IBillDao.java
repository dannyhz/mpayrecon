package com.sunrun.bill.dao.slave;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sunrun.bill.model.BillOrderDO;



public interface IBillDao {
	
	List<BillOrderDO> queryData();
	
	List<String> queryDataString(@Param("channelCode")String channelCode,@Param("dateStrMin")String dateStrMin,@Param("dateStrMax")String dateStrMax,@Param("trxIdFlag")String trxIdFlag);

}
