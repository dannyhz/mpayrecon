package com.sunrun.mpayrecon.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sunrun.mpayrecon.dao.slave.IChannelOrderDao;
import com.sunrun.mpayrecon.model.ChannelOrder;
import com.sunrun.mpayrecon.model.ReconFailRecord;
import com.sunrun.mpayrecon.model.ReconResult;
import com.sunrun.mpayrecon.model.ReconSuccessRecord;
import com.sunrun.mpayrecon.model.TxnOrder;

public class ChannelOrderServiceTest {
	
	@Test
	public void suppose_call_getChannelOrders_successful(){
		 ApplicationContext act = new ClassPathXmlApplicationContext(new String[] { "config/bill-context.xml" });

		 ChannelOrderService channelOrderService = act.getBean(ChannelOrderService.class);
		 
		 List<ChannelOrder> channelOrderList = channelOrderService.getChannelOrders("2016-07-27", "09:00:00", "10:00:00", "10");
		 
		 System.out.println(channelOrderList.size());
		 
	}
	
}
