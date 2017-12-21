package com.sunrun.mpayrecon.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sunrun.mpayrecon.model.ChannelOrder;
import com.sunrun.mpayrecon.model.ReconResult;
import com.sunrun.mpayrecon.model.TxnOrder;

import citic.hz.mpos.service.dao.po.BillingOrder;


public class ReconService {
	
	
	public ReconResult recon(List<TxnOrder> txnOrders, List<ChannelOrder> channelOrders, ReconResult reconResult) throws Exception{
		
		Map<String, ChannelOrder> channelOrderMap  = directMakeChannelOrderListConvertToMap(channelOrders);
		
		List<TxnOrder> matchOrders = new ArrayList<TxnOrder>();
		List<TxnOrder> notMatchOrders = new ArrayList<TxnOrder>();
		
		//第一轮
		//1.把我方流水交易 跟 渠道方流水比较，如果 流水号 金额 类型一致 ，就放到 List matchOrder , 如果只有交易 流水号一致，其他两个不一致， 就把交易放到List notMatchOrder
		//并且把 两边的map都先删除这明确对上和没对上的交易
		for(TxnOrder txnOrder : txnOrders){
			
			if(channelOrderMap.containsKey(txnOrder.getMY_ORDER_ID())){
				ChannelOrder channelOrder = channelOrderMap.get(txnOrder.getMY_ORDER_ID());
				if(txnOrder.getTRTP().equals(channelOrder.getTRTP()) && txnOrder.getTRAM().equals(channelOrder.getTRAM())){
					txnOrder.setCKFG("0");//三要素匹配
					//matchOrders.add(txnOrder);
				}else if(!txnOrder.getTRTP().equals(channelOrder.getTRTP())){
					txnOrder.setCKFG("-4");//类型不一样
					//notMatchOrders.add(txnOrder);
				}else if(!txnOrder.getTRAM().equals(channelOrder.getTRAM())){
					txnOrder.setCKFG("-3");//金额不一样
					//notMatchOrders.add(txnOrder);
				}else{
					System.out.println("!!!!! What !  Round 1,  it is impossible!");
				}
				
			}else{
				txnOrder.setCKFG("2");//我方多
			}
						
		}
		
		List<ChannelOrder> channelOrderOddList = new ArrayList<ChannelOrder>(); 
		//2.反过来， 以渠道方做循环，渠道方多的过滤出来
		Map<String, TxnOrder> myOrderMap  = directMakeMyOrderListConvertToMap(txnOrders);
		for(ChannelOrder channelOrder : channelOrders){
			if(!myOrderMap.containsKey(channelOrder.getMY_ORDER_ID())){
				channelOrder.setCKFG("1");
				//channelOrderOddList.add(channelOrder);
			}			
		}
		//只要order id 匹配都放到 TmpOrders
		List<TxnOrder> tmpOrders = new ArrayList<TxnOrder>();
		//我方多的留在 myOrderOddList  , 渠道方多的 留在 channelOrderOddList  
		List<TxnOrder> myOddOrderList = new ArrayList<TxnOrder>();
		//清理第一轮数据， 把数据源对上 0 -4 -3 都放到TMP_RESULT中
		for(TxnOrder txnOrder : txnOrders){
			if(txnOrder.getCKFG().equals("0") || txnOrder.getCKFG().equals("-4") || txnOrder.getCKFG().equals("-3")){
				tmpOrders.add(txnOrder);
			}
			//我方多的 放到MyOddOrderList
			else if(txnOrder.getCKFG().equals("2")){
				myOddOrderList.add(txnOrder);
			}
		}
		//渠道多的放到channelOddOrderList
		List<ChannelOrder> channelOddOrderList = new ArrayList<ChannelOrder>();
		for(ChannelOrder channelOrder : channelOrders){
			if(channelOrder.getCKFG().equals("1")){
				channelOddOrderList.add(channelOrder);
			}
		}
		
		//到现在为止， 对上 order_id的 都放在 tmpOrders
		//我方多的 放在  MyOddOrderList
		//渠道方多的 放在 channelOddOrderList
		
		
		//第二轮  把剩下我方多和渠道方多的记录 进行REL_ORDER_ID的比较
		Map<String, TxnOrder> myOddOrderMapForRelOrderId = directMakeMyOrderListConvertToMapWithRelOrderId(myOddOrderList);
		Map<String, ChannelOrder> channelOddOrderMapForRelOrderId = directMakeChannelOrderListConvertToMapWithRelOrderId(channelOddOrderList); 
		
		for(TxnOrder txnOrder : myOddOrderList){
			if(channelOddOrderMapForRelOrderId.containsKey(txnOrder.getREL_ORDER_ID())){
				ChannelOrder channelOrder = channelOddOrderMapForRelOrderId.get(txnOrder.getREL_ORDER_ID());
				if(txnOrder.getTRTM().equals(channelOrder.getTRTM()) && txnOrder.getTRAM().equals(channelOrder.getTRAM()) && !txnOrder.getTRTP().equals("21")){
					txnOrder.setCKFG("0");
				}else if(!txnOrder.getTRTM().equals(channelOrder.getTRTM()) ){
					txnOrder.setCKFG("-4");
				}else if(!txnOrder.getTRAM().equals(channelOrder.getTRAM()) ){
					txnOrder.setCKFG("-3");
				}else if(txnOrder.getTRTP().equals("21")){
					txnOrder.setCKFG("-6");
				}else{
					System.out.println("!!!!What! round 2 , it is impossible! ");
				}
			}else{
				txnOrder.setCKFG("2");
			}
		}
		
		for(ChannelOrder channelOrder : channelOddOrderList){
			if(!myOddOrderMapForRelOrderId.containsKey(channelOrder.getREL_ORDER_ID())){
				channelOrder.setCKFG("1");	
			}
		}
		
		
		return null;
	}
	

	public Map<String, TxnOrder> directMakeMyOrderListConvertToMap(List<TxnOrder> source) throws Exception{
		HashMap<String, TxnOrder> hm = new HashMap<String, TxnOrder>();
		for(TxnOrder order : source){
			hm.put(order.getMY_ORDER_ID(), order);
		}
		return hm;
	}
	
	public Map<String, ChannelOrder> directMakeChannelOrderListConvertToMap(List<ChannelOrder> source) throws Exception{
		HashMap<String, ChannelOrder> hm = new HashMap<String, ChannelOrder>();
		for(ChannelOrder order : source){
			hm.put(order.getMY_ORDER_ID(), order);
		}
		return hm;
	}
	
	public Map<String, TxnOrder> directMakeMyOrderListConvertToMapWithRelOrderId(List<TxnOrder> source) throws Exception{
		HashMap<String, TxnOrder> hm = new HashMap<String, TxnOrder>();
		for(TxnOrder order : source){
			if(order.getREL_ORDER_ID() != null && order.getREL_ORDER_ID().length() > 0){
				hm.put(order.getREL_ORDER_ID(), order);
			}
		}
		return hm;
	}
	
	public Map<String, ChannelOrder> directMakeChannelOrderListConvertToMapWithRelOrderId(List<ChannelOrder> source) throws Exception{
		HashMap<String, ChannelOrder> hm = new HashMap<String, ChannelOrder>();
		for(ChannelOrder order : source){
			if(order.getREL_ORDER_ID() != null && order.getREL_ORDER_ID().length() > 0){
				hm.put(order.getREL_ORDER_ID(), order);
			}
		}
		return hm;
	}
	
	

}
