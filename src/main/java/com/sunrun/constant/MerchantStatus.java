package com.sunrun.constant;

/**
 * TODO 类的主要职责和可能被使用的场景
 *
 * @author liuwen
 * @since V1.0.0
 */
public enum MerchantStatus {

    normal("A", "正常"), closed("C", "关闭"), deleted("D", "删除");

    private String status;

    private String desc;

    private MerchantStatus(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static MerchantStatus getByStatus(String status) {
        for (MerchantStatus value : MerchantStatus.values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }

        return null;
    }
}
