package com.sunrun.mpayrecon.dao.slave;

import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sunrun.mpayrecon.model.ChannelOrder;
import com.sunrun.mpayrecon.model.TxnOrder;

public class IChannelOrderDaoTest {
	//bat2_channel_dtl
	//select * from bat2_cmp_chl_dtl where channel_no = 30 and trtm < '2017-11-03 10:31:31' and trtm > '2017-11-01 10:46:03' 
	@Test
	public void suppose_query_BAT2_CMP_MPOS_DTL_successful(){
	
		 ApplicationContext act = new ClassPathXmlApplicationContext(new String[] { "config/bill-context.xml" });

		 IChannelOrderDao iChannelOrderDao = act.getBean(IChannelOrderDao.class);
		 
		 List<ChannelOrder>  channelOrderList =  iChannelOrderDao.queryChannelOrderByTimeAndChannel("2017-11-01 00:00:00", "2017-11-01 01:00:00", "05");
		 System.out.println(channelOrderList.size());
		 
		for(ChannelOrder order:channelOrderList){
			System.out.println(order.getCHL_ORDER_ID());
		}
		 
	}
	
	
	@Test
	public void suppose_query_list_successful(){
		 ApplicationContext act = new ClassPathXmlApplicationContext(new String[] { "config/bill-context.xml" });

		 IChannelOrderDao iChannelDao = act.getBean(IChannelOrderDao.class);
		 
		 List<ChannelOrder> channelOrderList =  iChannelDao.queryChannelTxn("68882801");
		 System.out.println(channelOrderList.get(0).getPAYBANK()); 
		 
		
	}

}
