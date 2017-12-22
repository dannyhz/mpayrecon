package com.sunrun.mpayrecon.processor;

import com.sunrun.mpayrecon.model.MerchantFeeClearSummary;
import com.sunrun.mpayrecon.model.ReconResult;
import com.sunrun.mpayrecon.model.SessionContext;
import com.sunrun.mpayrecon.processor.inf.Processor;
import com.sunrun.mpayrecon.service.MerchantFeeClearService;
import com.sunrun.mpayrecon.service.SharedService;

public class FeeClearProcessor  implements Processor{

	@Override
	public void execute(SessionContext sessionContext, SharedService sharedService) {
		
		ReconResult reconResult = (ReconResult)sessionContext.getContextData();
		
		MerchantFeeClearService merchantFeeClearService = sharedService.getMerchantFeeClearService();
		
		MerchantFeeClearSummary merchantFeeClearSummary = (MerchantFeeClearSummary)sessionContext.getObject("merchantFeeClearSummary");
		//把之前的 merchantFeeClearSummary 与现在的进行合并
		merchantFeeClearSummary = merchantFeeClearService.clearMerchantFee(reconResult, merchantFeeClearSummary);
		//合并后再放入缓存
		sessionContext.putObject("merchantFeeClearSummary", merchantFeeClearSummary);
		
		
	}

}
