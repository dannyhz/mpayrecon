package com.sunrun.bill.model;

/**
 * 平账数据实体类
 * @author Administrator
 *
 */
public class BillFiledataDO implements Comparable {
	
	private String channel_time;
	
	private String trx_id;
	
	private String cust_cast_no;
	
	private String trx_amt;
	
	private String mcht_seq_no;//商户订单号 2017-4-26 add
	

	public String getChannel_time() {
		return channel_time;
	}


	public void setChannel_time(String channel_time) {
		this.channel_time = channel_time;
	}


	public String getTrx_id() {
		return trx_id;
	}


	public void setTrx_id(String trx_id) {
		this.trx_id = trx_id;
	}


	public String getCust_cast_no() {
		return cust_cast_no;
	}


	public void setCust_cast_no(String cust_cast_no) {
		this.cust_cast_no = cust_cast_no;
	}


	public String getTrx_amt() {
		return trx_amt;
	}


	public void setTrx_amt(String trx_amt) {
		this.trx_amt = trx_amt;
	}
	

	public String getMcht_seq_no() {
		return mcht_seq_no;
	}


	public void setMcht_seq_no(String mcht_seq_no) {
		this.mcht_seq_no = mcht_seq_no;
	}


	@Override
	public int compareTo(Object o) {
		BillFiledataDO bfdDO = (BillFiledataDO)o;
		String otherTime = bfdDO.getChannel_time();
		return this.channel_time.compareTo(otherTime);
//		return 0;
	}

}
