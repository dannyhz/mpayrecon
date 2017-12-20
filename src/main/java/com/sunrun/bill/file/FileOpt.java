package com.sunrun.bill.file;

import java.util.Date;
import java.util.Map;

import com.sunrun.bill.exception.BillFileOptException;

/**
 * 文件获取数据
 * @author Administrator
 *
 */
public interface FileOpt {
	public Map<String,Object> queryFileList(Date date) throws BillFileOptException;
	
	public String getFileName();
	
	public String getDate();
}
