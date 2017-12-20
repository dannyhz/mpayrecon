package citic.hz.mpos.flow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public abstract class AbstractApiSvlt extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected final static Logger log = Logger.getLogger(new Object(){}.getClass().getEnclosingClass().getName());
	
	 @Override
    public void init(ServletConfig config) throws ServletException {
          //获取初始值username
		log.info("init方法执行");
    }
	
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
		log.info("doGet方法执行");
      }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("req:"+request.getRequestURI());
		JSONObject respJson = null;
		try {
			String charset = "utf-8";
			BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream(), charset));
			StringBuilder sb = new StringBuilder();
			char[] buff = new char[2048];
			int cnt = 0;
			while((cnt = in.read(buff))!=-1){
				sb.append(buff,0,cnt);
			}
			JSONObject reqJson = (JSONObject)JSONValue.parse(sb.toString());
			log.debug("req json:"+reqJson);
			String action = reqJson.getString("_action");
			
			if(null == action || action.trim().length()==0)
				respJson = defaultAction(request,response,reqJson);
			else{
				Method mAction = this.getClass().getMethod(action,HttpServletRequest.class,HttpServletResponse.class,JSONObject.class );
				respJson = (JSONObject)mAction.invoke(this,request, response,reqJson);
			}
		} catch (RuntimeException e) {
			log.error("error -999",e);
			respJson = new JSONObject().append("_respcd", "-999").append("_respMsg", e.getMessage());
		} catch (NoSuchMethodException e) {
			log.error("error -998",e);
			respJson = new JSONObject().append("_respcd", "-998").append("_respMsg", "action not implement");
		} catch (IllegalAccessException e) {
			log.error("error -997",e);
			respJson = new JSONObject().append("_respcd", "-997").append("_respMsg", "action not valid");
		} catch (InvocationTargetException e) {
			log.error("error -996",e.getTargetException());
			respJson = new JSONObject().append("_respcd", "-996").append("_respMsg", "action execute failed");
		} finally{
			if(null == respJson)
				respJson = new JSONObject().append("_respcd", "-995").append("_respMsg", "service unavailable");
			log.debug("resp json:"+respJson);
			OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream(),"utf-8");
			out.write(respJson.toString());
			out.flush();
			out.close();
		}
	}
	
	protected JSONObject defaultAction(HttpServletRequest request, HttpServletResponse response ,JSONObject reqJson) throws ServletException, IOException{
		throw new RuntimeException("default action not implemented");
	}
	
}
