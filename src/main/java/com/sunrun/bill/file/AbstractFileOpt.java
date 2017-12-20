package com.sunrun.bill.file;

import java.util.Date;

import com.sunrun.bill.thread.ThreadOpt;

public abstract class AbstractFileOpt implements FileOpt, ThreadOpt {

	/**
	 * 线程执行入口接口 的实现
	 */
	public Object threadRun(Date date,String channelCode) throws Exception{
		return queryFileList(date);
	}
	
	
}
