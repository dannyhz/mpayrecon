package citic.hz.mpos.service.dao.po;

public class ChannelOrder {
	
	private String TRTM;
	private String CHANNEL_NO;
	private String CHL_ORDER_ID;
	private String MY_ORDER_ID;
	private String MCH_NO;
	private String SEC_MCH_NO;
	private String TRAM;
	private String TRTP;
	private String REL_ORDER_ID;
	private String MEMO;
	private String CLDT;
	private String BNO;
	private String PAYBANK;
	
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
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ChannelOrder)){
			throw new RuntimeException("ChannelOrder 对象比较 ，传入的对象不是 ChannelOrder类型的");
		}
		ChannelOrder coNew = (ChannelOrder)obj;
		return this.MY_ORDER_ID.equals(coNew.MY_ORDER_ID) && this.TRTP.equals(coNew.TRTP) && this.TRAM.equals(coNew.TRAM);
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		
		hash = 31 * hash + MY_ORDER_ID.hashCode();
		hash = 31 * hash + TRTP.hashCode();
		hash = 31 * hash + TRAM.hashCode();
		
		return hash;
	}

}
