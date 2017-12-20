package com.sunrun.bill.service;

import java.util.List;

import com.sunrun.bill.model.MerchantDO;

/**
 * 商户服务.
 *
 * @author liuwen
 * @since V1.0.0
 */
public interface IMerchantService {

    List<MerchantDO> queryAll();

}
