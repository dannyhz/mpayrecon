package com.sunrun.constant;

/**
 * TODO 类的主要职责和可能被使用的场景
 *
 * @author liuwen
 * @since V1.0.0
 */
public enum MerchantType {

    PLATFORM("6100", "平台类型"), DIRECTREPAYMENT("6510", "直连还款"), DIRECTFINANCE("6520", "直连理财");

    private String type;

    private String desc;

    private MerchantType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static MerchantType getByType(String type) {
        for (MerchantType value : MerchantType.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }

        return null;
    }
}
