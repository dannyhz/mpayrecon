package citic.hz.mpos.service;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import citic.hz.mpos.service.dao.MPosBatDao;
import citic.hz.mpos.service.event.EventService;

/**
 * 回写数据库服务
 * @author phio
 *
 */
public class CmpRewriteService {

	private static final Logger log = Logger.getLogger(CmpRewriteService.class);

	/**
	 * 回写BAT2_CMP_RESULT
	 */
	public static void rewriteCmpResult(String ckdt,String batid){
		log.info("开始回写BAT2_CMP_RESULT:" + batid);
		try{
			MPosBatDao.rewriteCmpResult(ckdt, batid);
			log.info("完成回写BAT2_CMP_RESULT:" + batid);
		}catch(Exception e){
			log.error("回写BAT2_CMP_RESULT失败",e);
			WxNotifyService.offerMsg("回写BAT2_CMP_RESULT失败");
		}
	}

	
	/**
	 * 回写BAT2_CMP_RESULT_FAIL
	 */
	public static void rewriteCmpResultFail(String ckdt,String batid){
		log.info("开始回写BAT2_CMP_RESULT_FAIL:" + batid);
		try{
			MPosBatDao.rewriteCmpResultFail(ckdt, batid);
			log.info("完成回写BAT2_CMP_RESULT_FAIL:" + batid);
		}catch(Exception e){
			log.error("回写BAT2_CMP_RESULT_FAIL失败",e);
			WxNotifyService.offerMsg("回写BAT2_CMP_RESULT_FAIL失败");
		}
	}

	/**
	 * 回写BAT2_MCH_FEE_CLEAR
	 * chlNo和smdt仅用于通知，不用于清算逻辑
	 */
	public static void rewriteFeeClear(String batid,String chlNo,String smdt){
		log.info("开始回写BAT2_MCH_FEE_CLEAR:" + batid);
		try{
			MPosBatDao.rewriteFeeClear(batid);
			log.info("完成回写BAT2_MCH_FEE_CLEAR:" + batid);
			try{
				MPosBatDao.addFlag("REWRITE_CLEAR_"+chlNo+"_"+smdt, "OK");
			}catch(Throwable e){
				log.error("通知主库回写商户结算信息    重复",e);
			}
		}catch(Exception e){
			log.error("回写BAT2_MCH_FEE_CLEAR失败",e);
			WxNotifyService.offerMsg("回写BAT2_MCH_FEE_CLEAR失败");
		}
	}

	/**
	 * 回写BAT2_TERM_FEE_CLEAR
	 */
	public static void rewriteTermFee(String batid){
		log.info("开始回写BAT2_TERM_FEE_CLEAR:" + batid);
		try{
			MPosBatDao.rewriteTermFee(batid);
			log.info("完成回写BAT2_TERM_FEE_CLEAR:" + batid);
		}catch(Exception e){
			log.error("回写BAT2_TERM_FEE_CLEAR失败",e);
			WxNotifyService.offerMsg("回写BAT2_TERM_FEE_CLEAR失败");
		}
	}
	
	/**
	 * 回写BAT2_BANK_MCH_TOTAL
	 * chlNo和smdt仅用于通知，不用于清算逻辑
	 * @param batid
	 * @param chlNo
	 * @param smdt
	 * @author LINYQ
	 * @date 20170425
	 */
	public static void rewriteBankMchTotal(String batid,String chlNo,String smdt) {
		log.info("开始回写BAT2_BANK_MCH_TOTAL:" + batid);
		try {
			MPosBatDao.rewriteBankMchTotal(batid);
			log.info("完成回写BAT2_BANK_MCH_TOTAL:" + batid);
			try {
				MPosBatDao.addFlag("REWRITE_BANK_MCH_TOTAL_" + chlNo + "_" + smdt, "OK");
			} catch(Throwable e) {
				log.error("通知主库回写多机构商户结算信息    重复", e);
			}
		} catch(Exception e) {
			log.error("回写BAT2_BANK_MCH_TOTAL失败", e);
			WxNotifyService.offerMsg("回写BAT2_BANK_MCH_TOTAL失败");
		}
	}

	/**
	 * 同步隔日对账成功记录
	 * BAT2_CMP_RESULT_FAIL
	 * @param batid
	 */
	public static void rewriteCmpResultFailCksc(String batid){
		log.info("开始同步隔日对账成功记录:" + batid);
		try{
			MPosBatDao.rewriteCmpResultFailCksc(batid);
			log.info("完成同步隔日对账成功记录:" + batid);
		}catch(Exception e){
			log.error("同步隔日对账成功记录失败",e);
			WxNotifyService.offerMsg("同步隔日对账成功记录失败");
		}
	}

	/**
	 * 回写分润结果
	 * BAT2_AGT_PROFIT
	 * @param batid
	 */
	public static void rewriteProfit(String batid){
		log.info("开始回写分润结果:" + batid);
		try{
			MPosBatDao.rewriteProfit(batid);
			log.info("完成回写分润结果:" + batid);
		}catch(Exception e){
			log.error("回写分润结果失败",e);
			WxNotifyService.offerMsg("回写分润结果失败");
		}
	}

	/**
	 * 回写冻结单
	 * BAT2_CMP_CHL_FROZEN
	 * @param batno
	 */
	public static void rewriteFrozen(String batno){
		log.info("开始回写冻结单:" + batno);
		try{
			MPosBatDao.rewriteFrozen(batno);
			log.info("完成回写冻结单:" + batno);
		}catch(Exception e){
			log.error("回写冻结单失败",e);
			WxNotifyService.offerMsg("回写冻结单失败");
		}
	}
	
	/**
	 * 回写所有对账完毕，通知主库可以开放对账单查询下载
	 * @param ckdt8
	 * @param chlNo
	 */
	public static void rewriteAllFinish(String ckdt8,String chlNo){
		//TODO 暂时是微信、支付宝、百付宝都好了，才标记，以后可优化该环节
		//qq、京东暂时不计入
		if("30".equals(chlNo) ||"40".equals(chlNo)){
			return;
		}
		//故意limit设为2，下次时就会超限，于是触发通知标记
		JSONObject rt = CheckLimitService.checkLimit("REWRITE_CMP_RESULT", ckdt8, 3-1);
		if(rt.getInt("respcd") != 0){
			//通知主库回写对账单明细完成
			try{
				MPosBatDao.addFlag("REWRITE_CMP_RESULT_"+ckdt8, "OK");
				EventService.pub("/rewrite/all/finish", null);
			}catch(Throwable e){
				log.error("通知主库回写对账单明细    重复",e);
			}
		}	
	}
	
}
