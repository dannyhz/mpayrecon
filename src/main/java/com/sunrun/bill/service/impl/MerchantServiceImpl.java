package com.sunrun.bill.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunrun.bill.dao.slave.IMerchantQueryDao;
import com.sunrun.bill.model.MerchantDO;
import com.sunrun.bill.service.IMerchantService;

/**
 * 商户服务.
 *
 * @author liuwen
 * @since V1.0.0
 */
@Service
public class MerchantServiceImpl implements IMerchantService {

    @Autowired
    private IMerchantQueryDao merchantQueryDao;

    @Override
    public List<MerchantDO> queryAll() {
        return merchantQueryDao.queryAll();
    }

}
