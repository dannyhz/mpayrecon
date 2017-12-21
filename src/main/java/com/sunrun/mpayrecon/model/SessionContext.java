package com.sunrun.mpayrecon.model;

import java.util.HashMap;
import java.util.Map;

public class SessionContext {
	
	private Object contextData;
	private boolean execSucc;
	private String retCode;
	private String retMsg;
	
	private Map<String, String> paramMap = new HashMap<String, String>();

	private Map<String, Object> storeObjMap = new HashMap<String, Object>();
	
	public Map<String, String> putValue(String key, String value){
		if(key == null){
			return null;
		}
		paramMap.put(key, value);
		return paramMap;
	} 
	
	public String getValue(String key){
		if(key == null){
			return null;
		}
		return paramMap.get(key);
	} 
	
	public boolean isExecSucc() {
		return execSucc;
	}
	public void setExecSucc(boolean execSucc) {
		this.execSucc = execSucc;
	}
	public String getRetMsg() {
		return retMsg;
	}
	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}
	public String getRetCode() {
		return retCode;
	}
	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}
	public Object getContextData() {
		return contextData;
	}
	public void setContextData(Object contextData) {
		this.contextData = contextData;
	}
	
	public Object getObject(String objKey){
		return storeObjMap.get(objKey);
	}

	public Object putObject(String objKey, Object obj){
		return storeObjMap.put(objKey, obj);
	}
	
	public void mergeMap(Map<String, String> newMap){
		paramMap.putAll(newMap);
	}
	
}
