package com.sunrun.bill.model;

//table_name= orders
public class BillOrderDO {
    private String trx_id;// 流水号
    private String channel_code;// 商户号
    private String cust_card_no;// 客户卡号
    private String amount;// 金额
    private String currency;// 货币单位
    private String channel_no;// 渠道号
    private String channel_seq_no;// 渠道流水号
    private String status;// 状态码
    private String message;// 信息
    private String create_time;// 创建时间
    private String update_time;// 修改时间
    private String reconciliationStatus;

    public String getReconciliationStatus() {
        return reconciliationStatus;
    }

    public void setReconciliationStatus(String reconciliationStatus) {
        this.reconciliationStatus = reconciliationStatus;
    }

    public String getTrx_id() {
        return trx_id;
    }

    public void setTrx_id(String trx_id) {
        this.trx_id = trx_id;
    }

    public String getChannel_code() {
        return channel_code;
    }

    public void setChannel_code(String channel_code) {
        this.channel_code = channel_code;
    }

    public String getCust_card_no() {
        return cust_card_no;
    }

    public void setCust_card_no(String cust_card_no) {
        this.cust_card_no = cust_card_no;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getChannel_no() {
        return channel_no;
    }

    public void setChannel_no(String channel_no) {
        this.channel_no = channel_no;
    }

    public String getChannel_seq_no() {
        return channel_seq_no;
    }

    public void setChannel_seq_no(String channel_seq_no) {
        this.channel_seq_no = channel_seq_no;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

}
