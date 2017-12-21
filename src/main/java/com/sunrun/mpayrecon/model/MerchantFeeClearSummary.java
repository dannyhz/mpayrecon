package com.sunrun.mpayrecon.model;

import java.util.Map;

public class MerchantFeeClearSummary {
	//汇总普通商户入账金额 RZTP=1
	public Map<String, MerchantFeeClearRecord> normalMerchantFeeClearRecord;
	
	//汇总分账子商户入账金额 RZTP=2
	public Map<String, MerchantFeeClearRecord> splitMerchantFeeClearRecord;

	//日间成功D0/分账子商户D0超时/异常入平台商户垫支账户 RZTP=3 （垫支户只能是本行账户,且只能是D1）
	public Map<String, MerchantFeeClearRecord> D0MerchantFeeClearRecord;
	
	//汇总平台商户利差金额 RZTP=4
	public Map<String, MerchantFeeClearRecord> platformMerchantFeeClearRecord;
	
	//汇总独立商户入账金额 RZTP=2(与分账一致,通过商户号规则可以区分)
	public Map<String, MerchantFeeClearRecord> independentMerchantFeeClearRecord;
	
	//独立模式日间成功D0/D0超时/异常入平台商户垫支账户 RZTP=3 （垫支户只能是本行账户,且只能是D1）
	public Map<String, MerchantFeeClearRecord> independentD0MerchantFeeClearRecord;
	
	//汇总独立模式平台商户利差金额 RZTP=4(与分账一致,通过商户号规则可以区分)
	public Map<String, MerchantFeeClearRecord> independentMerchantMarginFeeClearRecord;
	
	//汇总终端手续费（比商户汇总再多一级TERM_NO汇总）
	public Map<String, MerchantFeeClearRecord> terminalFeeMerchantFeeClearRecord;
	
	
}
