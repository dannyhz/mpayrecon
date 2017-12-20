/*
 * 创建日期 2013-5-10
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package citic.hz.mpos.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import citic.hz.mpos.kit.Globle;

/**
 * @author phio
 *
 */
public class SmsNotifyService {
	
	private static final Logger log=Logger.getLogger(SmsNotifyService.class);
	private static final LinkedBlockingQueue<JSONObject> que=new LinkedBlockingQueue<JSONObject>(1000);
	private static String url = XytHttpService.getXytServiceUrl("smsapi");
	
	/**
	 * 发送SMS消息
	 */
	public static void offerMsg(String msg){
		if(null == msg)
			msg = "可能发生空指针错误";
		//returning immediately if this queue is full
		String smsNotifyMobileNos = Globle.config.get("smsNotifyMobileNos");
		String [] mobs = smsNotifyMobileNos.split("\\|",-1);
		for(String mob : mobs){
			JSONObject pm = new JSONObject();
			pm.put("tel",mob);
			pm.put("txtmsg",msg);
			pm.put("appname", "mposbat");	//TODO
			que.offer(pm);
		}
	}

	/**
	 * 发送SMS消息
	 */
	public static void offerMsg(String msg,String mobNos){
		if(null == msg)
			msg = "可能发生空指针错误";
		//returning immediately if this queue is full
		String [] mobs = mobNos.split("\\|",-1);
		for(String mob : mobs){
			JSONObject pm = new JSONObject();
			pm.put("tel",mob);
			pm.put("txtmsg",msg);
			pm.put("appname", "mposbat");	//TODO
			que.offer(pm);
		}
	}

	public static void init(){
		log.info("Start SmsNotify Service Lisenter..."+url);
		ExecutorService es=Executors.newSingleThreadExecutor();
		es.execute(new Runnable() {
			public void run() {
				while(true){
					try {
						JSONObject pm=(JSONObject)que.take();
						sendinfo(pm);
					} catch (Throwable e) {
						log.error(e.getMessage(),e);
					}
				}
			}
		});		
	}
	
	/*
	 * 将msg发送
	 */
	private static void sendinfo(JSONObject pm){
		//XytHttpService.send2xyt(url, pm);
	}
}
