package citic.hz.mpos.service.dao;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

import citic.hz.mpos.service.MPosDataSource;

public class CheckLimitDao {
	
	private static final Logger log = Logger.getLogger(CheckLimitDao.class);
	
	private static JdbcTemplate jt = new JdbcTemplate(MPosDataSource.getInstance()); 
	
	/**
	 * 校验额度限制，如果成功返回当前使用额度量（注意：只在成功时）
	 * @param ywtag
	 * @param checkSth
	 * @param limit
	 * @param step
	 * @return
	 */
	public static JSONObject checkLimit(String ywtag,String checkSth,long limit, long step){
		//默认超限
		JSONObject rt = new JSONObject().append("respcd", -1).append("cnt", limit);
		//简单的先select后insert、update流程，由于有pk限制，所以不怕有并发差错
		String sqlstr = "select CNT from T_CHECKLIMIT where YWTAG=? and CHECKSTH=?";
		List<Map<String, Object>> lst = jt.queryForList(sqlstr,ywtag,checkSth);
		if(lst.size()==0){	//第一次
			sqlstr = "insert into T_CHECKLIMIT(YWTAG,CHECKSTH,CNT) values(?,?,?)";
			try{
				jt.update(sqlstr,ywtag,checkSth,step);
				return rt.append("respcd", 0).append("cnt", step);
			}catch(DuplicateKeyException e){
				sqlstr = "select cnt from final table(update T_CHECKLIMIT set cnt=cnt+? where YWTAG=? and CHECKSTH=? and CNT<=?)";
				List<Map<String, Object>> updLst = jt.queryForList(sqlstr,step,ywtag,checkSth,limit-step);
				if(updLst.size() == 0)	//超限
					return rt;
				else{
					long cnt = (Long)updLst.get(0).get("cnt");
					return rt.append("respcd", 0).append("cnt", cnt);
				}
			}	
		}else{	//已有
			long cnt = (Long)lst.get(0).get("cnt");
			if(cnt >= limit)	//超限
				return rt;
			sqlstr = "select cnt from final table(update T_CHECKLIMIT set cnt=cnt+? where YWTAG=? and CHECKSTH=? and CNT<=?)";
			List<Map<String, Object>> updLst = jt.queryForList(sqlstr,step,ywtag,checkSth,limit-step);
			if(updLst.size() == 0) //并发情况下还是超限了
				return rt;
			else{
				cnt = (Long)updLst.get(0).get("cnt");
				return rt.append("respcd", 0).append("cnt", cnt);
			}
		}
	}

	/**
	 * 退还已经扣减的额度
	 * @param ywtag
	 * @param checkSth
	 * @param step
	 * @return 1=成功 0=失败
	 */
	public static int backCheckLimit(String ywtag,String checkSth,long step){
		String sqlstr = "update T_CHECKLIMIT set cnt=cnt-? where YWTAG=? and CHECKSTH=? and CNT>=?";
		return jt.update(sqlstr,step,ywtag,checkSth,step);
	}
	
	/**CHECKLIMIT
	 * 校验 是否能参加活动 
	 * @param itemId 2016yyh
	 * @param checksth cuno limit
	 * @return
	 */
	public static long qryCheckLimit(String itemId,String checksth){
		String sqlstr="select CNT from T_CHECKLIMIT WHERE YWTAG=? and checksth=? ";
		List<Map<String,Object>> li = jt.queryForList(sqlstr, itemId,checksth);
		if(li.size()>0){
			return (Long)li.get(0).get("CNT");
		}
		return 0L;
	}

}
