package com.sunrun.mpayrecon.service;

public class SharedService {
	
	public MyOrderService getMyOrderService(){
		return new MyOrderService();
	}
	

}