package com.sunrun.mpayrecon.processor;

import java.util.List;

import com.sunrun.mpayrecon.model.SessionContext;
import com.sunrun.mpayrecon.model.TxnOrder;
import com.sunrun.mpayrecon.processor.inf.Processor;
import com.sunrun.mpayrecon.service.MyOrderService;
import com.sunrun.mpayrecon.service.SharedService;

public class ReconProcessor implements Processor{

	@Override
	public void execute(SessionContext sessionContext, SharedService sharedService) {
		String startTime = sessionContext.getValue("startTime");
		String endTime = sessionContext.getValue("endTime");
		String checkDate = sessionContext.getValue("checkDate");
		String channelNo = sessionContext.getValue("channelNo");
		MyOrderService myOrderService = sharedService.getMyOrderService();
		List<TxnOrder> orders =  myOrderService.getMyOrders(checkDate,startTime,endTime,channelNo);
		//List<ChannelOrder> channelOrders =  getChannelOrders();
		
	}

}
