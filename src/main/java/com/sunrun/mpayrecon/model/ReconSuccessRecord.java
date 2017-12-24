package com.sunrun.mpayrecon.model;

public class ReconSuccessRecord {
	
	private Long ID;
	
	public Long getID() {
		return ID;
	}
	public void setID(Long iD) {
		ID = iD;
	}
	private String	SYSTM;//入库时间戳
	private String	BATID;//对账批次号
	private String	CLDT;//清算日期,来源于订单支付完成的渠道日期
	private String	SRC;//来源 CHL-MPOS-BOTH
	private String	TRTM;//交易时间
	private String	CHANNEL_NO;//渠道
	private String	CHL_ORDER_ID;//渠道方订单号
	private String	MY_ORDER_ID;//我方订单号
	private String	MCH_NO;//渠道商户号
	private String	SEC_MCH_NO;//渠道二级商户号
	private String	TRAM;//金额
	private String	TRTP;//交易类型
	private String	REL_ORDER_ID;//相关流水号，用于处理退款、撤销等业务
	private String	MEMO;//交易备注
	private String	BRH_ID;//机构号
	private String	MY_MCH_NO;//我方一级商户号,对应渠道二级商户号
	private String	MY_SEC_MCH_NO;//我方二级商户号
	private String	TERM_NO;//终端号
	private String	COST_RATE;//渠道成本费率
	private String	TOTAL_RATE;//向商户收取费率
	private String	COST;//渠道成本
	private String	TOTAL_FEE;//商户手续费
	private String	RZAMT;//商户入账金额
	private String	CKTURN;//对账轮次
	private String	CKFG;//对账标记
	private String	CKDT;//对账日期(分区键)
	private String	TRADE_TYPE;//交易方式，如JSAPI、NATIVE、MICROPAY
	private String	MCH_ORDER_ID;//商户订单号
	private String	REL_MCH_ORDER_ID;//商户相关订单号（预留，暂无数据）
	private String	PAYBANK;//付款银行
	private String	FZFG;//分账标记 N-不分帐 Y-分账 D-独立
	private String	MY_PTMCH_RATE;//平台商户利差费率
	private String	MY_PTMCH_FEE;//平台商户利差金额
	private String	D0FG;//D0标记，0-成功，1-余额不足，2-其他失败，3-状态未知,X-非D0
	private String	TRADE_CODE;//交易码
	private String	BANK_CODE;//银行编号
	
	private int num;
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}

	public String getSYSTM() {
		return SYSTM;
	}
	public void setSYSTM(String sYSTM) {
		SYSTM = sYSTM;
	}
	public String getBATID() {
		return BATID;
	}
	public void setBATID(String bATID) {
		BATID = bATID;
	}
	public String getCLDT() {
		return CLDT;
	}
	public void setCLDT(String cLDT) {
		CLDT = cLDT;
	}
	public String getSRC() {
		return SRC;
	}
	public void setSRC(String sRC) {
		SRC = sRC;
	}
	public String getTRTM() {
		return TRTM;
	}
	public void setTRTM(String tRTM) {
		TRTM = tRTM;
	}
	public String getCHANNEL_NO() {
		return CHANNEL_NO;
	}
	public void setCHANNEL_NO(String cHANNEL_NO) {
		CHANNEL_NO = cHANNEL_NO;
	}
	public String getCHL_ORDER_ID() {
		return CHL_ORDER_ID;
	}
	public void setCHL_ORDER_ID(String cHL_ORDER_ID) {
		CHL_ORDER_ID = cHL_ORDER_ID;
	}
	public String getMY_ORDER_ID() {
		return MY_ORDER_ID;
	}
	public void setMY_ORDER_ID(String mY_ORDER_ID) {
		MY_ORDER_ID = mY_ORDER_ID;
	}
	public String getMCH_NO() {
		return MCH_NO;
	}
	public void setMCH_NO(String mCH_NO) {
		MCH_NO = mCH_NO;
	}
	public String getSEC_MCH_NO() {
		return SEC_MCH_NO;
	}
	public void setSEC_MCH_NO(String sEC_MCH_NO) {
		SEC_MCH_NO = sEC_MCH_NO;
	}
	public String getTRAM() {
		return TRAM;
	}
	public void setTRAM(String tRAM) {
		TRAM = tRAM;
	}
	public String getTRTP() {
		return TRTP;
	}
	public void setTRTP(String tRTP) {
		TRTP = tRTP;
	}
	public String getREL_ORDER_ID() {
		return REL_ORDER_ID;
	}
	public void setREL_ORDER_ID(String rEL_ORDER_ID) {
		REL_ORDER_ID = rEL_ORDER_ID;
	}
	public String getMEMO() {
		return MEMO;
	}
	public void setMEMO(String mEMO) {
		MEMO = mEMO;
	}
	public String getBRH_ID() {
		return BRH_ID;
	}
	public void setBRH_ID(String bRH_ID) {
		BRH_ID = bRH_ID;
	}
	public String getMY_MCH_NO() {
		return MY_MCH_NO;
	}
	public void setMY_MCH_NO(String mY_MCH_NO) {
		MY_MCH_NO = mY_MCH_NO;
	}
	public String getMY_SEC_MCH_NO() {
		return MY_SEC_MCH_NO;
	}
	public void setMY_SEC_MCH_NO(String mY_SEC_MCH_NO) {
		MY_SEC_MCH_NO = mY_SEC_MCH_NO;
	}
	public String getTERM_NO() {
		return TERM_NO;
	}
	public void setTERM_NO(String tERM_NO) {
		TERM_NO = tERM_NO;
	}
	public String getCOST_RATE() {
		return COST_RATE;
	}
	public void setCOST_RATE(String cOST_RATE) {
		COST_RATE = cOST_RATE;
	}
	public String getTOTAL_RATE() {
		return TOTAL_RATE;
	}
	public void setTOTAL_RATE(String tOTAL_RATE) {
		TOTAL_RATE = tOTAL_RATE;
	}
	public String getCOST() {
		return COST;
	}
	public void setCOST(String cOST) {
		COST = cOST;
	}
	public String getTOTAL_FEE() {
		return TOTAL_FEE;
	}
	public void setTOTAL_FEE(String tOTAL_FEE) {
		TOTAL_FEE = tOTAL_FEE;
	}
	public String getRZAMT() {
		return RZAMT;
	}
	public void setRZAMT(String rZAMT) {
		RZAMT = rZAMT;
	}
	public String getCKTURN() {
		return CKTURN;
	}
	public void setCKTURN(String cKTURN) {
		CKTURN = cKTURN;
	}
	public String getCKFG() {
		return CKFG;
	}
	public void setCKFG(String cKFG) {
		CKFG = cKFG;
	}
	public String getCKDT() {
		return CKDT;
	}
	public void setCKDT(String cKDT) {
		CKDT = cKDT;
	}
	public String getTRADE_TYPE() {
		return TRADE_TYPE;
	}
	public void setTRADE_TYPE(String tRADE_TYPE) {
		TRADE_TYPE = tRADE_TYPE;
	}
	public String getMCH_ORDER_ID() {
		return MCH_ORDER_ID;
	}
	public void setMCH_ORDER_ID(String mCH_ORDER_ID) {
		MCH_ORDER_ID = mCH_ORDER_ID;
	}
	public String getREL_MCH_ORDER_ID() {
		return REL_MCH_ORDER_ID;
	}
	public void setREL_MCH_ORDER_ID(String rEL_MCH_ORDER_ID) {
		REL_MCH_ORDER_ID = rEL_MCH_ORDER_ID;
	}
	public String getPAYBANK() {
		return PAYBANK;
	}
	public void setPAYBANK(String pAYBANK) {
		PAYBANK = pAYBANK;
	}
	public String getFZFG() {
		return FZFG;
	}
	public void setFZFG(String fZFG) {
		FZFG = fZFG;
	}
	public String getMY_PTMCH_RATE() {
		return MY_PTMCH_RATE;
	}
	public void setMY_PTMCH_RATE(String mY_PTMCH_RATE) {
		MY_PTMCH_RATE = mY_PTMCH_RATE;
	}
	public String getMY_PTMCH_FEE() {
		return MY_PTMCH_FEE;
	}
	public void setMY_PTMCH_FEE(String mY_PTMCH_FEE) {
		MY_PTMCH_FEE = mY_PTMCH_FEE;
	}
	public String getD0FG() {
		return D0FG;
	}
	public void setD0FG(String d0fg) {
		D0FG = d0fg;
	}
	public String getTRADE_CODE() {
		return TRADE_CODE;
	}
	public void setTRADE_CODE(String tRADE_CODE) {
		TRADE_CODE = tRADE_CODE;
	}
	public String getBANK_CODE() {
		return BANK_CODE;
	}
	public void setBANK_CODE(String bANK_CODE) {
		BANK_CODE = bANK_CODE;
	}
	
	

}
