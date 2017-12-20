package citic.hz.mpos.service;

import org.apache.log4j.Logger;

import citic.hz.mpos.service.dao.MPosBatDao;

/**
 * 分润服务
 * @author phio
 *
 */
public class ProfitService {

	private static final Logger log = Logger.getLogger(ProfitService.class);

	
	/**
	 * 分润计算
	 */
	public static void profitClear(String batid,String ckdt){
		log.info("开始分润计算"+batid+"|"+ckdt);
		String rt = MPosBatDao.profitClear(batid,ckdt);
		if(!"0".equals(rt)){
			log.error("分润计算失败："+rt);
			WxNotifyService.offerMsg("分润计算失败："+rt);
			throw new RuntimeException("分润计算："+rt);
		}else{
			log.info("分润计算完成");
			//WxNotifyService.offerMsg("分润计算完成"+batid+"|"+ckdt);
		}
	}
	
	/**
	 * 检查分润情况
	 * @param batid
	 */
	public static void checkProfitBalance(String batid){
		String msg = MPosBatDao.checkProfitBalance(batid).toString();
		WxNotifyService.offerCheckMsg(msg);
	}
}
