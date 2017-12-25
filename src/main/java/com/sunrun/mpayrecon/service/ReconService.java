package com.sunrun.mpayrecon.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sunrun.mpayrecon.model.ChannelOrder;
import com.sunrun.mpayrecon.model.MainMerchant;
import com.sunrun.mpayrecon.model.ReconFailRecord;
import com.sunrun.mpayrecon.model.ReconResult;
import com.sunrun.mpayrecon.model.ReconSuccessRecord;
import com.sunrun.mpayrecon.model.TxnOrder;

public class ReconService {
	
	/**
	 * 对账主逻辑
	 * @param txnOrders 我方本时间片成功交易 
	 * @param channelOrders  渠道方本时间片成功交易 
	 * @param txnFailOrders  我方本时间片失败交易 
	 * @param reconResult  上次的reconResult， 内含 上次我方和渠道方没有匹配上的 交易 
	 * @return
	 * @throws Exception
	 */
	public ReconResult recon(List<TxnOrder> txnOrders, List<ChannelOrder> channelOrders, List<TxnOrder> txnFailOrders, ReconResult reconResult) throws Exception{
		
		Map<String, ChannelOrder> channelOrderMap  = directMakeChannelOrderListConvertToMap(channelOrders);
		
		List<TxnOrder> matchOrders = new ArrayList<TxnOrder>();
		List<TxnOrder> notMatchOrders = new ArrayList<TxnOrder>();
		
		//第一轮
		//1.把我方流水交易 跟 渠道方流水比较，如果 流水号 金额 类型一致 ，就放到 List matchOrder , 如果只有交易 流水号一致，其他两个不一致， 就把交易放到List notMatchOrder
		//并且把 两边的map都先删除这明确对上和没对上的交易
		//比较完后把 当前 我方以及渠道比较没有对上的记录放入reconResult
				
		if(reconResult.getOddTxnOrdersHistory() != null){
			txnOrders.addAll(reconResult.getOddTxnOrdersHistory());
			reconResult.getOddTxnOrdersHistory().clear();
		}
		if(reconResult.getOddChannelOrdersHistory() != null){
			channelOrders.addAll(reconResult.getOddChannelOrdersHistory());
			reconResult.getOddChannelOrdersHistory().clear();
		}
		
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
		
		//2.反过来， 以渠道方做循环，渠道方多的过滤出来
		Map<String, TxnOrder> myOrderMap  = directMakeMyOrderListConvertToMap(txnOrders);
		for(ChannelOrder channelOrder : channelOrders){
			if(!myOrderMap.containsKey(channelOrder.getMY_ORDER_ID())){
				channelOrder.setCKFG("1");
			}			
		}
		//只要order id 匹配都放到 TmpOrders
		List<TxnOrder> tmpOrders = new ArrayList<TxnOrder>();
		
		
		//得到历史多余交易记录
		
		//我方多的留在 myOrderOddList  , 渠道方多的 留在 channelOrderOddList  
		List<TxnOrder> myOddOrderList = new ArrayList<TxnOrder>();
		//把历史的我方多余未对上记录并入这次未对上的记录数组内
		if(reconResult.getOddTxnOrdersHistory() != null){
			myOddOrderList.addAll(reconResult.getOddTxnOrdersHistory());
		}
		//清理第一轮数据， 把数据源对上 0 -4 -3 都放到TMP_RESULT中
		for(TxnOrder txnOrder : txnOrders){
			if("0".equals(txnOrder.getCKFG()) || "-4".equals(txnOrder.getCKFG()) || "-3".equals(txnOrder.getCKFG())){
				tmpOrders.add(txnOrder);
			}
			//我方多的 放到MyOddOrderList
			else if("2".equals(txnOrder.getCKFG())){
				myOddOrderList.add(txnOrder);
			}
		}
		//渠道多的放到channelOddOrderList
		List<ChannelOrder> channelOddOrderList = new ArrayList<ChannelOrder>();
		//把历史的渠道多余未对上记录并入这次未对上的记录数组内
		if(reconResult.getOddChannelOrdersHistory() != null){
			channelOddOrderList.addAll(reconResult.getOddChannelOrdersHistory());
		}
		for(ChannelOrder channelOrder : channelOrders){
			if("1".equals(channelOrder.getCKFG())){
				channelOddOrderList.add(channelOrder);
			}
			else{
				channelOrder.setCKFG("-7");
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
				if(txnOrder.getTRTM().equals(channelOrder.getTRTM()) && txnOrder.getTRAM().equals(channelOrder.getTRAM()) && txnOrder.getTRTP().equals("21")){
					txnOrder.setCKFG("0");
				}else if(!txnOrder.getTRTM().equals(channelOrder.getTRTM()) ){
					txnOrder.setCKFG("-4");
				}else if(!txnOrder.getTRAM().equals(channelOrder.getTRAM()) ){
					txnOrder.setCKFG("-3");
				}else if(!txnOrder.getTRTP().equals("21")){
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
			else{
				channelOrder.setCKFG("-7");//REL_ORDER_ID  渠道方存在的，这条要移除， 在下面的逻辑里移除
			}
		}
		
		//放到Tmp_result
		Iterator<TxnOrder> txnOrderIt = myOddOrderList.iterator();
		while(txnOrderIt.hasNext()){
			TxnOrder txnOrder = txnOrderIt.next();
			if("0".equals(txnOrder.getCKFG()) || "-3".equals(txnOrder.getCKFG())  || "-4".equals(txnOrder.getCKFG()) || "-6".equals(txnOrder.getCKFG())){
				tmpOrders.add(txnOrder);
				txnOrderIt.remove();
			}
		}
		//移除REL_ORDER_ID 匹配上的渠道 交易Order
		Iterator<ChannelOrder> channelOrderIt = channelOddOrderList.iterator();
		while(channelOrderIt.hasNext()){
			ChannelOrder channelOrder = channelOrderIt.next();
			if(!"1".equals(channelOrder.getCKFG()) ){
				channelOrderIt.remove();
			}
		}
		
		//第三轮 以渠道方多出来的记录为循环
		Map<String, TxnOrder> failTxnOrderMap = directMakeMyOrderListConvertToMap(txnFailOrders);
		Iterator<ChannelOrder> oddChannelOrderIt = channelOddOrderList.iterator();
		while(oddChannelOrderIt.hasNext()){
			ChannelOrder channelOrder = oddChannelOrderIt.next();
			//如果在失败的记录里面找到了这条orderId, 说明这是在我方失败，而在渠道方交易成功的记录，需要把这条放到匹配成功的列表里
			if(failTxnOrderMap.containsKey(channelOrder.getMY_ORDER_ID())){
				
				TxnOrder txnOrder = failTxnOrderMap.get(channelOrder.getMY_ORDER_ID());
				if(txnOrder.getTRTP().equals(channelOrder.getTRTP()) && txnOrder.getTRAM().equals(channelOrder.getTRAM())){
					txnOrder.setCKFG("0");	
				}else if(!txnOrder.getTRTP().equals(channelOrder.getTRTP())){
					txnOrder.setCKFG("-4");	
				}else if(!txnOrder.getTRAM().equals(channelOrder.getTRAM())){
					txnOrder.setCKFG("-3");	
				}
				//oddChannelOrderList保存是 渠道方 真正匹配不上的记录
				oddChannelOrderIt.remove();
				
			}
			
		}
		
		//处理原来在我方失败的交易，把里面做成功的交易放到tmpResult中，然后从失败列表交易中删除
		Iterator<TxnOrder> iterFailTxnOrders = txnFailOrders.iterator();
		while(iterFailTxnOrders.hasNext()){
			TxnOrder txnOrder = iterFailTxnOrders.next();
			if("0".equals(txnOrder.getCKFG()) || "-4".equals(txnOrder.getCKFG()) || "-3".equals(txnOrder.getCKFG()) ){
				tmpOrders.add(txnOrder);
				iterFailTxnOrders.remove();	
			}
		}
		
		//把本切片所有对上帐的放到 matchOrder，等待插入数据库表BAT2_CMP_RESULT, 其他只有orderid或者 rel_order_id相同，但是金额或类型不同的放入 BAT2_CMP_RESULT_FAIL
		for(TxnOrder txnOrder:tmpOrders){
			if("0".equals(txnOrder.getCKFG())){
				matchOrders.add(txnOrder);
			}else{
				notMatchOrders.add(txnOrder);
			}
		}
		//比较完后， 把对上的记录放到  success records, 没对上的放入 fail records
		reconResult.setSuccessRecords(convertToReconSuccessRecords(matchOrders));
		
//		List<ReconFailRecord> txnOddOrderToReconFailRecordList = convertToReconFailRecords(myOddOrderList);
//		
//		Map<String, MainMerchant> mainMerchantMap = new HashMap<String, MainMerchant>();
//		List<ReconFailRecord> channelOddOrderToReconFailRecordList = convertToReconFailRecords(channelOddOrderList, mainMerchantMap);
		
		
		List<ReconFailRecord> failRecordList = convertToReconFailRecords(notMatchOrders);
//		if(txnOddOrderToReconFailRecordList != null){
//			failRecordList.addAll(txnOddOrderToReconFailRecordList);
//		}
//		if(channelOddOrderToReconFailRecordList != null){
//			failRecordList.addAll(channelOddOrderToReconFailRecordList);
//		}
		
		reconResult.setFailRecords(failRecordList);
		
		//比较完后把 当前 我方以及渠道比较没有对上的记录放入reconResult
		reconResult.setOddTxnOrdersHistory(myOddOrderList);
		reconResult.setOddChannelOrdersHistory(channelOddOrderList);
		
		return reconResult;
	}
	
	public List<ReconSuccessRecord> convertToReconSuccessRecords(List<TxnOrder> source){
		List<ReconSuccessRecord> reconSuccessRecords = new ArrayList<ReconSuccessRecord>();
		for(TxnOrder txnOrder : source){
			ReconSuccessRecord successRecord = new ReconSuccessRecord();
			successRecord.setBANK_CODE(txnOrder.getBANK_CODE());
			successRecord.setMY_ORDER_ID(txnOrder.getMY_ORDER_ID());
			successRecord.setTRAM(txnOrder.getTRAM());
			successRecord.setTRTM(txnOrder.getTRTM());
			reconSuccessRecords.add(successRecord);
		}
		return reconSuccessRecords;
	}
	
	public List<ReconFailRecord> convertToReconFailRecords(List<TxnOrder> source){
		
		List<ReconFailRecord> reconFailRecords = new ArrayList<ReconFailRecord>();
		for(TxnOrder txnOrder : source){
			ReconFailRecord failRecord = new ReconFailRecord();
			failRecord.setBANK_CODE(txnOrder.getBANK_CODE());
			failRecord.setMY_ORDER_ID(txnOrder.getMY_ORDER_ID());
			failRecord.setTRAM(txnOrder.getTRAM());
			failRecord.setTRTM(txnOrder.getTRTM());
			reconFailRecords.add(failRecord);
		}
		return reconFailRecords;
		
	}
	
	public List<ReconFailRecord> convertToReconFailRecords(List<ChannelOrder> source, Map<String, MainMerchant> mainMerchantMap){
		
		List<ReconFailRecord> reconFailRecords = new ArrayList<ReconFailRecord>();
		for(ChannelOrder channelOrder : source){
			ReconFailRecord failRecord = new ReconFailRecord();
			MainMerchant mainMerchant = mainMerchantMap.get(channelOrder.getMCH_NO());
			failRecord.setBANK_CODE(mainMerchant.getBANK_CODE());
			failRecord.setMY_ORDER_ID(channelOrder.getMY_ORDER_ID());
			failRecord.setTRAM(channelOrder.getTRAM());
			failRecord.setTRTM(channelOrder.getTRTM());
			reconFailRecords.add(failRecord);
		}
		return reconFailRecords;
		
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
