package com.sunrun.mpayrecon.dao.slave;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sunrun.mpayrecon.model.ChannelOrder;

import citic.hz.mpos.service.dao.po.ResultOrder;

public interface IChannelOrderDao {

	List<ChannelOrder> queryChannelTxn(@Param("id")String id);

}
