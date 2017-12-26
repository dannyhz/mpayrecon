package com.sunrun.mpayrecon.dao.slave;

import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sunrun.mpayrecon.model.TxnOrder;

import org.junit.Assert;

public class ITxnFailOrderDaoTest {
	//bat2_cmp_mpos_dtl_fail
	//select * from bat2_mpos_chl_dtl_fail where channel_no = 30 and trtm < '2017-11-03 10:31:31' and trtm > '2017-11-01 10:46:03' 
	@Test
	public void suppose_query_BAT2_CMP_MPOS_DTL_successful(){
	
		 ApplicationContext act = new ClassPathXmlApplicationContext(new String[] { "config/bill-context.xml" });

		 ITxnFailOrderDao iTxnFailOrderDao = act.getBean(ITxnFailOrderDao.class);
		 
		 List<TxnOrder>  txnOrderList =  iTxnFailOrderDao.queryTxnFailOrderByTimeAndChannel("2017-11-10 00:00:00", "2017-11-10 01:00:00", "10");
		 System.out.println(txnOrderList.size());
		 
		for(TxnOrder order:txnOrderList){
			System.out.println(order.getMY_MCH_RATE());
		}
		
		Assert.assertEquals(3, txnOrderList.size());
		 
	}

}
