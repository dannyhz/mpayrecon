package com.sunrun.mpayrecon.processor;

import java.util.List;

import com.sunrun.mpayrecon.model.ChannelOrder;
import com.sunrun.mpayrecon.model.ReconResult;
import com.sunrun.mpayrecon.model.SessionContext;
import com.sunrun.mpayrecon.model.TxnOrder;
import com.sunrun.mpayrecon.processor.inf.Processor;
import com.sunrun.mpayrecon.service.ChannelOrderService;
import com.sunrun.mpayrecon.service.MyOrderService;
import com.sunrun.mpayrecon.service.ReconService;
import com.sunrun.mpayrecon.service.SharedService;

public class ReconProcessor implements Processor{

	@Override
	public void execute(SessionContext sessionContext, SharedService sharedService) {
		String startTime = sessionContext.getValue("startTime");
		String endTime = sessionContext.getValue("endTime");
		String checkDate = sessionContext.getValue("checkDate");
		String channelNo = sessionContext.getValue("channelNo");
		MyOrderService myOrderService = sharedService.getMyOrderService();
		
		List<TxnOrder> txnOrders =  myOrderService.getMyOrders(checkDate,startTime,endTime,channelNo);
		
		ChannelOrderService channelOrderService = sharedService.getChannelOrderService();
		
		List<ChannelOrder> channelOrders =  channelOrderService.getChannelOrders(checkDate,startTime,endTime,channelNo);
		
		ReconService reconService = sharedService.getReconService();
		ReconResult reconResult = new ReconResult();
		try {
			reconService.recon(txnOrders, channelOrders, reconResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sessionContext.setExecSucc(true);
		sessionContext.setContextData(reconResult);
	}

}
