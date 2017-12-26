package com.sunrun.mpayrecon.dao.slave;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sunrun.mpayrecon.model.TxnOrder;

import citic.hz.mpos.service.dao.po.ResultOrder;

public interface ITxnOrderDao {

	List<ResultOrder> queryTxn(@Param("id")String id);
	
	//select * from bat2_cmp_chl_dtl where channel_no = 30 and trtm < '2017-11-03 10:31:31' and trtm > '2017-11-01 10:46:03';
	List<TxnOrder> queryTxnOrderByTimeAndChannel(@Param("startDateAndTime")String startDateAndTime, @Param("endDateAndTime")String endDateAndTime, @Param("channelNo")String channelNo);

}
