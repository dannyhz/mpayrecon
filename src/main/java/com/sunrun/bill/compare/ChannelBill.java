package com.sunrun.bill.compare;

import java.util.Date;

/**
 * 渠道对账.
 *
 * @author liuwen
 * @since V1.0.0
 */
public interface ChannelBill {

    /**
     * 渠道对账核心方法.
     *
     * @param date
     */
    void core(final Date billDate);

}
