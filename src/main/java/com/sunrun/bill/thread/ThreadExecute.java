package com.sunrun.bill.thread;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class ThreadExecute extends Thread{
	private final CountDownLatch latch;
	private final ThreadOpt targetObj;
	private Object returnObj;
	private Throwable throwable;
	private Date date;
	private String channelCode;
	
	public ThreadExecute(CountDownLatch latch,ThreadOpt targetObj,Date date,String channelCode){
		this.latch=latch;
		this.targetObj=targetObj;
		this.date=date;
		this.channelCode=channelCode;
	}
	
	public void run(){
		try {
			returnObj = targetObj.threadRun(date,channelCode);
		} catch (Exception e) {
			throwable = e;
		} finally {
			latch.countDown();
		}
	}
	
	public Object getReturnObj(){
		  return returnObj;
	}
	
	public Throwable getThrowable() {
		return throwable;
	}
	
	public ThreadOpt getTargetObj() {
		return targetObj;
	}
	
	
	
}
