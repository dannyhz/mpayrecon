package citic.hz.mpos.kit;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;

import org.apache.log4j.Logger;

import citic.hz.mpos.service.WxNotifyService;

/**
 * 用于线程池中子线程出错将主线程中断的场景
 * @author phio
 *
 */
public class ExceptionHandlerThreadFactory implements ThreadFactory{
	
	private static final Logger log = Logger.getLogger(ExceptionHandlerThreadFactory.class);
	
	private ThreadFactory defaultThreadFactory;
	private Thread parent;
	private UncaughtExceptionHandler exceptionHandler;
	
	public ExceptionHandlerThreadFactory(ThreadFactory defaultThreadFactory,Thread parent) {
		this.defaultThreadFactory = defaultThreadFactory;
		this.parent = parent;
		this.exceptionHandler = new exceptionHandler();
	}

	@Override
	public Thread newThread(Runnable arg0) {
		Thread t = defaultThreadFactory.newThread(arg0);
		t.setUncaughtExceptionHandler(exceptionHandler);
		return t;
	}
	
    class exceptionHandler implements UncaughtExceptionHandler{
    	
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			log.error("["+t.getName()+"]"+e.getMessage(),e);
			try{
				WxNotifyService.offerMsg(e.getMessage());
			}catch(Throwable ignore){}	
			parent.interrupt();
		}
    	
    }
	
	
}
