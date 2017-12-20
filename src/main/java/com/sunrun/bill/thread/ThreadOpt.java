package com.sunrun.bill.thread;

import java.util.Date;

public interface ThreadOpt {
	public Object threadRun(Date date,String channelCode) throws Exception;
}
