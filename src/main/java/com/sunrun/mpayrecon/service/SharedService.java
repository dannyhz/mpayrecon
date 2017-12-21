package com.sunrun.mpayrecon.service;

public class SharedService {
	
	public MyOrderService getMyOrderService(){
		return new MyOrderService();
	}
	
	public ChannelOrderService getChannelOrderService(){
		return new ChannelOrderService();
	}
	
	public ReconService getReconService(){
		return new ReconService();
	}
	
	public MerchantFeeClearService getMerchantFeeClearService(){
		return new MerchantFeeClearService();
	}
}
