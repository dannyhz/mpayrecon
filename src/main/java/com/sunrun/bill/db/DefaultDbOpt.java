package com.sunrun.bill.db;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunrun.bill.exception.BillDbOptException;
import com.sunrun.bill.model.BillControlDO;
import com.sunrun.bill.model.BillDetailDO;
import com.sunrun.bill.model.BillOrderDO;
import com.sunrun.bill.service.IBillService;
import com.sunrun.bill.thread.ThreadOpt;
import com.sunrun.mpos.common.utils.DateUtils;
import com.sunrun.mpos.common.utils.PropertyUtils;
import com.sunrun.mpos.common.utils.StringUtils;
import com.sunrun.mpos.core.SpringContextHolder;
import com.sunrun.util.MapBytesEntry;

public class DefaultDbOpt implements DbOpt, ThreadOpt {
    private List<BillDetailDO> billDetailList;
    private BillControlDO billControlDO;
    private List<BillOrderDO> billOrderDOList;

    private IBillService iBillService = SpringContextHolder.getBean(IBillService.class);
    private static final Logger logger = LoggerFactory.getLogger(DefaultDbOpt.class);

    // public DefaultDbOpt(String channelCode,String date){
    // this.channelCode=channelCode;
    // this.date=date;
    // }
    public DefaultDbOpt() {

    }

    public DefaultDbOpt(List<BillDetailDO> billDetailList, BillControlDO billControlDO,
        List<BillOrderDO> billOrderDOList) {
        this.billDetailList = billDetailList;
        this.billControlDO = billControlDO;
        this.billOrderDOList = billOrderDOList;
    }

    @Override
    public final HashMap<MapBytesEntry, byte[]> queryDbMap(Date date, String channelCode) throws BillDbOptException {
        HashMap<MapBytesEntry, byte[]> dbMap = null;
        List<String> billList = null;
        String _dateStr = DateUtils.formatDate(DateUtils.minusDay(date, 1), "yyyy-MM-dd");
        String _dateStrMin = _dateStr + " 00:00:00";
        String _dateStrMax = _dateStr + " 23:59:59";
        String trxIdFlag = PropertyUtils.getValue("com.sunrun.bill.trxidflag");// 2017-4-26
                                                                               // add
                                                                               // 银行流水号标记
                                                                               // 3表示生产数据
                                                                               // 1表示非生产数据
        try {
            logger.info("=====开始抽取数据库数据[date=" + _dateStr + ",channelCode=" + channelCode + "]=====");
            // long queryStarttime=System.currentTimeMillis();
            billList = iBillService.queryDataString(channelCode, _dateStrMin, _dateStrMax, trxIdFlag);// channelCode:
                                                                                                      // Z=中金
                                                                                                      // Y=翼支付
            // logger.debug("=====mybatis从数据库读取数据到List<BillDO>的耗时="+(System.currentTimeMillis()-queryStarttime));
            long putStarttime = System.currentTimeMillis();
            dbMap = new HashMap<MapBytesEntry, byte[]>(billList.size());
            for (String billStr : billList) {
                if (StringUtils.isNotEmpty(billStr)) {
                    String tmpStr[] = billStr.split(",");
                    dbMap.put(new MapBytesEntry(tmpStr[0].getBytes()), tmpStr[1].getBytes());
                }
            }
            // logger.debug("=====迭代List<String>并将其转换为HashMap<String,byte[]>耗时="+(System.currentTimeMillis()-putStarttime));
            logger.info("===== 数据库记录数=[ " + dbMap.size() + " ]条=====");
            logger.info("=====数据库数据抽取全部完成[date=" + _dateStr + ",channelCode=" + channelCode + "]=====");
        } catch (Exception e) {
            throw new BillDbOptException(e.getMessage(), e);
        } finally {
            billList = null;
        }
        return dbMap;
    }

    @Override
    public Boolean updateBillResult() throws BillDbOptException {
        // logger.info("=====开始往数据库回写对账数据 [mcht_no=" +
        // this.billControlDO.getMcht_no() + "]=====");
        // try {
        // iBillService.insertControl(this.billControlDO);
        // if (this.billDetailList != null && this.billDetailList.size() > 0) {
        // iBillService.insertDetailList(this.billDetailList,
        // Integer.valueOf(billControlDO.getId()),
        // billControlDO.getBat_id());
        // }
        // // 更新交易流水对账状态
        // if (null != this.billOrderDOList && this.billOrderDOList.size() > 0)
        // {
        // iBillService.updateOrderBillStatus(this.billOrderDOList);
        // }
        // logger.info("=====数据库回写完毕 [mcht_no=" +
        // this.billControlDO.getMcht_no() + "]=====");
        // } catch (Exception e) {
        // logger.error(e.getMessage());
        // throw new BillDbOptException(e.getMessage());
        // }

        return null;
    }

    @Override
    public Object threadRun(Date date, String channelCode) throws Exception {
        // 调用数据库读取数据
        return queryDbMap(date, channelCode);
    }

}
