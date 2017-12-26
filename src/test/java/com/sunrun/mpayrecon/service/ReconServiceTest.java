package com.sunrun.mpayrecon.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.sunrun.mpayrecon.model.ChannelOrder;
import com.sunrun.mpayrecon.model.ReconFailRecord;
import com.sunrun.mpayrecon.model.ReconResult;
import com.sunrun.mpayrecon.model.ReconSuccessRecord;
import com.sunrun.mpayrecon.model.TxnOrder;

public class ReconServiceTest {
	/**
	 *  第一时间片
	 *  
	 * 200001 两边匹配 
	 * 200002 金额不同
	 * 200003 类型不同
	 * 200004 我方多
	 * 200005 渠道方多
	 * 200006 我方失败  渠道实际成功
	 * 200007 REL_ORDER_ID相同  交易类型是21  撤销
	 * 200008 渠道多 并且在下一个时间片也没有匹配的数据
	 * 200009 我方多 但是会在下一轮有匹配的数据
	 * 
	 * 
	 * 我方 成功
	 * order_id   txn_amt     txn_type     Rel_ORDER_ID
	 * 200001     100 	      01    
	 * 200002	  100		  01
	 * 200003	  100		  01
	 * 200004	  100		  01
	 * 200007     100		  21   			200007
	 * 200009	  100         01
	 * 
	 * 渠道方 成功
	 * order_id   txn_amt     txn_type     Rel_ORDER_ID
	 * 200001     100 	      01    
	 * 200002	  101		  01
	 * 200003	  100	      00
	 * 200005	  100		  01
	 * 200006     100		  01
	 * 			  100		  21			200007
	 * 200008     100         01             
	 * 
	 * 
	 * 我方 失败 
	 * order_id   txn_amt     txn_type
	 * 200006     100		  01
	 * 
	 * 
	 * match_list 
	 * 200001
	 * 200006
	 * 200007
	 * 
	 * not match list
	 * 200002
	 * 200003
	 * 200004
	 * 200005
	 * 200008
	 * 200009
	 * 
	 * myOddList
	 * 200004
	 * 200008
	 * 
	 * channelOddList
	 * 200005
	 * 200009
	 * 
	 * 
	 * 
	 * 
	 * 第二个时间片 
	 * 
	 * 200009 渠道来了一笔  是与上个时间片可以匹配起来的数据
	 * 200010 两边都来了匹配的 ， 能匹配上
	 * 
	 * 
	 * 我方成功
	 * order_id   txn_amt     txn_type     Rel_ORDER_ID
	 * 200010     100 	      01    
	 * 
	 * 渠道方
	 * order_id   txn_amt     txn_type     Rel_ORDER_ID
	 * 200009     100 	      01    
	 * 200010	  100		  01
	 * 
	 * 我方失败
	 *  order_id   txn_amt     txn_type     Rel_ORDER_ID
	 *  
	 *  
	 *  ----------PIECE 1 ---------
		success order:
		order id : 200001
		order id : 200007_txn
		order id : 200006
		fail order:
		order id : 200002
		order id : 200003
		odd txn order in history:
		order id : 200004
		order id : 200009
		odd channel order in history:
		order id : 200005
		order id : 200008
		----------PIECE 2 ---------
		success order:
		order id : 200010
		order id : 200009
		fail order:
		odd txn order in history:
		order id : 200004
		odd channel order in history:
		order id : 200005
		order id : 200008
	 *  
	 */
	@Test
	public void test_recon_sample1(){
		ReconService reconService = new ReconService();
		List<TxnOrder> txnOrders = new ArrayList<TxnOrder>();
		TxnOrder txnOrder1 = generateTxnOrder();
		txnOrder1.setMY_ORDER_ID("200001");
		txnOrders.add(txnOrder1);
		TxnOrder txnOrder2 = generateTxnOrder();
		txnOrder2.setMY_ORDER_ID("200002");
		txnOrder2.setTRAM("100");
		txnOrders.add(txnOrder2);
		TxnOrder txnOrder3 = generateTxnOrder();
		txnOrder3.setMY_ORDER_ID("200003");
		txnOrder3.setTRTP("01");
		txnOrders.add(txnOrder3);
		TxnOrder txnOrder4 = generateTxnOrder();
		txnOrder4.setMY_ORDER_ID("200004");
		txnOrders.add(txnOrder4);
		TxnOrder txnOrder7 = generateTxnOrder();
		txnOrder7.setMY_ORDER_ID("200007_txn");
		txnOrder7.setREL_ORDER_ID("200007");
		txnOrder7.setTRTP("21");
		txnOrders.add(txnOrder7);
		TxnOrder txnOrder9 = generateTxnOrder();
		txnOrder9.setMY_ORDER_ID("200009");
		txnOrders.add(txnOrder9);
		
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
		
		List<TxnOrder> txnFailOrders = new ArrayList<TxnOrder>();//我方失败
		TxnOrder txnOrder6 = generateTxnOrder();
		txnOrder6.setMY_ORDER_ID("200006");
		txnFailOrders.add(txnOrder6);
		
		ReconResult reconResult = new ReconResult();
		
		try {
			reconService.recon(txnOrders, channelOrders, txnFailOrders, reconResult);
			
			System.out.println("----------PIECE 1 ---------");
			System.out.println("success order:");
			for(ReconSuccessRecord successRecord : reconResult.getSuccessRecords()){
				System.out.println("order id : " + successRecord.getMY_ORDER_ID());
			}
			Assert.assertEquals(3, reconResult.getSuccessRecords().size());
			
			System.out.println("fail order:");			
			for(ReconFailRecord failRecord : reconResult.getFailRecords()){
				System.out.println("order id : " + failRecord.getMY_ORDER_ID());
			}
			Assert.assertEquals(2, reconResult.getFailRecords().size());
			
			System.out.println("odd txn order in history:");
			for(TxnOrder txnOrder : reconResult.getOddTxnOrdersHistory()){
				System.out.println("order id : " + txnOrder.getMY_ORDER_ID());
			}
			Assert.assertEquals(2, reconResult.getOddTxnOrdersHistory().size());
			
			System.out.println("odd channel order in history:");
			for(ChannelOrder channelOrder : reconResult.getOddChannelOrdersHistory()){
				System.out.println("order id : " + channelOrder.getMY_ORDER_ID());
			}
			Assert.assertEquals(2, reconResult.getOddChannelOrdersHistory().size());
			
			
			reconResult.getSuccessRecords().clear();
			reconResult.getFailRecords().clear();
			
			
			List<TxnOrder> txnOrders_piece_2 = new ArrayList<TxnOrder>();
			TxnOrder txnOrder10 = generateTxnOrder();
			txnOrder10.setMY_ORDER_ID("200010");
			txnOrders_piece_2.add(txnOrder10);
			
			
			List<ChannelOrder> channelOrders_piece_2 = new ArrayList<ChannelOrder>();
			ChannelOrder channelOrder9 = generateChannelOrder();
			channelOrder9.setMY_ORDER_ID("200009");
			channelOrders_piece_2.add(channelOrder9);
			ChannelOrder channelOrder10 = generateChannelOrder();
			channelOrder10.setMY_ORDER_ID("200010");
			channelOrders_piece_2.add(channelOrder10);
			
			List<TxnOrder> txnFailOrders_piece_2 = new ArrayList<TxnOrder>();//我方失败 piece 2
			
			reconService.recon(txnOrders_piece_2, channelOrders_piece_2, txnFailOrders_piece_2, reconResult);
			
			System.out.println("----------PIECE 2 ---------");
			
			System.out.println("success order:");
			for(ReconSuccessRecord successRecord : reconResult.getSuccessRecords()){
				System.out.println("order id : " + successRecord.getMY_ORDER_ID());
			}
			Assert.assertEquals(2, reconResult.getSuccessRecords().size());
			
			System.out.println("fail order:");			
			for(ReconFailRecord failRecord : reconResult.getFailRecords()){
				System.out.println("order id : " + failRecord.getMY_ORDER_ID());
			}
			Assert.assertEquals(0, reconResult.getFailRecords().size());
			
			System.out.println("odd txn order in history:");
			for(TxnOrder txnOrder : reconResult.getOddTxnOrdersHistory()){
				System.out.println("order id : " + txnOrder.getMY_ORDER_ID());
			}
			Assert.assertEquals(1, reconResult.getOddTxnOrdersHistory().size());
			
			System.out.println("odd channel order in history:");
			for(ChannelOrder channelOrder : reconResult.getOddChannelOrdersHistory()){
				System.out.println("order id : " + channelOrder.getMY_ORDER_ID());
			}
			Assert.assertEquals(2, reconResult.getOddChannelOrdersHistory().size());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	} 
	
	
	@Test
	public void test_recon_only_for_REL_TXN_ID(){
		ReconService reconService = new ReconService();
		List<TxnOrder> txnOrders = new ArrayList<TxnOrder>();
		
		TxnOrder txnOrder7 = generateTxnOrder();
		txnOrder7.setMY_ORDER_ID("200007_txn");
		txnOrder7.setREL_ORDER_ID("200007");
		txnOrder7.setTRTP("21");
		txnOrders.add(txnOrder7);
		
		List<ChannelOrder> channelOrders = new ArrayList<ChannelOrder>();
		
		ChannelOrder channelOrder7 = generateChannelOrder();//渠道成功REL_ORDER_ID 存在
		channelOrder7.setMY_ORDER_ID("200007_chl");
		channelOrder7.setREL_ORDER_ID("200007");
		channelOrder7.setTRTP("21");
		channelOrders.add(channelOrder7);
	
		
		List<TxnOrder> txnFailOrders = new ArrayList<TxnOrder>();//我方失败
		
		
		ReconResult reconResult = new ReconResult();
		
		try {
			reconService.recon(txnOrders, channelOrders, txnFailOrders, reconResult);
			
			System.out.println("success order:");
			for(ReconSuccessRecord successRecord : reconResult.getSuccessRecords()){
				System.out.println("order id : " + successRecord.getMY_ORDER_ID());
			}
			
			System.out.println("fail order:");			
			for(ReconFailRecord failRecord : reconResult.getFailRecords()){
				System.out.println("order id : " + failRecord.getMY_ORDER_ID());
			}
			
			System.out.println("odd txn order in history:");
			for(TxnOrder txnOrder : reconResult.getOddTxnOrdersHistory()){
				System.out.println("order id : " + txnOrder.getMY_ORDER_ID());
			}
			
			System.out.println("odd channel order in history:");
			for(ChannelOrder channelOrder : reconResult.getOddChannelOrdersHistory()){
				System.out.println("order id : " + channelOrder.getMY_ORDER_ID());
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	} 
	
	
	
	private TxnOrder generateTxnOrder(){
		TxnOrder order = new TxnOrder();
		
		order.setTRTM("20171101 100001");
		order.setCHANNEL_NO("30");
		order.setCHL_ORDER_ID("100001");
		order.setMY_ORDER_ID("200001");
		order.setMCH_NO("3001");
		order.setSEC_MCH_NO("31001");
		order.setTRAM("100");
		order.setTRTP("01");
		order.setREL_ORDER_ID("");
		order.setMEMO("");
		order.setCLDT("20171101");
		order.setBNO("101");
		order.setPAYBANK("中信银行");
		
		order.setBRH_ID("80001");
		order.setMY_MCH_NO("3001");
		order.setMY_SEC_MCH_NO("31001");
		order.setTERM_NO("40001");
		order.setTRADE_TYPE("01");
		order.setMCH_ORDER_ID("700001");
		order.setREL_MCH_ORDER_ID("");
		order.setFZFG("N");
		order.setMY_MCH_RATE("0.05");
		order.setMY_SEC_MCH_RATE("0.01");
		order.setD0FG("X");
		order.setTRADE_CODE("01");
		order.setBANK_CODE("302331033102");
		
		return order;
	}

	private ChannelOrder generateChannelOrder(){
		ChannelOrder order = new ChannelOrder();
		
		order.setTRTM("20171101 100001");
		order.setCHANNEL_NO("30");
		order.setCHL_ORDER_ID("100001");
		order.setMY_ORDER_ID("200001");
		order.setMCH_NO("3001");
		order.setSEC_MCH_NO("31001");
		order.setTRAM("100");
		order.setTRTP("01");
		order.setREL_ORDER_ID("");
		order.setMEMO("");
		order.setCLDT("20171101");
		order.setBNO("101");
		order.setPAYBANK("中信银行");
		
		return order;
	}
	
	
	@Test
	public void remove_item_from_list(){
		
		List<ChannelOrder> channelList = new ArrayList<ChannelOrder>();
		
		channelList.add(generateChannelOrder());
		channelList.add(generateChannelOrder());
		channelList.add(generateChannelOrder());
		channelList.add(generateChannelOrder());
		channelList.add(generateChannelOrder());

		Iterator<ChannelOrder> it = channelList.iterator();
		
		while(it.hasNext()){
			ChannelOrder co = it.next();
			it.remove();
		}
		
		System.out.println(channelList.size());
		
		
		
	}
	
}
