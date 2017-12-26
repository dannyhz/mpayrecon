package com.sunrun.mpayrecon.dao.slave;

import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sunrun.mpayrecon.model.TxnOrder;

public class ITxnOrderDaoTest {
	//bat2_cmp_mpos_dtl
	//select * from bat2_cmp_chl_dtl where channel_no = 30 and trtm < '2017-11-03 10:31:31' and trtm > '2017-11-01 10:46:03' 
	@Test
	public void suppose_query_BAT2_CMP_MPOS_DTL_successful(){
	
		 ApplicationContext act = new ClassPathXmlApplicationContext(new String[] { "config/bill-context.xml" });

		 ITxnOrderDao iTxnOrderDao = act.getBean(ITxnOrderDao.class);
		 
		 List<TxnOrder>  txnOrderList =  iTxnOrderDao.queryTxnOrderByTimeAndChannel("2017-11-10 00:00:00", "2017-11-10 01:00:00", "10");
		 System.out.println(txnOrderList.size());
		 
		for(TxnOrder order:txnOrderList){
			System.out.println(order.getMY_MCH_RATE());
		}
		 
	}

}
