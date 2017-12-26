package com.sunrun.mpayrecon.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

@Service
public class SharedService {
	
	@Resource
	private TxnOrderService myOrderService;
	@Resource
	private ChannelOrderService channelOrderService;
	@Resource
	private ReconService reconService;
	
	public TxnOrderService getTxnOrderService(){
		return myOrderService;
	}
	
	public ChannelOrderService getChannelOrderService(){
		return channelOrderService;
	}
	
	public ReconService getReconService(){
		return reconService;
	}
	
	public MerchantFeeClearService getMerchantFeeClearService(){
		return new MerchantFeeClearService();
	}
}
