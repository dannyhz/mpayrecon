package com.sunrun.bill.model;

public class BillControlDO {

    private Long id;// pk
    private Long batId;// 批次号(区分渠道，但是不区分商户号)
    private String fileName;// 对账文件名称
    private String channelCode;// 渠道
    private String billDate;// yyyyMMdd 交易日期
    private Long balanceAmount;// 平账金额(结算金额)
    private Long balanceBillsAccount;// 平账笔数(结算笔数)
    private Long totalBillsAccount;// 交易笔数
    private Long totalBillsAmount;// 交易金额
    private Long wrongBillsAccount;// 错账笔数
    private Long dbBillsAccount;// 我方多 笔数
    private Long fileBillsAccount;// 商户多 笔数
    private String createTime;// 创建时间
    private String mchtNo;// 商户号

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBatId() {
        return batId;
    }

    public void setBatId(Long batId) {
        this.batId = batId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public Long getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(Long balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public Long getBalanceBillsAccount() {
        return balanceBillsAccount;
    }

    public void setBalanceBillsAccount(Long balanceBillsAccount) {
        this.balanceBillsAccount = balanceBillsAccount;
    }

    public Long getTotalBillsAccount() {
        return totalBillsAccount;
    }

    public void setTotalBillsAccount(Long totalBillsAccount) {
        this.totalBillsAccount = totalBillsAccount;
    }

    public Long getTotalBillsAmount() {
        return totalBillsAmount;
    }

    public void setTotalBillsAmount(Long totalBillsAmount) {
        this.totalBillsAmount = totalBillsAmount;
    }

    public Long getWrongBillsAccount() {
        return wrongBillsAccount;
    }

    public void setWrongBillsAccount(Long wrongBillsAccount) {
        this.wrongBillsAccount = wrongBillsAccount;
    }

    public Long getDbBillsAccount() {
        return dbBillsAccount;
    }

    public void setDbBillsAccount(Long dbBillsAccount) {
        this.dbBillsAccount = dbBillsAccount;
    }

    public Long getFileBillsAccount() {
        return fileBillsAccount;
    }

    public void setFileBillsAccount(Long fileBillsAccount) {
        this.fileBillsAccount = fileBillsAccount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMchtNo() {
        return mchtNo;
    }

    public void setMchtNo(String mchtNo) {
        this.mchtNo = mchtNo;
    }

}
