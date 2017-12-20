package citic.hz.mpos.service;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import citic.hz.mpos.kit.Globle;
import citic.hz.mpos.service.dao.DayReportDao;
import citic.hz.phio.kit.PhioH;

/**
 * 客户的特殊定制的一些服务
 * @author phio
 *
 */
public class CustomSpecialService {
	
	private static final Logger log = Logger.getLogger(CustomSpecialService.class);
	
	/**
	 * 益选等代理的每日交易数据汇总
	 */
	public static void yxSum(){
		String smdt = PhioH.nowDate("yyyyMMdd");
		String specialSmsDayRep = Globle.config.get("specialSmsDayRep");
		JSONArray ja = JSONValue.parseJa(specialSmsDayRep);
		for(int i=0;i<ja.size();i++){
			JSONObject jo = ja.getJSONObject(i);
			log.info("开始"+jo.getString("name")+"汇总" );
			String dayrep = jo.getString("name")+"汇总:"+DayReportDao.specialDayRep(smdt, jo.getString("acno")).toString();
			WxNotifyService.offerCheckMsg(dayrep);
			SmsNotifyService.offerMsg(dayrep,jo.getString("mobNos"));
		}
	}

}
