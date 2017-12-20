package com.sunrun.bill.db;

import java.util.Date;
import java.util.HashMap;

import com.sunrun.bill.exception.BillDbOptException;
import com.sunrun.util.MapBytesEntry;

/**
 * 数据库操作接口
 * @author Administrator
 *
 */
public interface DbOpt {
	
	/**
	 * 以HashMap<MapBytesEntry,byte[]>的形式返回数据库读取内容
	 * @param String date 日期 yyyyMMdd 20161111
	 * @return
	 */
	public HashMap<MapBytesEntry,byte[]> queryDbMap(Date date,String channelCode) throws BillDbOptException;
	
	/**
	 * 回写对账结果到数据库
	 * @return
	 */
	public Boolean updateBillResult() throws BillDbOptException;
}
