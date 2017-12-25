package com.sunrun.mpayrecon.processor;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.sunrun.mpayrecon.model.SessionContext;
import com.sunrun.mpayrecon.model.TxnOrder;
import com.sunrun.mpayrecon.service.MyOrderService;
import com.sunrun.mpayrecon.service.SharedService;
import com.sunrun.mpayrecon.testbase.TestBase;

public class ReconProcessorTest extends TestBase{
	
	@Test
	public void execute_test(){
		
		ReconProcessor processor = new ReconProcessor();
		
		SessionContext sessionContext = new SessionContext(); 
		SharedService sharedService = new SharedService(); 
		
		MyOrderService myOrderService = Mockito.mock(MyOrderService.class);
		List<TxnOrder> txnOrderList1 = new ArrayList<TxnOrder>();   
		Mockito.when(myOrderService.getMySuccessOrders("20171225", "000000", "010000", "30")).thenReturn(txnOrderList1);
		
		sessionContext.putValue("startTime", "000000");
		sessionContext.putValue("endTime", "010000");
		sessionContext.putValue("checkDate", "20171225");
		sessionContext.putValue("channelNo", "30");
	
		processor.execute(sessionContext, sharedService);
		
		List<TxnOrder> txnOrderList2 = new ArrayList<TxnOrder>();   
		Mockito.when(myOrderService.getMySuccessOrders("20171225", "010000", "020000", "30")).thenReturn(txnOrderList2);
		
		sessionContext.putValue("startTime", "010000");
		sessionContext.putValue("endTime", "020000");
		sessionContext.putValue("checkDate", "20171225");
		sessionContext.putValue("channelNo", "30");
		
		processor.execute(sessionContext, sharedService);
		
		
	}

}
