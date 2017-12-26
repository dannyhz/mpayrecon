package com.sunrun.mpayrecon.processor;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sunrun.mpayrecon.constants.ReconConstants;
import com.sunrun.mpayrecon.dao.slave.IChannelOrderDao;
import com.sunrun.mpayrecon.model.ChannelOrder;
import com.sunrun.mpayrecon.model.ReconResult;
import com.sunrun.mpayrecon.model.SessionContext;
import com.sunrun.mpayrecon.model.TxnOrder;
import com.sunrun.mpayrecon.service.ChannelOrderService;
import com.sunrun.mpayrecon.service.ReconService;
import com.sunrun.mpayrecon.service.SharedService;
import com.sunrun.mpayrecon.service.TxnOrderService;
import com.sunrun.mpayrecon.testbase.TestBase;

import org.junit.Assert;

public class ReconProcessorTest extends TestBase{
	
	@Test
	public void execute_test(){
		
		ReconProcessor processor = new ReconProcessor();
		
		SessionContext sessionContext = new SessionContext(); 
		SharedService sharedService = Mockito.mock(SharedService.class); 
		
		TxnOrderService myOrderService = Mockito.mock(TxnOrderService.class);
		
		ChannelOrderService channelOrderService = Mockito.mock(ChannelOrderService.class);
		
		Mockito.when(sharedService.getTxnOrderService()).thenReturn(myOrderService);
		
		Mockito.when(sharedService.getChannelOrderService()).thenReturn(channelOrderService);
		
		ReconService reconService = new ReconService();
		
		Mockito.when(sharedService.getReconService()).thenReturn(reconService);
		
		
		List<TxnOrder> txnOrderList = new ArrayList<TxnOrder>();   
		
		TxnOrder txnOrder1 = generateTxnOrder();
		txnOrder1.setMY_ORDER_ID("200001");
		txnOrderList.add(txnOrder1);
		TxnOrder txnOrder2 = generateTxnOrder();
		txnOrder2.setMY_ORDER_ID("200002");
		txnOrder2.setTRAM("100");
		txnOrderList.add(txnOrder2);
		TxnOrder txnOrder3 = generateTxnOrder();
		txnOrder3.setMY_ORDER_ID("200003");
		txnOrder3.setTRTP("01");
		txnOrderList.add(txnOrder3);
		TxnOrder txnOrder4 = generateTxnOrder();
		txnOrder4.setMY_ORDER_ID("200004");
		txnOrderList.add(txnOrder4);
		TxnOrder txnOrder7 = generateTxnOrder();
		txnOrder7.setMY_ORDER_ID("200007_txn");
		txnOrder7.setREL_ORDER_ID("200007");
		txnOrder7.setTRTP("21");
		txnOrderList.add(txnOrder7);
		TxnOrder txnOrder9 = generateTxnOrder();
		txnOrder9.setMY_ORDER_ID("200009");
		txnOrderList.add(txnOrder9);
		
		Mockito.when(myOrderService.getMySuccessOrders("20171225", "000000", "010000", "30")).thenReturn(txnOrderList);
		
		List<TxnOrder> txnFailOrders = new ArrayList<TxnOrder>();//我方失败
		TxnOrder txnOrder6 = generateTxnOrder();
		txnOrder6.setMY_ORDER_ID("200006");
		txnFailOrders.add(txnOrder6);
		Mockito.when(myOrderService.getMyFailOrders("20171225", "000000", "010000", "30")).thenReturn(txnFailOrders);
		
		
		List<ChannelOrder> channelOrders = new ArrayList<ChannelOrder>();
		ChannelOrder channelOrder1 = generateChannelOrder();
		channelOrder1.setMY_ORDER_ID("200001");
		channelOrders.add(channelOrder1);
		ChannelOrder channelOrder2 = generateChannelOrder();
		channelOrder2.setMY_ORDER_ID("200002");
		channelOrder2.setTRAM("101");
		channelOrders.add(channelOrder2);
		ChannelOrder channelOrder3 = generateChannelOrder();
		channelOrder3.setMY_ORDER_ID("200003");
		channelOrder3.setTRTP("04");
		channelOrders.add(channelOrder3);
		ChannelOrder channelOrder5 = generateChannelOrder();
		channelOrder5.setMY_ORDER_ID("200005");
		channelOrders.add(channelOrder5);
		ChannelOrder channelOrder6 = generateChannelOrder();//渠道成功
		channelOrder6.setMY_ORDER_ID("200006");
		channelOrders.add(channelOrder6);
		ChannelOrder channelOrder7 = generateChannelOrder();//渠道成功REL_ORDER_ID 存在
		channelOrder7.setMY_ORDER_ID("200007_chl");
		channelOrder7.setREL_ORDER_ID("200007");
		channelOrder7.setTRTP("21");
		channelOrders.add(channelOrder7);
		ChannelOrder channelOrder8 = generateChannelOrder();//渠道成功REL_ORDER_ID 存在
		channelOrder8.setMY_ORDER_ID("200008");
		channelOrders.add(channelOrder8);
		
		Mockito.when(channelOrderService.getChannelOrders("20171225", "000000", "010000", "30")).thenReturn(channelOrders);
		
		sessionContext.putValue("startTime", "000000");
		sessionContext.putValue("endTime", "010000");
		sessionContext.putValue("checkDate", "20171225");
		sessionContext.putValue("channelNo", "30");
	
		processor.execute(sessionContext, sharedService);
		
		if(sessionContext.isExecSucc()){
			ReconResult reconResult = (ReconResult)sessionContext.getObject(ReconConstants.RECON_RESULT);
			Assert.assertEquals(3, reconResult.getSuccessRecords().size());
			Assert.assertEquals(2, reconResult.getFailRecords().size());
			Assert.assertEquals(2, reconResult.getOddTxnOrdersHistory().size());
			Assert.assertEquals(2, reconResult.getOddChannelOrdersHistory().size());
		}else{
			Assert.assertTrue(false);
		}
		
		List<TxnOrder> txnOrders_piece_2 = new ArrayList<TxnOrder>();
		TxnOrder txnOrder10 = generateTxnOrder();
		txnOrder10.setMY_ORDER_ID("200010");
		txnOrders_piece_2.add(txnOrder10);
		
		Mockito.when(myOrderService.getMySuccessOrders("20171225", "010000", "020000", "30")).thenReturn(txnOrders_piece_2);
		
		List<ChannelOrder> channelOrders_piece_2 = new ArrayList<ChannelOrder>();
		ChannelOrder channelOrder9 = generateChannelOrder();
		channelOrder9.setMY_ORDER_ID("200009");
		channelOrders_piece_2.add(channelOrder9);
		ChannelOrder channelOrder10 = generateChannelOrder();
		channelOrder10.setMY_ORDER_ID("200010");
		channelOrders_piece_2.add(channelOrder10);
		
		Mockito.when(channelOrderService.getChannelOrders("20171225", "010000", "020000", "30")).thenReturn(channelOrders_piece_2);
		
		List<TxnOrder> txnFailOrders_piece_2 = new ArrayList<TxnOrder>();//我方失败 piece 2

		Mockito.when(myOrderService.getMyFailOrders("20171225", "010000", "020000", "30")).thenReturn(txnFailOrders_piece_2);
		
		sessionContext.putValue("startTime", "010000");
		sessionContext.putValue("endTime", "020000");
		sessionContext.putValue("checkDate", "20171225");
		sessionContext.putValue("channelNo", "30");
	
		processor.execute(sessionContext, sharedService);
		
		if(sessionContext.isExecSucc()){
			ReconResult reconResult = (ReconResult)sessionContext.getObject(ReconConstants.RECON_RESULT);
			Assert.assertEquals(2, reconResult.getSuccessRecords().size());
			Assert.assertEquals(0, reconResult.getFailRecords().size());
			Assert.assertEquals(1, reconResult.getOddTxnOrdersHistory().size());
			Assert.assertEquals(2, reconResult.getOddChannelOrdersHistory().size());
		}else{
			Assert.assertTrue(false);
		}
		
	}

	@Test
	public void integrate_test(){
		
		ApplicationContext act = new ClassPathXmlApplicationContext(new String[] { "config/bill-context.xml" });

		ReconProcessor reconProcessor = act.getBean(ReconProcessor.class);
		 
		SharedService services = act.getBean(SharedService.class);
		 
		SessionContext sessionContext = new SessionContext();
		sessionContext.putValue("startTime","00:00:00");
		sessionContext.putValue("endTime","01:00:00");
		sessionContext.putValue("checkDate","2017-11-10");
		sessionContext.putValue("channelNo","10");
		 
		reconProcessor.execute(sessionContext, services);
		 
	}
	

}
