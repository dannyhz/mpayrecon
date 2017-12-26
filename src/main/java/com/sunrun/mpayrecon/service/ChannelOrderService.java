package com.sunrun.mpayrecon.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.sunrun.mpayrecon.dao.slave.IChannelOrderDao;
import com.sunrun.mpayrecon.model.ChannelOrder;

@Service
public class ChannelOrderService {
	@Resource
	IChannelOrderDao iChannelOrderDao;
	
	public List<ChannelOrder> getChannelOrders(String checkDate, String startTime, String endTime, String channelNo){
		
		String startDateAndTime = checkDate + " " + startTime;
		String endDateAndTime = checkDate + " " + endTime;
		List<ChannelOrder> channelOrderList = iChannelOrderDao.queryChannelOrderByTimeAndChannel(startDateAndTime, endDateAndTime, channelNo);
		return channelOrderList;
		
	} 

}

