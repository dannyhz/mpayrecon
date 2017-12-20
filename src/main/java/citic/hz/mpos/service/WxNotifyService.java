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
import citic.hz.phio.kit.PhioH;

/**
 * @author phio
 *
 */
public class WxNotifyService {
	
	private static final Logger log=Logger.getLogger(WxNotifyService.class);
	private static final LinkedBlockingQueue<JSONObject> que=new LinkedBlockingQueue<JSONObject>(1000);
	private static String url = XytHttpService.getXytServiceUrl("wxpush");
	
	/**
	 * 发送系统消息
	 */
	public static void offerMsg(String msg){
		if(null == msg)
			msg = "可能发生空指针错误";
		//returning immediately if this queue is full
		String wxNotifyOpenids = Globle.config.get("wxNotifyOpenids");
		String [] openids = wxNotifyOpenids.split(",",-1);
		for(String openid : openids){
			JSONObject pm = new JSONObject();
			pm.put("_action","templatePushByOpenid");
			pm.put("openid",openid);
			pm.put("first","移动收单对账提醒");
			pm.put("keyword1",PhioH.nowDate("yyyyMMdd HH:mm:ss"));
			pm.put("keyword2","");
			pm.put("remark",msg);
			que.offer(pm);
		}
	}

	/**
	 * 发送业务消息
	 */
	public static void offerBizMsg(String msg){
		if(null == msg)
			msg = "可能发生空指针错误";
		//returning immediately if this queue is full
		String wxNotifyOpenids = Globle.config.get("wxNotifyBizOpenids");
		String [] openids = wxNotifyOpenids.split(",",-1);
		for(String openid : openids){
			JSONObject pm = new JSONObject();
			pm.put("_action","templatePushByOpenid");
			pm.put("openid",openid);
			pm.put("first","移动收单对账提醒");
			pm.put("keyword1",PhioH.nowDate("yyyyMMdd HH:mm:ss"));
			pm.put("keyword2","");
			pm.put("remark",msg);
			que.offer(pm);
		}
	}
	
	/**
	 * 发送CHECK类信息
	 * @param msg
	 */
	public static void offerCheckMsg(String msg){
		if(null == msg)
			msg = "可能发生空指针错误";
		//returning immediately if this queue is full
		String wxNotifyOpenids = Globle.config.get("wxNotifyCheckOpenids");
		String [] openids = wxNotifyOpenids.split(",",-1);
		for(String openid : openids){
			JSONObject pm = new JSONObject();
			pm.put("_action","templatePushByOpenid");
			pm.put("openid",openid);
			pm.put("first","移动收单CHECK");
			pm.put("keyword1",PhioH.nowDate("yyyyMMdd HH:mm:ss"));
			pm.put("keyword2","");
			pm.put("remark",msg);
			que.offer(pm);
		}
	}

	
	public static void init(){
		log.info("Start WxNotify Service Lisenter..."+url);
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
