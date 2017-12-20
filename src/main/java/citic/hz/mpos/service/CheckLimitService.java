package citic.hz.mpos.service;

import org.json.simple.JSONObject;

import citic.hz.mpos.service.dao.CheckLimitDao;
/**
 * 额度、次数限制的校验
 * @author phio
 *
 */
public class CheckLimitService {

	/**
	 * 校验额度限制，并返回当前使用额度量
	 * @param ywtag
	 * @param checkSth ywtag+checkSth 唯一
	 * @param limit 限制额度
	 * @param step 本次使用额度量
	 * @return {"respcd":0,"cnt":2} respcd:0=成功，-1=超限(此时返回的cnt==limit)
	 */
	public static JSONObject checkLimit(String ywtag,String checkSth,long limit,long step){
		JSONObject rt = CheckLimitDao.checkLimit(ywtag,checkSth, limit,step);
		//如果超限了，需要查询当前使用额度量
		if(rt.getInt("respcd") != 0){
			long cnt = qryCheckLimit(ywtag, checkSth);
			return rt.append("cnt", cnt);
		}else
			return rt;
	}
	
	/**
	 * 校验次数限制，并返回最终次数
	 * @param ywtag
	 * @param checkSth
	 * @param limit
	 * @return {"respcd":0,"cnt":2} respcd:0=成功，-1=超限
	 */
	public static JSONObject checkLimit(String ywtag,String checkSth,int limit){
		//step==1的情况下，一旦超限，肯定是cnt==limit，不用再次查询
		return CheckLimitDao.checkLimit(ywtag, checkSth, limit,1L);
	}
	
	/**
	 * 返还额度
	 * @param ywtag
	 * @param checkSth
	 * @param step
	 * @return 1=成功 0=失败
	 */
	public static int backCheckLimit(String ywtag,String checkSth,long step){
		return CheckLimitDao.backCheckLimit(ywtag,checkSth, step);
	}
	
	/**
	 * 试一下额度限制，不做变更
	 * @param itemId
	 * @param checksth
	 * @param limit
	 * @param step
	 * @return
	 */
	public static boolean testCheckLimit(String ywtag,String checkSth,long limit,long step){
		long cnt = qryCheckLimit(ywtag, checkSth);
		return cnt + step <= limit;
		//return LotteryDao.testCheckLimit(ywtag, checksth, limit, step);
	}
	
	/**
	 * 查询当前额度使用情况
	 * @param itemId
	 * @param checksth
	 * @return
	 */
	public static long qryCheckLimit(String ywtag,String checkSth){
		return CheckLimitDao.qryCheckLimit(ywtag, checkSth);
	}
}
