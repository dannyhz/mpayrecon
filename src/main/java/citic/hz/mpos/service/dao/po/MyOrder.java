package citic.hz.mpos.service.dao.po;

public class MyOrder {
	
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
	private String BRH_ID;//机构号
	private String MY_MCH_NO;//我方一级商户号,对应渠道二级商户号
	private String MY_SEC_MCH_NO;//我方二级商户号
	private String TERM_NO;//终端号
	private String CLDT;//清算日期
	private String BNO;//入库批次号（与BATID不同）
	private String TRADE_TYPE;//交易方式，如JSAPI、NATIVE、MICROPAY
	private String MCH_ORDER_ID;//商户订单号
	private String REL_MCH_ORDER_ID;//商户相关订单号（预留，暂无数据）
	private String FZFG;//分账标记 N-不分帐 Y-分账
	private String MY_MCH_RATE;//分账模式下为平台商户费率，普通模式下为普通商户费率
	private String MY_SEC_MCH_RATE;//分账模式下为分账子商户费率，普通模式下为空
	private String D0FG;//D0标记，0-成功，1-余额不足，2-其他失败，3-状态未知,X-非D0,4-受理超时，5-已受理，结果未知
	private String TRADE_CODE;//交易码
	private String PAYBANK;//付款银行
	private String BANK_CODE;//银行编号
	
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
	public String getFZFG() {
		return FZFG;
	}
	public void setFZFG(String fZFG) {
		FZFG = fZFG;
	}
	public String getMY_MCH_RATE() {
		return MY_MCH_RATE;
	}
	public void setMY_MCH_RATE(String mY_MCH_RATE) {
		MY_MCH_RATE = mY_MCH_RATE;
	}
	public String getMY_SEC_MCH_RATE() {
		return MY_SEC_MCH_RATE;
	}
	public void setMY_SEC_MCH_RATE(String mY_SEC_MCH_RATE) {
		MY_SEC_MCH_RATE = mY_SEC_MCH_RATE;
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
	public String getPAYBANK() {
		return PAYBANK;
	}
	public void setPAYBANK(String pAYBANK) {
		PAYBANK = pAYBANK;
	}
	public String getBANK_CODE() {
		return BANK_CODE;
	}
	public void setBANK_CODE(String bANK_CODE) {
		BANK_CODE = bANK_CODE;
	}
}
