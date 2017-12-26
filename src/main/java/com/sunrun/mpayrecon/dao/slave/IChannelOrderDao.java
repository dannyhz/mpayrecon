package com.sunrun.mpayrecon.dao.slave;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sunrun.mpayrecon.model.ChannelOrder;
import com.sunrun.mpayrecon.model.TxnOrder;

import citic.hz.mpos.service.dao.po.ResultOrder;

public interface IChannelOrderDao {

	List<ChannelOrder> queryChannelTxn(@Param("id")String id);

	//select * from bat2_cmp_chl_dtl  where channel_no = 10 and trtm >= '2016-07-27 09:00:00' and trtm < '2016-07-27 10:00:00'
	List<ChannelOrder> queryChannelOrderByTimeAndChannel(@Param("startDateAndTime")String startDateAndTime, 
			@Param("endDateAndTime")String endDateAndTime, @Param("channelNo")String channelNo);

}
