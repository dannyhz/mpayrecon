package citic.hz.mpos.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import citic.hz.mpos.service.dao.MPayBillDao;
import citic.hz.mpos.service.dao.po.BillingOrder;

public class BillingServiceTest {
	//各两笔都成功
	@Test
	public void billing_test() throws Exception{
		
		List<BillingOrder> myOrderList = new ArrayList<BillingOrder>();
		List<BillingOrder> channelOrderList = new ArrayList<BillingOrder>();
		
		for(int i=0; i<2; i++){
			BillingOrder myBo = generateCommonOrder("my");
			myBo.setMY_ORDER_ID("20000"+i);
			myOrderList.add(myBo);
		}
		for(int i=0; i<2; i++){
			BillingOrder chBo = generateCommonOrder("ch");
			chBo.setMY_ORDER_ID("20000"+i);
			channelOrderList.add(chBo);
		}
		
		BillingService service = new BillingService();
		
		
		MPayBillDao billDao = Mockito.mock(MPayBillDao.class);
		
		Mockito.when(billDao.getChannelOrders("30")).thenReturn(channelOrderList);
		Mockito.when(billDao.getMyOrders("30")).thenReturn(myOrderList);
		
		service.setBillDao(billDao);
		
		
		service.billing("1", "30");
		
		System.out.println("matchorder size : " + service.matchOrders.size());
		
	}
	
	//一笔成功 双方都有
	//另一笔不成功 类型不一样
	//预期结果  一笔对帐成功
	//另一笔 对账不成功
	@Test
	public void billing_one_match_one_not_match_test() throws Exception{
		
		List<BillingOrder> myOrderList = new ArrayList<BillingOrder>();
		List<BillingOrder> channelOrderList = new ArrayList<BillingOrder>();
		
		for(int i=0; i<1; i++){
			BillingOrder myBo = generateCommonOrder("my");
			myBo.setMY_ORDER_ID("20000"+i);
			myOrderList.add(myBo);
		}
		for(int i=0; i<1; i++){
			BillingOrder chBo = generateCommonOrder("ch");
			chBo.setMY_ORDER_ID("20000"+i);
			channelOrderList.add(chBo);
		}
		
		BillingOrder myBo2 = generateCommonOrder("my");
		myBo2.setMY_ORDER_ID("200001");
		myBo2.setTRTP("01");
		myOrderList.add(myBo2);
	
		BillingOrder chBo2 = generateCommonOrder("ch");
		chBo2.setMY_ORDER_ID("200001");
		chBo2.setTRTP("04");
		channelOrderList.add(chBo2);
		
		BillingService service = new BillingService();
		
		
		MPayBillDao billDao = Mockito.mock(MPayBillDao.class);
		
		Mockito.when(billDao.getChannelOrders("30")).thenReturn(channelOrderList);
		Mockito.when(billDao.getMyOrders("30")).thenReturn(myOrderList);
		
		service.setBillDao(billDao);
		
		
		service.billing("1", "30");
		
		System.out.println("matchorder size : " + service.matchOrders.size());
		System.out.println("not matchorder size : " + service.notMatchOrders.size());
	}
	
	//一笔成功 双方都有
		//另一笔不成功 渠道多 ， 一笔我方多
	//预期  多的都放到差错表
		@Test
		public void billing_channel_odd_my_odd_test() throws Exception{
			
			List<BillingOrder> myOrderList = new ArrayList<BillingOrder>();
			List<BillingOrder> channelOrderList = new ArrayList<BillingOrder>();
			
			for(int i=0; i<1; i++){
				BillingOrder myBo = generateCommonOrder("my");
				myBo.setMY_ORDER_ID("20000"+i);
				myOrderList.add(myBo);
			}
			for(int i=0; i<1; i++){
				BillingOrder chBo = generateCommonOrder("ch");
				chBo.setMY_ORDER_ID("20000"+i);
				channelOrderList.add(chBo);
			}
			
			BillingOrder myBo2 = generateCommonOrder("my");
			myBo2.setMY_ORDER_ID("200001");
			myBo2.setTRTP("01");
			myOrderList.add(myBo2);
		
			BillingOrder chBo2 = generateCommonOrder("ch");
			chBo2.setMY_ORDER_ID("200001");
			chBo2.setTRTP("04");
			channelOrderList.add(chBo2);
			
			
			BillingOrder myBo3 = generateCommonOrder("my");
			myBo3.setMY_ORDER_ID("200002");
			myOrderList.add(myBo3);
		
			BillingOrder chBo4 = generateCommonOrder("ch");
			chBo4.setMY_ORDER_ID("200003");
			channelOrderList.add(chBo4);
			
			BillingService service = new BillingService();
			
			
			MPayBillDao billDao = Mockito.mock(MPayBillDao.class);
			
			Mockito.when(billDao.getChannelOrders("30")).thenReturn(channelOrderList);
			Mockito.when(billDao.getMyOrders("30")).thenReturn(myOrderList);
			
			service.setBillDao(billDao);
			
			
			service.billing("1", "30");
			
			System.out.println("matchorder size : " + service.matchOrders.size());
			System.out.println("not matchorder size : " + service.notMatchOrders.size());
		}
	
	
		//一笔成功 双方都有 成功 20000
		// 双方的20001  类型不一样 
				//另一笔不成功 渠道多 ， 一笔我方多
			//预期  多的都放到差错表
		//比较撤销的记录 rel_order_id
		//预期 在match的有两笔
		//在差错的有三笔
				@Test
				public void billing_revoke_test() throws Exception{
					
					List<BillingOrder> myOrderList = new ArrayList<BillingOrder>();
					List<BillingOrder> channelOrderList = new ArrayList<BillingOrder>();
					
					for(int i=0; i<1; i++){
						BillingOrder myBo = generateCommonOrder("my");
						myBo.setMY_ORDER_ID("20000"+i);
						myOrderList.add(myBo);
					}
					for(int i=0; i<1; i++){
						BillingOrder chBo = generateCommonOrder("ch");
						chBo.setMY_ORDER_ID("20000"+i);
						channelOrderList.add(chBo);
					}
					
					BillingOrder myBo2 = generateCommonOrder("my");
					myBo2.setMY_ORDER_ID("200001");
					myBo2.setTRTP("01");
					myOrderList.add(myBo2);
				
					BillingOrder chBo2 = generateCommonOrder("ch");
					chBo2.setMY_ORDER_ID("200001");
					chBo2.setTRTP("04");
					channelOrderList.add(chBo2);
					
					
					BillingOrder myBo3 = generateCommonOrder("my");
					myBo3.setMY_ORDER_ID("200002");
					myOrderList.add(myBo3);
				
					BillingOrder chBo4 = generateCommonOrder("ch");
					chBo4.setMY_ORDER_ID("200003");
					channelOrderList.add(chBo4);
					
					BillingOrder myBoRevoke = generateCommonOrder("my");
					myBoRevoke.setTRTP("21");
					myBoRevoke.setREL_ORDER_ID("200005");
					myBoRevoke.setMY_ORDER_ID("YYYYYY");
					myOrderList.add(myBoRevoke);
					
					BillingOrder chBoRevoke = generateCommonOrder("ch");
					chBoRevoke.setTRTP("21");
					chBoRevoke.setREL_ORDER_ID("200005");
					chBoRevoke.setMY_ORDER_ID("XXXXXX");
					channelOrderList.add(chBoRevoke);
					
					BillingService service = new BillingService();
					
					
					MPayBillDao billDao = Mockito.mock(MPayBillDao.class);
					
					Mockito.when(billDao.getChannelOrders("30")).thenReturn(channelOrderList);
					Mockito.when(billDao.getMyOrders("30")).thenReturn(myOrderList);
					
					service.setBillDao(billDao);
					
					
					service.billing("1", "30");
					
					System.out.println("matchorder size : " + service.matchOrders.size());
					for(BillingOrder mo:service.matchOrders){
						System.out.println(mo.getMY_ORDER_ID());
					}
					
					System.out.println("not matchorder size : " + service.notMatchOrders.size());
					for(BillingOrder mo:service.notMatchOrders){
						System.out.println(mo.getMY_ORDER_ID());
					}
				}
			

				//一笔成功 双方都有 成功 20000
				// 双方的20001  类型不一样 
						//另一笔不成功 渠道多 ， 一笔我方多
					//预期  多的都放到差错表
				//比较撤销的记录 rel_order_id
				//添加我方 错误交易记录 ,跟 差错表里 有记录匹配
				//预期 在match的有三笔
				//在差错的有三笔
						@Test
						public void billing_fail_record_test() throws Exception{
							
							List<BillingOrder> myOrderList = new ArrayList<BillingOrder>();
							List<BillingOrder> channelOrderList = new ArrayList<BillingOrder>();
							List<BillingOrder> myFailOrderList = new ArrayList<BillingOrder>();
							
							for(int i=0; i<1; i++){
								BillingOrder myBo = generateCommonOrder("my");
								myBo.setMY_ORDER_ID("20000"+i);
								myOrderList.add(myBo);
							}
							for(int i=0; i<1; i++){
								BillingOrder chBo = generateCommonOrder("ch");
								chBo.setMY_ORDER_ID("20000"+i);
								channelOrderList.add(chBo);
							}
							
							BillingOrder myBo2 = generateCommonOrder("my");
							myBo2.setMY_ORDER_ID("200001");
							myBo2.setTRTP("01");
							myOrderList.add(myBo2);
						
							BillingOrder chBo2 = generateCommonOrder("ch");
							chBo2.setMY_ORDER_ID("200001");
							chBo2.setTRTP("04");
							channelOrderList.add(chBo2);
							
							
							BillingOrder myBo3 = generateCommonOrder("my");
							myBo3.setMY_ORDER_ID("200002");
							myBo3.setTRTP("01");
							myOrderList.add(myBo3);
						
							BillingOrder chBo4 = generateCommonOrder("ch");
							chBo4.setMY_ORDER_ID("200003");
							chBo4.setTRTP("01");
							chBo4.setTRAM("100");
							channelOrderList.add(chBo4);
							
							
							BillingOrder myBo5 = generateCommonOrder("my");
							myBo5.setMY_ORDER_ID("200003");
							myBo5.setTRTP("01");
							myBo5.setTRAM("100");
							myFailOrderList.add(myBo5);
							
							BillingOrder myBoRevoke = generateCommonOrder("my");
							myBoRevoke.setTRTP("21");
							myBoRevoke.setREL_ORDER_ID("200005");
							myBoRevoke.setMY_ORDER_ID("YYYYYY");
							myOrderList.add(myBoRevoke);
							
							BillingOrder chBoRevoke = generateCommonOrder("ch");
							chBoRevoke.setTRTP("21");
							chBoRevoke.setREL_ORDER_ID("200005");
							chBoRevoke.setMY_ORDER_ID("XXXXXX");
							channelOrderList.add(chBoRevoke);
							
							BillingService service = new BillingService();
							
							
							MPayBillDao billDao = Mockito.mock(MPayBillDao.class);
							
							Mockito.when(billDao.getChannelOrders("30")).thenReturn(channelOrderList);
							Mockito.when(billDao.getMyOrders("30")).thenReturn(myOrderList);
							Mockito.when(billDao.getMyFailOrders("30")).thenReturn(myFailOrderList);
							service.setBillDao(billDao);
							
							
							service.billing("1", "30");
							
							System.out.println("matchorder size : " + service.matchOrders.size());
							for(BillingOrder mo:service.matchOrders){
								System.out.println(mo.getMY_ORDER_ID());
							}
							
							System.out.println("not matchorder size : " + service.notMatchOrders.size());
							for(BillingOrder mo:service.notMatchOrders){
								System.out.println(mo.getMY_ORDER_ID());
							}
						}
							
						
						
						//一笔成功 双方都有 成功 20000
						// 双方的20001  类型不一样 
								//另一笔不成功 渠道多 ， 一笔我方多
							//预期  多的都放到差错表
						//比较撤销的记录 rel_order_id
						//添加我方 错误交易记录 ,跟 差错表里 有记录匹配
						//预期 在match的有三笔
						//在差错的有三笔
						//
						//又有一波新一轮的比较 ， 上轮的没对上的在 result fail里
			@Test
			public void billing_next_round_test() throws Exception{
				
				List<BillingOrder> myOrderList = new ArrayList<BillingOrder>();
				List<BillingOrder> channelOrderList = new ArrayList<BillingOrder>();
				List<BillingOrder> myFailOrderList = new ArrayList<BillingOrder>();
				
				for(int i=0; i<1; i++){
					BillingOrder myBo = generateCommonOrder("my");
					myBo.setMY_ORDER_ID("20000"+i);
					myOrderList.add(myBo);
				}
				for(int i=0; i<1; i++){
					BillingOrder chBo = generateCommonOrder("ch");
					chBo.setMY_ORDER_ID("20000"+i);
					channelOrderList.add(chBo);
				}
				
				BillingOrder myBo2 = generateCommonOrder("my");
				myBo2.setMY_ORDER_ID("200001");
				myBo2.setTRTP("01");
				myOrderList.add(myBo2);
			
				BillingOrder chBo2 = generateCommonOrder("ch");
				chBo2.setMY_ORDER_ID("200001");
				chBo2.setTRTP("04");
				channelOrderList.add(chBo2);
				
				
				BillingOrder myBo3 = generateCommonOrder("my");
				myBo3.setMY_ORDER_ID("200002");
				myBo3.setTRTP("01");
				myOrderList.add(myBo3);
			
				BillingOrder chBo4 = generateCommonOrder("ch");
				chBo4.setMY_ORDER_ID("200003");
				chBo4.setTRTP("01");
				chBo4.setTRAM("100");
				channelOrderList.add(chBo4);
				
				
				BillingOrder myBo5 = generateCommonOrder("my");
				myBo5.setMY_ORDER_ID("200003");
				myBo5.setTRTP("01");
				myBo5.setTRAM("100");
				myFailOrderList.add(myBo5);
				
				BillingOrder myBoRevoke = generateCommonOrder("my");
				myBoRevoke.setTRTP("21");
				myBoRevoke.setREL_ORDER_ID("200005");
				myBoRevoke.setMY_ORDER_ID("YYYYYY");
				myOrderList.add(myBoRevoke);
				
				BillingOrder chBoRevoke = generateCommonOrder("ch");
				chBoRevoke.setTRTP("21");
				chBoRevoke.setREL_ORDER_ID("200005");
				chBoRevoke.setMY_ORDER_ID("XXXXXX");
				channelOrderList.add(chBoRevoke);
				
				BillingService service = new BillingService();
				
				
				MPayBillDao billDao = Mockito.mock(MPayBillDao.class);
				
				Mockito.when(billDao.getChannelOrders("30")).thenReturn(channelOrderList);
				Mockito.when(billDao.getMyOrders("30")).thenReturn(myOrderList);
				Mockito.when(billDao.getMyFailOrders("30")).thenReturn(myFailOrderList);
				service.setBillDao(billDao);
				
				
				service.billing("1", "30");
				
				System.out.println("matchorder size : " + service.matchOrders.size());
				for(BillingOrder mo:service.matchOrders){
					System.out.println(mo.getMY_ORDER_ID());
				}
				
				System.out.println("not matchorder size : " + service.notMatchOrders.size());
				for(BillingOrder mo:service.notMatchOrders){
					System.out.println(mo.getMY_ORDER_ID());
				}
			}
				
	/**
	 *
	 * @param chOrMyFlag  
	 * 
	 * my 我方 
	 * ch 渠道
	 * 
	 * 
	 * 
	 * TRTM	交易时间
		CHANNEL_NO	渠道
		CHL_ORDER_ID	渠道方订单号
		MY_ORDER_ID	我方订单号
		MCH_NO	渠道方商户号
		SEC_MCH_NO	渠道方二级商户号
		TRAM	金额
		TRTP	交易类型
		REL_ORDER_ID	相关流水号，用于处理退款、撤销等业务
		MEMO	交易备注
		BRH_ID	机构号
		MY_MCH_NO	我方一级商户号,对应渠道二级商户号
		MY_SEC_MCH_NO	我方二级商户号
		TERM_NO	终端号
		CLDT	清算日期
		BNO	入库批次号（与BATID不同）
		TRADE_TYPE	交易方式，如JSAPI、NATIVE、MICROPAY
		MCH_ORDER_ID	商户订单号
		REL_MCH_ORDER_ID	商户相关订单号（预留，暂无数据）
		FZFG	分账标记 N-不分帐 Y-分账
		MY_MCH_RATE	分账模式下为平台商户费率，普通模式下为普通商户费率
		MY_SEC_MCH_RATE	分账模式下为分账子商户费率，普通模式下为空
		D0FG	D0标记，0-成功，1-余额不足，2-其他失败，3-状态未知,X-非D0,4-受理超时，5-已受理，结果未知
		TRADE_CODE	交易码
		PAYBANK	付款银行
		BANK_CODE	银行编号

	 * 
	 * @return
	 */
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
