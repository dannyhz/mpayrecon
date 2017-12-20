package com.sunrun.bill.model;

import java.io.Serializable;
import java.util.Date;

public class BaseDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5179122189822046596L;
    private Integer id;
    private Integer isDeleted;
    private Date createTime;
    private Date updateTime;
    private String stcd;

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
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

}
