package citic.hz.mpos.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import citic.hz.mpos.kit.Config;

public class XytHttpService {
	
	protected final static Logger log = Logger.getLogger(new Object(){}.getClass().getEnclosingClass().getName());
	//配置
	protected static Config config = Config.getConfig();
	//调用渠道，区分信e通微网站、信e达等
	protected static String channel = "mposbat";
	
	/**
	 * 组合成最终的服务URL
	 * @return
	 */
	public static String getXytServiceUrl(String serviceResource){
		return config.get("service_url_base")+serviceResource;
	}
	
	public static void send2xyt(String requrl,JSONObject req){
		try {
			//push traceNo to req
			//req.put("_traceNo", MDC.get("traceNo"));
			log.debug(req.toString());
			
			URL url = new URL(requrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);	//POST
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			log.debug("post to "+requrl+" ...");
			conn.connect();

			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(),"UTF-8");
			out.write( req.toString());
			out.flush();
			out.close();
			log.debug(conn.getResponseCode()+" "+conn.getResponseMessage());
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
	}
	
	public static JSONObject req2xyt(String requrl,JSONObject req){
		try {
			//push traceNo to req
			//req.put("_traceNo", MDC.get("traceNo"));
			log.debug(req.toString());
			
			URL url = new URL(requrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);	//POST
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			log.debug("post to "+requrl+" ...");
			conn.connect();

			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(),"UTF-8");
			out.write( req.toString());
			out.flush();
			out.close();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			StringBuilder sb = new StringBuilder();
			char[] buff = new char[2048];
			int cnt = 0;
			while((cnt = in.read(buff))!=-1)
				sb.append(buff,0,cnt);
			in.close();
			log.debug(conn.getResponseCode()+" "+conn.getResponseMessage());
			log.debug("resp:"+sb.toString());
			JSONObject respJo = (JSONObject)JSONValue.parse(sb.toString());
			return respJo;
		} catch (IOException e) {
			log.error(e.getMessage(),e);
			throw new RuntimeException(e);
		}
	}

	

}
