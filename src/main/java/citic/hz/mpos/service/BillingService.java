package citic.hz.mpos.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import citic.hz.mpos.service.dao.MPayBillDao;
import citic.hz.mpos.service.dao.po.BillingOrder;
import citic.hz.mpos.service.dao.po.ChannelOrder;
import citic.hz.mpos.service.dao.po.MyOrder;

/**
 * 对账服务
 * @author zhuxiang
 *
 */
public class BillingService {

	private static final Logger log = Logger.getLogger(BillingService.class);
	private MPayBillDao billDao;
	
	public Map<String, BillingOrder> channelOrderMap = new HashMap<String, BillingOrder>();
	public Map<String, BillingOrder> channelOrderMapWithRelOrderId = new HashMap<String, BillingOrder>();
	public Map<String, BillingOrder> myOrderMap = new HashMap<String, BillingOrder>();
	public Map<String, BillingOrder> myOrderMapWithRelOrderId = new HashMap<String, BillingOrder>();
	
	List<BillingOrder> matchOrders = new ArrayList<BillingOrder>();
	List<BillingOrder> notMatchOrders = new ArrayList<BillingOrder>();
	
	/**
	 * 进行渠道对账
	 * @throws Exception 
	 */
	public void billing(String batid, String chlNo) throws Exception{
		log.info("开始渠道对账" + batid+"|"+chlNo);

		List<BillingOrder> channelOrders = billDao.getChannelOrders(chlNo);
		channelOrderMap = directMakeChannelOrderListConvertToMap(channelOrders);
		
		channelOrderMapWithRelOrderId = directMakeChannelOrderListConvertToMapWithRelOrderId(channelOrders);
		
		List<BillingOrder> myOrders = billDao.getMyOrders(chlNo);
		myOrderMap = directMakeMyOrderListConvertToMap(myOrders);
		myOrderMapWithRelOrderId = directMakeMyOrderListConvertToMapWithRelOrderId(myOrders);
		
		
		List<BillingOrder> channelOddOrders = new ArrayList<BillingOrder>();
		List<BillingOrder> myOddOrders = new ArrayList<BillingOrder>();
		//1.把我方流水交易 跟 渠道方流水比较，如果 流水号 金额 类型一致 ，就放到 List matchOrder , 如果只有交易 流水号一致，其他两个不一致， 就把交易放到List notMatchOrder
		//并且把 两边的map都先删除这明确对上和没对上的交易
		for(BillingOrder myOrder : myOrders){
			if(channelOrderMap.containsKey(myOrder.getMY_ORDER_ID()) ){//渠道方记录包含订单号
				BillingOrder chOrder = channelOrderMap.get(myOrder.getMY_ORDER_ID());
				if(myOrder.getTRAM().equals(chOrder.getTRAM()) && myOrder.getTRTP().equals(chOrder.getTRTP())){
					matchOrders.add(myOrder);
				}else{
					notMatchOrders.add(myOrder);//金额或者类型不一样
				}
				channelOrderMap.remove(myOrder.getMY_ORDER_ID());//只要有对上的记录 ， 都从 源 中清除
				myOrderMap.remove(myOrder.getMY_ORDER_ID());//只要有对上的记录 ， 都从 源 中清除
			}else{
				//myOrderMap.remove(myOrder.getMY_ORDER_ID());//没包含，说明是多余的记录
				//notMatchOrders.add(myOrder);
				//myOddOrders.add(myOrder); //是 我方流水中 ， 单独多的记录 
			}
		}
		//2.反过来以 渠道为中心， 跟我方流水进行比较， 如果流水号 金额类型一致，就放到 list matchOrder , 如果只有 交易流水号一致， 其他两个不一致，就把交易放到 list notmatchorder
		//并且把两边的map都删除这对上和没对上的交易
		
		Set entry = channelOrderMap.entrySet();
		Iterator it = entry.iterator();
		while(it.hasNext()){
			Map.Entry<String, BillingOrder> channelOrderPair = (Map.Entry<String, BillingOrder>)it.next();
			String orderId = channelOrderPair.getKey();
			if(myOrderMap.containsKey(orderId)){
				BillingOrder chOrder = channelOrderMap.get(orderId);
				BillingOrder myOrder = myOrderMap.get(orderId);
				if(myOrder.getTRAM().equals(chOrder.getTRAM()) && myOrder.getTRTP().equals(chOrder.getTRTP())){
					matchOrders.add(chOrder);
				}else{
					notMatchOrders.add(chOrder);
				}
				channelOrderMap.remove(orderId);
				myOrderMap.remove(orderId);
			}else{
				//channelOrderMap.remove(orderId);
				//notMatchOrders.add(channelOrderPair.getValue());
			}
		}
		
		Map<String, BillingOrder> myRelOrderMap = new HashMap<String, BillingOrder>();
		Map<String, BillingOrder> chRelOrderMap = new HashMap<String, BillingOrder>();
		//把Rel_order_id 作为key 把之前过滤后的  渠道流水Map和交易流水Map 再生成两个map
		Collection<BillingOrder> chRelOrders = channelOrderMap.values();
		if(chRelOrders != null && chRelOrders.size() > 0){
			for(BillingOrder relOrder:chRelOrders){
				if(relOrder.getREL_ORDER_ID() != null && relOrder.getREL_ORDER_ID().length() > 0){
					chRelOrderMap.put(relOrder.getREL_ORDER_ID(), relOrder);
				}
			}
		}
		Collection<BillingOrder> myRelOrders = myOrderMap.values();
		if(myRelOrders != null && myRelOrders.size() > 0){
			for(BillingOrder relOrder:myRelOrders){
				if(relOrder.getREL_ORDER_ID() != null && relOrder.getREL_ORDER_ID().length() > 0){
					myRelOrderMap.put(relOrder.getREL_ORDER_ID(), relOrder);
				}
			}
		}
		
		Set myRelOrderEntry = myRelOrderMap.entrySet();
		Iterator itForMyRelOrderEntry = myRelOrderEntry.iterator();
		BillingOrder myOrder = null;
		while(itForMyRelOrderEntry.hasNext()){
			Map.Entry<String, BillingOrder> myRelOrderPair = (Map.Entry<String, BillingOrder>)itForMyRelOrderEntry.next();
			String reLOrderId = myRelOrderPair.getKey();
			
			if(chRelOrderMap.containsKey(reLOrderId)){
				BillingOrder chOrder = chRelOrderMap.get(reLOrderId);
				myOrder = myRelOrderMap.get(reLOrderId);
				if(myOrder.getTRAM().equals(chOrder.getTRAM()) && myOrder.getTRTP().equals(chOrder.getTRTP()) ){
					if(myOrder.getTRTP().equals("21")){
						matchOrders.add(myOrder);
						
					}else{
						notMatchOrders.add(myOrder);
					}
					myOrderMap.remove(myOrder.getMY_ORDER_ID());
					channelOrderMap.remove(chOrder.getMY_ORDER_ID());
				}else{
					notMatchOrders.add(myOrder);
				}
			
			}else{
				//chRelOrderMap.remove(reLOrderId);
				//notMatchOrders.add(myOrder);
			}
		}
		
		List<BillingOrder> myFailOrders = billDao.getMyFailOrders(chlNo);
		Map myOrderFailMap = directMakeMyOrderListConvertToMap(myFailOrders);
		
		for(BillingOrder order:myFailOrders){
			BillingOrder missOrder = channelOrderMap.get(order.getMY_ORDER_ID());
			if(missOrder.getTRAM().equals(order.getTRAM()) && missOrder.getTRTP().equals(order.getTRTP())){
				matchOrders.add(order);
				myOrderFailMap.remove(order.getMY_ORDER_ID());
				channelOrderMap.remove(order.getMY_ORDER_ID());
			}
		}
		
		
		//最后一步，把两边多余的记录，都移到差错表
		Collection<BillingOrder> chLeftSet = channelOrderMap.values();
		for(BillingOrder chLeftOrder : chLeftSet){
			notMatchOrders.add(chLeftOrder);
		}
		Collection<BillingOrder> myLeftSet = myOrderMap.values();
		for(BillingOrder myLeftOrder : myLeftSet){
			notMatchOrders.add(myLeftOrder);
		}
		
	}
	
	
	public Map directMakeMyOrderListConvertToMapWithRelOrderId(List<BillingOrder> source) throws Exception{
		HashMap hm = new HashMap();
		for(BillingOrder order : source){
			if(order.getREL_ORDER_ID() != null && order.getREL_ORDER_ID().length() > 0){
				hm.put(order.getREL_ORDER_ID(), order);
			}
		}
		return hm;
	}
	
	public Map directMakeChannelOrderListConvertToMapWithRelOrderId(List<BillingOrder> source) throws Exception{
		HashMap hm = new HashMap();
		for(BillingOrder order : source){
			if(order.getREL_ORDER_ID() != null && order.getREL_ORDER_ID().length() > 0){
				hm.put(order.getREL_ORDER_ID(), order);
			}
		}
		return hm;
	}
	
	public Map directMakeMyOrderListConvertToMap(List<BillingOrder> source) throws Exception{
		HashMap hm = new HashMap();
		for(BillingOrder order : source){
			//hm.put(order.getMY_ORDER_ID(), order.getMY_ORDER_ID()+ "_" + order.getTRAM() + "_" + order.getTRTP());
			hm.put(order.getMY_ORDER_ID(), order);
		}
		return hm;
	}
	
	public Map directMakeChannelOrderListConvertToMap(List<BillingOrder> source) throws Exception{
		HashMap hm = new HashMap();
		for(BillingOrder order : source){
			//hm.put(order.getMY_ORDER_ID(), order.getMY_ORDER_ID()+ "_" + order.getTRAM() + "_" + order.getTRTP());
			hm.put(order.getMY_ORDER_ID(), order);
		}
		return hm;
	}
	
	public Map makeListConvertToMap(List<BillingOrder> source, String keyPropertyName, String valPropertyNames[]) throws Exception{
		
		HashMap<String, String> hm = new HashMap<String, String>();
		
		if(keyPropertyName == null || keyPropertyName.length() == 0){
			return null;
		}
		Field keyField = BillingOrder.class.getDeclaredField(keyPropertyName);
		ArrayList<Field> allValueField = new ArrayList<Field>();
		
		if(keyField == null){
			return null;
		}
		if(valPropertyNames == null){
			return null;
		}else{
			for(String propertyName : valPropertyNames){
				if(propertyName == null || propertyName.length() == 0){
					return null;
				}
			}
		}
		
		ArrayList valueFieldList = new ArrayList<Field>();
		for(String valuePropertyName:valPropertyNames){
			if(valuePropertyName == null){
				return null;
			}
			Field valueField = MyOrder.class.getDeclaredField(valuePropertyName);
			if(valueField == null){
				return null;
			}
			valueFieldList.add(valueField);
		}
		
		
		StringBuilder sbStr = new StringBuilder();
		sbStr.delete(0, sbStr.length());
		for(BillingOrder order : source){
			String keyValue = (String)keyField.get(order);
			
			for(int i = 0 ; i < valueFieldList.size(); i++){
				if(i!=0){
					sbStr.append("_");
				}
				Field tmpValField = (Field)valueFieldList.get(i);
				sbStr.append((String)tmpValField.get(order));	
			}
			hm.put(keyValue, sbStr.toString());
		}
		
		return hm;
	}
	
	public void setBillDao(MPayBillDao billDao){
		this.billDao = billDao;
	}
	
}
