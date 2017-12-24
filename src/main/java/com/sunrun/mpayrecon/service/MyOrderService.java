package com.sunrun.mpayrecon.service;

import java.util.ArrayList;
import java.util.List;

import com.sunrun.mpayrecon.model.TxnOrder;

public class MyOrderService {
	
	
	
	public List<TxnOrder> getMySuccessOrders(String checkDate, String startTime, String endTime, String channelNo){
		return new ArrayList<TxnOrder>();
	} 


	public List<TxnOrder> getMyFailOrders(String checkDate, String startTime, String endTime, String channelNo){
		return new ArrayList<TxnOrder>();
	} 
}
