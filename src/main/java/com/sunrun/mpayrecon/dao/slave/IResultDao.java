package com.sunrun.mpayrecon.dao.slave;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import citic.hz.mpos.service.dao.po.ResultOrder;

public interface IResultDao {

	List<ResultOrder> queryTxn(@Param("id")String id);

}
