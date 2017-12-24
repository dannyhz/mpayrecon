package com.sunrun.mpayrecon.bill.dao;

import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sunrun.bill.dao.slave.IBillDao;
import com.sunrun.bill.model.BillOrderDO;

import citic.hz.mpos.service.dao.po.BillingOrder;
import citic.hz.mpos.service.dao.po.ResultOrder;

public class TxnDaoTest {
	
	@Test
	public void getTxn(){
	
		 ApplicationContext act = new ClassPathXmlApplicationContext(new String[] { "config/bill-context.xml" });

		 IBillDao billDao =	act.getBean(IBillDao.class);
		 List beans =  billDao.queryData();
		 System.out.println(beans.size());
		 
		 
	     ITxnDao dao = (ITxnDao) act.getBean(ITxnDao.class);
	     
	     List<BillingOrder> list =  dao.queryTxn("4200000016201712122543998901");
	     
	     System.out.println(list.size());
	     
	     System.out.println(((BillingOrder)list.get(0)).getTPAM_TXN_ID()); 
	     System.out.println(((BillingOrder)list.get(0)).getTRAM());
	     
	}
	
	@Test
	public void getResult(){
	
		 ApplicationContext act = new ClassPathXmlApplicationContext(new String[] { "config/bill-context.xml" });

		 IResultDao dao = (IResultDao) act.getBean(IResultDao.class);
	     
	     //List<ResultOrder> list =  dao.queryTxn("1");
		 List<ResultOrder> list =  dao.queryTxn("1477859");
	     System.out.println(list.size());
	     
	     System.out.println(((ResultOrder)list.get(0)).getMY_ORDER_ID()); 
	     
	}

}
