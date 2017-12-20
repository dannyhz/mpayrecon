package com.sunrun.bill.dao.slave;

import java.util.List;

import com.sunrun.bill.model.MerchantDO;

public interface IMerchantQueryDao {

    MerchantDO selectByMchtNo(String mchtNo);

    /**
     * 商户号:4位类型号段+12位顺序数字 .查询后12位最大值.
     * 
     */
    String getMaxMchtNo();

    List<MerchantDO> queryAll();

}