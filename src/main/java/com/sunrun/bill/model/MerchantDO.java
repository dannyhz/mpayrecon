package com.sunrun.bill.model;

import java.util.Date;

import com.sunrun.constant.MerchantStatus;
import com.sunrun.constant.MerchantType;
import com.sunrun.mpos.common.utils.StringUtils;

/**
 * @author tongheqiang 商户类 2017年3月16日
 */
public class MerchantDO extends BaseDO {

    private static final long serialVersionUID = -8215049066417325521L;

    private String mchtNo; // 商户号
    private String mchtName; // 商户名称
    private Integer settleAccountFlag; // 结算银行标志(0: 行内 1: 行外)
    private String settleAccount; // 结算帐号(只支持行内)
    private String settleName; // 结算户名
    private String settleBankCode; // 结算银行编码
    private String settleBankName; // 结算银行名称
    private String stcd; // A 正常 C 关闭 D 删除
    private String stcdName;// 状态名称
    private String createBy; // 创建帐号
    private String updateBy; // 修改帐号
    private Date createTime; // 创建时间
    private Date updateTime; // 修改时间

    private String sftpFilePath;// 商户在sftp上的文件路径,唯一

    private String olpSwitch;// 商户的外联开关，控制向商户FTP服务器发送对账文件,1代表开启

    private String olpFlag;// 调用外联本地标记 如果有此标记 表示当天已发起过外联请求 不用重复发送,唯一

    private String olpCode;// 发起过外联请求时，商户编码

    private String settleBankBranchCode;// 清算银行联行号

    private Long chargePerTrade;// 单笔手续费(分)

    private Long upperLimitPerTrade;// 单笔最大金额(分)

    private Long upperLimitPerDayPerCustomer;// 日累计交易上限

    private String legalPersonName;// 法人姓名

    private String legalPersonId;// 法人身份证

    private String businessLicense;// 营业执照

    private String contactPersonName;// 联系人姓名

    private String contactPhone;// 联系人手机号

    private String mchtType;// 商户类型 code值

    private String mchtTypeName;// 商户类型 name值

    public String getMchtNo() {
        return mchtNo;
    }

    public void setMchtNo(String mchtNo) {
        this.mchtNo = mchtNo;
        if (StringUtils.isNotBlank(mchtNo) && mchtNo.length() > 4) {
            MerchantType type = MerchantType.getByType(mchtNo.substring(0, 4));
            this.mchtType = (null == type ? "" : type.getType());
            this.mchtTypeName = (null == type ? "" : type.getDesc());
        }
    }

    public String getMchtName() {
        return mchtName;
    }

    public void setMchtName(String mchtName) {
        this.mchtName = mchtName;
    }

    public Integer getSettleAccountFlag() {
        return settleAccountFlag;
    }

    public void setSettleAccountFlag(Integer settleAccountFlag) {
        this.settleAccountFlag = settleAccountFlag;
    }

    public String getSettleAccount() {
        return settleAccount;
    }

    public void setSettleAccount(String settleAccount) {
        this.settleAccount = settleAccount;
    }

    public String getSettleName() {
        return settleName;
    }

    public void setSettleName(String settleName) {
        this.settleName = settleName;
    }

    public String getSettleBankCode() {
        return settleBankCode;
    }

    public void setSettleBankCode(String settleBankCode) {
        this.settleBankCode = settleBankCode;
    }

    public String getSettleBankName() {
        return settleBankName;
    }

    public void setSettleBankName(String settleBankName) {
        this.settleBankName = settleBankName;
    }

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
        this.stcdName = MerchantStatus.getByStatus(stcd) == null ? "" : MerchantStatus.getByStatus(stcd).getDesc();
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getChargePerTrade() {
        return chargePerTrade;
    }

    public void setChargePerTrade(Long chargePerTrade) {
        this.chargePerTrade = chargePerTrade;
    }

    public Long getUpperLimitPerTrade() {
        return upperLimitPerTrade;
    }

    public void setUpperLimitPerTrade(Long upperLimitPerTrade) {
        this.upperLimitPerTrade = upperLimitPerTrade;
    }

    public Long getUpperLimitPerDayPerCustomer() {
        return upperLimitPerDayPerCustomer;
    }

    public void setUpperLimitPerDayPerCustomer(Long upperLimitPerDayPerCustomer) {
        this.upperLimitPerDayPerCustomer = upperLimitPerDayPerCustomer;
    }

    public String getLegalPersonName() {
        return legalPersonName;
    }

    public void setLegalPersonName(String legalPersonName) {
        this.legalPersonName = legalPersonName;
    }

    public String getLegalPersonId() {
        return legalPersonId;
    }

    public void setLegalPersonId(String legalPersonId) {
        this.legalPersonId = legalPersonId;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getSettleBankBranchCode() {
        return settleBankBranchCode;
    }

    public void setSettleBankBranchCode(String settleBankBranchCode) {
        this.settleBankBranchCode = settleBankBranchCode;
    }

    public String getMchtType() {
        return mchtType;
    }

    public void setMchtType(String mchtType) {
        this.mchtType = mchtType;
    }

    public String getMchtTypeName() {
        return mchtTypeName;
    }

    public void setMchtTypeName(String mchtTypeName) {
        this.mchtTypeName = mchtTypeName;
    }

    public String getStcdName() {
        return stcdName;
    }

    public void setStcdName(String stcdName) {
        this.stcdName = stcdName;
    }

    public String getSftpFilePath() {
        return sftpFilePath;
    }

    public void setSftpFilePath(String sftpFilePath) {
        this.sftpFilePath = sftpFilePath;
    }

    public String getOlpSwitch() {
        return olpSwitch;
    }

    public void setOlpSwitch(String olpSwitch) {
        this.olpSwitch = olpSwitch;
    }

    public String getOlpFlag() {
        return olpFlag;
    }

    public void setOlpFlag(String olpFlag) {
        this.olpFlag = olpFlag;
    }

    public String getOlpCode() {
        return olpCode;
    }

    public void setOlpCode(String olpCode) {
        this.olpCode = olpCode;
    }

}
