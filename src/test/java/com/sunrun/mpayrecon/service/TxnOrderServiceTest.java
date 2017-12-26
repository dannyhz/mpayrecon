package com.sunrun.mpayrecon.service;

import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sunrun.mpayrecon.model.TxnOrder;

public class TxnOrderServiceTest {
	
	@Test
	public void suppose_call_getTxnOrders_successful(){
		 ApplicationContext act = new ClassPathXmlApplicationContext(new String[] { "config/bill-context.xml" });

		 TxnOrderService txnOrderService = act.getBean(TxnOrderService.class);
		 
		 List<TxnOrder> txnOrderList = txnOrderService.getMySuccessOrders("2017-11-10", "00:00:00", "01:00:00", "10");
		 
		 System.out.println(txnOrderList.size());
		 
	}
	
	@Test
	public void suppose_call_getTxnFailOrders_successful(){
		 ApplicationContext act = new ClassPathXmlApplicationContext(new String[] { "config/bill-context.xml" });

		 TxnOrderService txnOrderService = act.getBean(TxnOrderService.class);
		 
		 List<TxnOrder> txnFailOrderList = txnOrderService.getMyFailOrders("2017-11-10", "00:00:00", "01:00:00", "10");
		 
		 System.out.println(txnFailOrderList.size());
		 
	}
}
