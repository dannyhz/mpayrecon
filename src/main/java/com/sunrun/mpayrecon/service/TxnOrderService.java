package com.sunrun.mpayrecon.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.sunrun.mpayrecon.dao.slave.ITxnFailOrderDao;
import com.sunrun.mpayrecon.dao.slave.ITxnOrderDao;
import com.sunrun.mpayrecon.model.TxnOrder;

@Service
public class TxnOrderService {
	@Resource
	private ITxnOrderDao txnOrderDao;
	@Resource
	private ITxnFailOrderDao txnFailOrderDao;
	
	public List<TxnOrder> getMySuccessOrders(String checkDate, String startTime, String endTime, String channelNo){
		
		String startDateAndTime = checkDate + " " + startTime;
		String endDateAndTime = checkDate + " " + endTime;
		List<TxnOrder> txnOrderList = txnOrderDao.queryTxnOrderByTimeAndChannel(startDateAndTime, endDateAndTime, channelNo);
		
		return txnOrderList;
	} 


	public List<TxnOrder> getMyFailOrders(String checkDate, String startTime, String endTime, String channelNo){
		
		String startDateAndTime = checkDate + " " + startTime;
		String endDateAndTime = checkDate + " " + endTime;
		List<TxnOrder> failTxnOrderList = txnFailOrderDao.queryTxnFailOrderByTimeAndChannel(startDateAndTime, endDateAndTime, channelNo);
		
		return failTxnOrderList;
	} 
}
