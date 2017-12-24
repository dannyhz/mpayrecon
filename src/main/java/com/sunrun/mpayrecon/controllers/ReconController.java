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
import com.sunrun.mpayrecon.constants.ReconConstants;
import com.sunrun.mpayrecon.model.MerchantFeeClearRecord;
import com.sunrun.mpayrecon.model.MerchantFeeClearSummary;
import com.sunrun.mpayrecon.model.ReconFailRecord;
import com.sunrun.mpayrecon.model.ReconResult;
import com.sunrun.mpayrecon.model.ReconSuccessRecord;
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
    
    private ReconProcessor reconProcessor = new ReconProcessor();
    private FeeClearProcessor feeClearProcessor = new FeeClearProcessor();
    private AgentProfitProcessor agentProfitProcessor = new AgentProfitProcessor();
    private BankMerchantStatisticsProcessor bankMerchantStatisticsProcessor = new BankMerchantStatisticsProcessor();
    
    @ResponseBody
    @RequestMapping("/recon")
    public void execute(HttpServletRequest req) {
    	
//    	sequenceProcessors.add(new ReconProcessor());
//    	sequenceProcessors.add(new FeeClearProcessor());
//    	sequenceProcessors.add(new AgentProfitProcessor());
//    	sequenceProcessors.add(new BankMerchantStatisticsProcessor());
    	
    	
    	SessionContext sessionContext = new SessionContext();
    	
//    	String startTime = sessionContext.getValue("startTime");
//		String endTime = sessionContext.getValue("endTime");
//		String checkDate = sessionContext.getValue("checkDate");
//		String channelNo = sessionContext.getValue("channelNo");
    	
    	sessionContext.putValue("channelNo", "30");
    	sessionContext.putValue("checkDate", "20171221");
    	SplitStrategyMachine strategyMachine = new SplitStrategyMachine();
    	List<Map<String, String>> paramList = strategyMachine.generateIteratorList();
    	
    	//参数是24个小时， 再加从前天开始，由23点24点 开始，往前推30天。
    	for(Map<String,String> param : paramList){
    		//包含了4个参数 channelNo checkDate startTime endTime 
    		sessionContext.mergeMap(param);
    		
    		//先从一个对账的 处理类根据 sessionContext里面的参数，进行查询，来得到对账结果。 对账结果 CMP_Result 放到 sessionContext中
    			reconProcessor.execute(sessionContext, sharedService);
    			
    			//执行完 ,如果  sessionContext 的 这个方法 返回时false， 说明执行出了问题 ， 就跳出循环，结束整个程序
    			if(!sessionContext.isExecSucc()){
	    			break;
	    		}
    			ReconResult reconResult = (ReconResult)sessionContext.getObject(ReconConstants.RECON_RESULT);
    			//需要把本地两边都多余的记录留给下一次时间片进来再参与比较。
    			
    			//保存匹配上的记录 
    			saveMatchRecord(reconResult.getSuccessRecords());
    			//保存没匹配上的记录
    			saveFailMatchRecord(reconResult.getFailRecords());
    			
    			//缓存里需要存放，下面三个方法的累积数据， 知道最后时间片执行完， 再把这三个方法最新累积的数据，写入数据库
    			//后面的三个Processor都用这个CMP_RESULT来计算， 也把累计的结果放到sessionContext中
    			feeClearProcessor.execute(sessionContext, sharedService);
    			agentProfitProcessor.execute(sessionContext, sharedService);
    			bankMerchantStatisticsProcessor.execute(sessionContext, sharedService);
    			
    	}
    	//以第二个Processor为例， 他会计算normalMerchantFeeClearRecord, 把所有的值都累计到这个map，还有其他的map
    	MerchantFeeClearSummary feeClearSummary = (MerchantFeeClearSummary)sessionContext.getObject(ReconConstants.MERCHANT_FEE_CLEAR_SUMMARY);
    	//这只是他其中计算的一项，在这里以及完成了所有时间片的计算，这是最后的结果， 需要保存到数据库
    	Map<String, MerchantFeeClearRecord> normalMerchantFeeClearRecordMap = feeClearSummary.normalMerchantFeeClearRecord;
    	saveNormalMerchantFeeClearRecord(normalMerchantFeeClearRecordMap);
    	//后面还有一堆保存的动作
    }

	private void saveNormalMerchantFeeClearRecord(Map<String, MerchantFeeClearRecord> normalMerchantFeeClearRecordMap) {
		// TODO Auto-generated method stub
		
	}

	private void saveFailMatchRecord(List<ReconFailRecord> failRecords) {
		// TODO Auto-generated method stub
		
	}

	private void saveMatchRecord(List<ReconSuccessRecord> successRecords) {
		// TODO Auto-generated method stub
		
	}

   
}
