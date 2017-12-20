package citic.hz.mpos.service;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import citic.hz.mpos.service.dao.MPosBatDao;

/**
 * 对账服务
 * @author phio
 *
 */
public class CmpService {

	private static final Logger log = Logger.getLogger(CmpService.class);

	/**
	 * 进行渠道（微信、支付宝等）对账
	 */
	public static void cmpChl(String batid,String chlNo){
		log.info("开始渠道对账" + batid+"|"+chlNo);
		String rt = MPosBatDao.cmpChl(batid,chlNo);
		if(!"0".equals(rt)){
			log.error("渠道对账失败："+rt);
			WxNotifyService.offerMsg("渠道对账失败："+rt);
			throw new RuntimeException("渠道对账失败："+rt);
		}else{
			log.info("渠道对账完成");
			WxNotifyService.offerMsg("渠道对账完成"+chlNo);
		}
	}
	
	/**
	 * 按对账批次对对账成功的交易进行手续费计算
	 * @param batid
	 */
	public static void feeClear(String batid,String ckdt,String chlNo){
		log.info("开始手续费计算"+batid+"|"+chlNo);
		String rt = MPosBatDao.feeClear(batid,ckdt,chlNo);
		if(!"0".equals(rt)){
			log.error("手续费计算失败："+rt);
			WxNotifyService.offerMsg("手续费计算失败："+rt);
			throw new RuntimeException("手续费计算："+rt);
		}else{
			log.info("手续费计算完成");
			WxNotifyService.offerMsg("手续费计算完成"+batid+"|"+chlNo);
		}
	}
	
	/**
	 * 按对账批次对对账成功的交易进行交易汇总_多机构
	 * @param batid 对账批次号，为0时系统自动计算最大的批次号
	 * @param ckdt 对账日期
	 * @param chlNo 渠道号
	 * @return errmsg
	 * @author LINYQ 
	 * @date 20170425
	 */
	public static void bankMchTotal(String batid, String ckdt, String chlNo) {
		log.info("开始多机构交易汇总计算" + batid + " | " + chlNo);
		String rt = MPosBatDao.bankMchTotal(batid, ckdt, chlNo);
		if(!"0".equals(rt)) {
			log.error("多机构交易汇总计算失败：" + rt);
			WxNotifyService.offerMsg("多机构交易汇总计算失败：" + rt);
			throw new RuntimeException("多机构交易汇总计算：" + rt);
		} else {
			log.info("多机构交易汇总计算完成");
			WxNotifyService.offerMsg("多机构交易汇总计算 " + batid + " | " + chlNo);
		}
	}
	
	/**
	 * 进行邦联数据库的连接测试
	 */
	public static void testFredLink(){
		//尝试次数
		int trycnt = 3;
		while(trycnt>0){
			try {
				MPosBatDao.testFredLink();
				break;
			} catch (Throwable e) {
				trycnt--;
			}
		}
		if(trycnt<=0){
			log.error("测试邦联连接失败");
			WxNotifyService.offerMsg("测试邦联连接失败");
			throw new RuntimeException("测试邦联连接失败");
		}
	}
	
	/**
	 * 校验流水费率的完整性
	 */
	public static void checkDtlFeeRate(){
		JSONObject rt = MPosBatDao.checkDtlFeeRate();
		if(rt.getInt("_respcd") == 1){	//有异常，报警
			log.error("校验流水费率完整性：异常");
			WxNotifyService.offerCheckMsg("流水费率异常"+rt);
		}
	}

	
	/**
	 * 渠道冻结单处理(只有微信有)
	 */
	public static void frozen(String batno,String chlNo){
		log.info("开始渠道冻结单处理" + batno+"|"+chlNo);
		String rt = MPosBatDao.frozen(batno,chlNo);
		if(!"0".equals(rt)){
			log.error("渠道冻结单处理失败："+rt);
			WxNotifyService.offerMsg("渠道冻结单处理失败："+rt);
			throw new RuntimeException("渠道冻结单处理失败："+rt);
		}else{
			log.info("渠道冻结单处理完成");
			WxNotifyService.offerMsg("渠道冻结单处理完成"+chlNo);
		}
	}

}
