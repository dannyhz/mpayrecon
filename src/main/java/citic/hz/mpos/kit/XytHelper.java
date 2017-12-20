package citic.hz.mpos.kit;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * xyt业务辅助公共类
 * @author phio
 *
 */
public class XytHelper {
	
	private static final Logger log = Logger.getLogger(XytHelper.class);
	
	private static Config config = Config.getConfig();
	
	public static JSONObject cvtDbMap2Json(Map<String, Object> row){
		JSONObject rt = new JSONObject();
		Set<String> keys = row.keySet();
		for (String key : keys) {
			rt.put(key.toLowerCase(), row.get(key));
		}
		return rt;
	}

	public static JSONArray cvtDbList2Json(List<Map<String, Object>> rows ){
		JSONArray rt = new JSONArray();
		for (Map<String, Object> row : rows) {
			rt.add(cvtDbMap2Json(row));
		}
		return rt;
	}
	

}
