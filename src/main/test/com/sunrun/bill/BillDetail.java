package com.sunrun.bill;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sunrun.bill.model.BillControlDO;
import com.sunrun.bill.model.BillDetailDO;
import com.sunrun.bill.service.IBillService;
import com.sunrun.mpos.common.utils.DateUtils;
import com.sunrun.mpos.core.SpringContextHolder;

public class BillDetail {
	/**
	public static void main(String[] args) {
		GenericXmlApplicationContext context = new GenericXmlApplicationContext();
		context.setValidating(false);
		context.load("classpath:config/bill-context.xml");
		context.refresh();
		genBillDetail();
	}**/
	
	public static void main(String[] args) {
		String dateStr="20170613";
		try {
			String flagDateStr=DateUtils.formatDate(DateUtils.addDay(DateUtils.parse(dateStr,"yyyyMMdd"),1),"yyyyMMdd");
			System.out.println(flagDateStr);
		} catch (ParseException e) {
			 
		}
	}
	
//	@Test
	public static void genBillDetail(){
	IBillService iBillService = SpringContextHolder.getBean(IBillService.class);
		BillDetailDO tmp_detailDO =new BillDetailDO("bestpay",
				"20170121","fileName123");
		tmp_detailDO.setCust_card_no("6226900815256984");							
		tmp_detailDO.setFile_trx_id("1112223334445");
		tmp_detailDO.setDatabase_trx_id("11112223334456");
		tmp_detailDO.setDb_amount("500099");
		tmp_detailDO.setMcht_no("60000001");
		tmp_detailDO.setFile_amount("1231313");
		tmp_detailDO.setChannel_time("2017-01-12 14:11:22");
		
		BillDetailDO tmp_detailDO2 =new BillDetailDO("bestpay",
				"20170121","fileName12345");
		tmp_detailDO2.setCust_card_no("63226900815256984");							
		tmp_detailDO2.setFile_trx_id("5544332211");
		tmp_detailDO2.setDatabase_trx_id("66554433322");
		tmp_detailDO2.setDb_amount("1500099");
		tmp_detailDO2.setMcht_no("60000002");
		tmp_detailDO2.setFile_amount("321321");
		tmp_detailDO2.setChannel_time("2017-01-12 16:13:22");
		
		
		List<BillDetailDO> billDetailWsList = new ArrayList<BillDetailDO>();//鍥炲啓涓氬姟鏄庣粏dataList(缃戝晢)
		
		billDetailWsList.add(tmp_detailDO);
		billDetailWsList.add(tmp_detailDO2);
		
		BillControlDO billControlWsDO = new BillControlDO();
		billControlWsDO.setChannel_code("12312456789");
		billControlWsDO.setBill_date("20170121");
		billControlWsDO.setFile_name("fileName123");
		billControlWsDO.setTotal_bills_amount(String.valueOf("12213123"));//浜ゆ槗鎬婚噾棰�
 		billControlWsDO.setBalance_amount(String.valueOf("121212121"));
		billControlWsDO.setBalance_bills_account(String.valueOf("0"));
		billControlWsDO.setWrong_bills_account(String.valueOf("0"));
		billControlWsDO.setDb_bills_account(String.valueOf("99"));
		billControlWsDO.setFile_bills_account(String.valueOf("0"));
		billControlWsDO.setTotal_bills_account(String.valueOf("0"));
		billControlWsDO.setMcht_no("0");
		String bat_id = new Date().getTime()+"";
		billControlWsDO.setBat_id(bat_id);
		try {
			iBillService.insertControl(billControlWsDO);
			if(billDetailWsList!=null && billDetailWsList.size()>0){
				iBillService.insertDetailList(billDetailWsList,Integer.valueOf(billControlWsDO.getId()),billControlWsDO.getBat_id());
			}
			System.out.println("succees!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			
		} catch (Exception e) { 
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//鍥炲啓
	}
	
	
}
