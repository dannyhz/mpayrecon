package com.sunrun.mpayrecon.processor;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import com.sunrun.mpayrecon.constants.ReconConstants;
import com.sunrun.mpayrecon.model.ChannelOrder;
import com.sunrun.mpayrecon.model.ReconResult;
import com.sunrun.mpayrecon.model.SessionContext;
import com.sunrun.mpayrecon.model.TxnOrder;
import com.sunrun.mpayrecon.processor.inf.Processor;
import com.sunrun.mpayrecon.service.ChannelOrderService;
import com.sunrun.mpayrecon.service.ReconService;
import com.sunrun.mpayrecon.service.SharedService;
import com.sunrun.mpayrecon.service.TxnOrderService;

@Service
public class ReconProcessor implements Processor{
	
	@Override
	public void execute(SessionContext sessionContext, SharedService sharedService) {
		
		String startTime = sessionContext.getValue("startTime");
		String endTime = sessionContext.getValue("endTime");
		String checkDate = sessionContext.getValue("checkDate");
		String channelNo = sessionContext.getValue("channelNo");
		
		//得到三个从数据库查询的数据
		TxnOrderService myOrderService = sharedService.getTxnOrderService();
		List<TxnOrder> txnSuccessOrders =  myOrderService.getMySuccessOrders(checkDate,startTime,endTime,channelNo);
		List<TxnOrder> txnFailOrders =  myOrderService.getMyFailOrders(checkDate,startTime,endTime,channelNo);
		
		ChannelOrderService channelOrderService = sharedService.getChannelOrderService();
		List<ChannelOrder> channelOrders =  channelOrderService.getChannelOrders(checkDate,startTime,endTime,channelNo);
		
		ReconService reconService = sharedService.getReconService();
		ReconResult reconResult = new ReconResult();
		reconResult.setOddTxnOrdersHistory((List)sessionContext.getObject(ReconConstants.ODD_TXN_ORDER_HISTORY));
		reconResult.setOddChannelOrdersHistory((List)sessionContext.getObject(ReconConstants.ODD_CHANNEL_ORDER_HISTORY));
		try {
			reconService.recon(txnSuccessOrders, channelOrders, txnFailOrders, reconResult);
		} catch (Exception e) {
			e.printStackTrace();
			sessionContext.setExecSucc(false);
			return;
		}
		
		//数据库插入比较完的成功数据和失败数据
		
		sessionContext.setExecSucc(true);
		sessionContext.putObject(ReconConstants.ODD_TXN_ORDER_HISTORY, reconResult.getOddTxnOrdersHistory());
		sessionContext.putObject(ReconConstants.ODD_CHANNEL_ORDER_HISTORY, reconResult.getOddChannelOrdersHistory());
		sessionContext.putObject(ReconConstants.RECON_RESULT, reconResult);
	}

}
