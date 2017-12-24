package citic.hz.mpos.service.dao.po;

public class ResultOrder {

	private Integer id;
	private String MY_ORDER_ID;//我方订单号
	//private String MCH_NO;//渠道方商户号
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getMY_ORDER_ID() {
		return MY_ORDER_ID;
	}
	public void setMY_ORDER_ID(String mY_ORDER_ID) {
		MY_ORDER_ID = mY_ORDER_ID;
	}
//	public String getMCH_NO() {
//		return MCH_NO;
//	}
//	public void setMCH_NO(String mCH_NO) {
//		MCH_NO = mCH_NO;
//	}
	
}
