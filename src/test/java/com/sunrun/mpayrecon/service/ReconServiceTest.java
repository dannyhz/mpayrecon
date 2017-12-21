package com.sunrun.mpayrecon.service;

import citic.hz.mpos.service.dao.po.BillingOrder;

public class ReconServiceTest {




	
	
	
	
	private BillingOrder generateCommonOrder(String chOrMyFlag){
		BillingOrder order = new BillingOrder();
		
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
		
		
		if(chOrMyFlag.equals("my")){
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
		}
		return order;
	}

}
