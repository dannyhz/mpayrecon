package com.sunrun.mpayrecon.model;

public class ChannelOrder {

	private String TRTM;//交易时间
	private String CHANNEL_NO;//渠道
	private String CHL_ORDER_ID;//渠道方订单号
	private String MY_ORDER_ID;//我方订单号
	private String MCH_NO;//渠道方商户号
	private String SEC_MCH_NO;//渠道方二级商户号
	private String TRAM;//金额
	private String TRTP;//交易类型
	private String REL_ORDER_ID;//相关流水号，用于处理退款、撤销等业务
	private String MEMO;//交易备注
	private String CLDT;//清算日期
	private String BNO;//入库批次号（与BATID不同）
	private String PAYBANK;//付款银行
	
	private String CKFG;//比较标识  0 匹配  ， 我方多 2， 渠道方多1，  金额不等 -3 ， 交易类型不等 -4
	
	public String getCKFG() {
		return CKFG;
	}
	public void setCKFG(String cKFG) {
		CKFG = cKFG;
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
	public String getCLDT() {
		return CLDT;
	}
	public void setCLDT(String cLDT) {
		CLDT = cLDT;
	}
	public String getBNO() {
		return BNO;
	}
	public void setBNO(String bNO) {
		BNO = bNO;
	}
	public String getPAYBANK() {
		return PAYBANK;
	}
	public void setPAYBANK(String pAYBANK) {
		PAYBANK = pAYBANK;
	}
	
}
