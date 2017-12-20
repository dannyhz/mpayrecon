package citic.hz.mpos.service.dao;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.springframework.jdbc.core.JdbcTemplate;

import citic.hz.mpos.kit.XytHelper;
import citic.hz.mpos.service.MPosDataSource;

/**
 * 记录清分过程中的一些比较重要的量
 * 主要用于内管中清算日报的数据源
 * @author phio
 *
 */
public class DayReportDao {
	
	private static final Logger log = Logger.getLogger(DayReportDao.class);
	
	//threadsafe jdbctemplate
	private static JdbcTemplate jt = new JdbcTemplate(MPosDataSource.getInstance()); 
	
	/**
	 * add key1+key2+val1
	 * @param trdt
	 * @param key1
	 * @param key2
	 * @param val1
	 * @param memo
	 */
	public static void addK2V(String trdt, String key1, String key2, String val1, String memo) {
		String sqlstr = "insert into MPOS.NK_BAT2_DAY_REPORT(trdt,key1,key2,val1,memo) values(?,?,?,?,?)";
		jt.update(sqlstr, trdt,key1,key2,val1,memo);
	}
	
	/**
	 * 益选等商户每日的交易情况
	 */
	public static JSONArray specialDayRep(String smdt,String acno){
//		//益选的收款账号
//		String yxRecvAcno = "422900000000200027";
		//简单的以账号规则统计益选的商户
		String sqlstr = "select case channel_no when '05' then '微信' when '10' then '支付宝' when '20' then '百度' when '30' then 'QQ' when '40' then '京东' end 渠道,"
				+ " sum(tcnt) 笔数,sum(ttram) 交易金额,sum(rzamt) 清算金额,sum(frzamt) 其中冻结清算金额 "
				+ "from(select channel_no,tcnt,ttram,rzamt,case when LENGTH(SETTLE_BANK_CODE)<>12 or RZST in (8,9) then rzamt else 0 end frzamt "
				+ "	from MPOS.BAT2_MCH_FEE_CLEAR where smdt=? and SETTLE_ACCT = ? ) group by channel_no";
		return XytHelper.cvtDbList2Json(jt.queryForList(sqlstr, smdt,acno));
	}
	
}
