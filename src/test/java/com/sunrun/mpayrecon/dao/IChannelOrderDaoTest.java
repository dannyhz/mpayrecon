package com.sunrun.mpayrecon.dao;

import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sunrun.mpayrecon.dao.slave.IChannelOrderDao;
import com.sunrun.mpayrecon.model.ChannelOrder;
import com.sunrun.mpayrecon.testbase.TestBase;

public class IChannelOrderDaoTest {

	
	
	@Test
	public void suppose_query_list_successful(){
		 ApplicationContext act = new ClassPathXmlApplicationContext(new String[] { "config/bill-context.xml" });

		 IChannelOrderDao iChannelDao = act.getBean(IChannelOrderDao.class);
		 
		 List<ChannelOrder> channelOrderList =  iChannelDao.queryChannelTxn("68882801");
		 System.out.println(channelOrderList.get(0).getPAYBANK()); 
		 
		
	}
	

}
