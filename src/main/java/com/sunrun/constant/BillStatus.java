package com.sunrun.constant;

/**
 * 对账状态
 * 
 * @author liuwen
 * @since V1.0.0
 */
public enum BillStatus {
    BILL_NONE("NA", "未对账"), BILL_SUCCESS("S", "对账成功"), BILL_FAIL("F", "对账失败");

    private String status;

    private String desc;

    private BillStatus(String status, String desc) {
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

    public static BillStatus getByStatus(String status) {
        for (BillStatus value : BillStatus.values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }

        return null;
    }
}
