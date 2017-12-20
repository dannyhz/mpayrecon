package com.sunrun.bill.model;

public class BillDetailDO {

    private Long id;// 主键
    private String control_id;// 外键
    private String bat_id;// 批次号(区分渠道，但是不区分商户号)
    private String database_trx_id;// 数据库序列号
    private String file_trx_id;// 文件序列号
    private String db_amount;// 数据库金额
    private String file_amount;// 文件金额
    private String cust_card_no;// 银行卡号
    private String channel_code;// 渠道
    private String wrong_reason_flag;// 错账原因标识符 D:银行多记录(渠道查无记录); F:渠道多记录(银行查无记录)
                                     // ; W:两边金额不一致 S:金额一致，对账成功
    private String bill_date;// 交易日期
    private String create_time;// 创建时间
    private String file_name;// 文件名称
    private String mcht_no;// 商户号
    private String channel_time;// 交易时间

    private String dbOrderStatus;// 代扣平台记录的交易状态
    private String channelOrderStatus;// 渠道对账单中的交易状态

    public BillDetailDO() {
    }

    public BillDetailDO(String channel_code, String bill_date, String file_name) {
        this.channel_code = channel_code;
        this.bill_date = bill_date;
        this.file_name = file_name;
    }

    public String getDbOrderStatus() {
        return dbOrderStatus;
    }

    public void setDbOrderStatus(String dbOrderStatus) {
        this.dbOrderStatus = dbOrderStatus;
    }

    public String getChannelOrderStatus() {
        return channelOrderStatus;
    }

    public void setChannelOrderStatus(String channelOrderStatus) {
        this.channelOrderStatus = channelOrderStatus;
    }

    public String getBat_id() {
        return bat_id;
    }

    public void setBat_id(String bat_id) {
        this.bat_id = bat_id;
    }

    public String getMcht_no() {
        return mcht_no;
    }

    public void setMcht_no(String mcht_no) {
        this.mcht_no = mcht_no;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getControl_id() {
        return control_id;
    }

    public void setControl_id(String control_id) {
        this.control_id = control_id;
    }

    public String getWrong_reason_flag() {
        return wrong_reason_flag;
    }

    public void setWrong_reason_flag(String wrong_reason_flag) {
        this.wrong_reason_flag = wrong_reason_flag;
    }

    public String getDatabase_trx_id() {
        return database_trx_id;
    }

    public void setDatabase_trx_id(String database_trx_id) {
        this.database_trx_id = database_trx_id;
    }

    public String getFile_trx_id() {
        return file_trx_id;
    }

    public void setFile_trx_id(String file_trx_id) {
        this.file_trx_id = file_trx_id;
    }

    public String getDb_amount() {
        return db_amount;
    }

    public void setDb_amount(String db_amount) {
        this.db_amount = db_amount;
    }

    public String getFile_amount() {
        return file_amount;
    }

    public void setFile_amount(String file_amount) {
        this.file_amount = file_amount;
    }

    public String getCust_card_no() {
        return cust_card_no;
    }

    public void setCust_card_no(String cust_card_no) {
        this.cust_card_no = cust_card_no;
    }

    public String getChannel_code() {
        return channel_code;
    }

    public void setChannel_code(String channel_code) {
        this.channel_code = channel_code;
    }

    public String getBill_date() {
        return bill_date;
    }

    public void setBill_date(String bill_date) {
        this.bill_date = bill_date;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getChannel_time() {
        return channel_time;
    }

    public void setChannel_time(String channel_time) {
        this.channel_time = channel_time;
    }

}