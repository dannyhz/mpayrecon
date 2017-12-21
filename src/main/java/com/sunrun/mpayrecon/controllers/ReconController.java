package com.sunrun.mpayrecon.controllers;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sunrun.bill.compare.ChannelBill;
import com.sunrun.bill.service.IMerchantService;
import com.sunrun.mpayrecon.model.MerchantFeeClearRecord;
import com.sunrun.mpayrecon.model.MerchantFeeClearSummary;
import com.sunrun.mpayrecon.model.SessionContext;
import com.sunrun.mpayrecon.processor.AgentProfitProcessor;
import com.sunrun.mpayrecon.processor.BankMerchantStatisticsProcessor;
import com.sunrun.mpayrecon.processor.FeeClearProcessor;
import com.sunrun.mpayrecon.processor.ReconProcessor;
import com.sunrun.mpayrecon.processor.inf.Processor;
import com.sunrun.mpayrecon.service.SharedService;
import com.sunrun.mpayrecon.service.SplitStrategyMachine;

/**
 * 对账控制类.
 *
 * @author zhuxiang
 * @since V1.0.0
 */
@Controller
public class ReconController {
    private static final Logger logger = LoggerFactory.getLogger(ReconController.class);

    @Autowired
    private List<ChannelBill> channelBillList;

    @Autowired
    private IMerchantService merchantService;

    private List<Processor> sequenceProcessors;
    
    private SharedService sharedService;
    
    
    @ResponseBody
    @RequestMapping("/recon")
    public void execute(HttpServletRequest req) {
    	
    	sequenceProcessors.add(new ReconProcessor());
    	sequenceProcessors.add(new FeeClearProcessor());
    	sequenceProcessors.add(new AgentProfitProcessor());
    	sequenceProcessors.add(new BankMerchantStatisticsProcessor());
    	
    	
    	SessionContext sessionContext = new SessionContext();
    	
//    	String startTime = sessionContext.getValue("startTime");
//		String endTime = sessionContext.getValue("endTime");
//		String checkDate = sessionContext.getValue("checkDate");
//		String channelNo = sessionContext.getValue("channelNo");
    	
    	sessionContext.putValue("channelNo", "30");
    	sessionContext.putValue("checkDate", "20171221");
    	SplitStrategyMachine strategyMachine = new SplitStrategyMachine();
    	List<Map<String, String>> paramList = strategyMachine.generateIteratorList();
    	
    	for(Map<String,String> param : paramList){
    		
    		sessionContext.mergeMap(param);
    		
	    	for(Processor processor : sequenceProcessors){
	    		
	    		processor.execute(sessionContext, sharedService);
	    		
	    		if(!sessionContext.isExecSucc()){
	    			break;
	    		}
	    		
	    	}
	    	
    	}
    	
    	MerchantFeeClearSummary feeClearSummary = (MerchantFeeClearSummary)sessionContext.getObject("feeClearSummary");
    	Map<String, MerchantFeeClearRecord> normalMerchantFeeClearRecordMap = feeClearSummary.normalMerchantFeeClearRecord;
    	
    	
    	

    }

   
}
