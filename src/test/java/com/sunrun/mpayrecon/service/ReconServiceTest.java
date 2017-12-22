package com.sunrun.mpayrecon.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.sunrun.mpayrecon.model.ChannelOrder;
import com.sunrun.mpayrecon.model.TxnOrder;

import citic.hz.mpos.service.dao.po.BillingOrder;

public class ReconServiceTest {




	
	
	
	
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
