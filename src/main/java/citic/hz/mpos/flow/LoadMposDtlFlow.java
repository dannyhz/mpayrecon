package citic.hz.mpos.flow;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.util.StringUtils;

import citic.hz.mpos.service.CmpGetDtlService;
import citic.hz.mpos.service.CmpService;
import citic.hz.phio.kit.PhioH;

/**
 * 每日获取上日mpos流水流程
 * @author phio
 *
 */
@WebServlet("/ldMposFlow")
public class LoadMposDtlFlow extends AbstractApiSvlt{
	
	/**
	 * curl -d '{"_action":"loadDtl"}' 127.0.0.1:8080/MposBat/ldMposFlow
	 * @param request
	 * @param response
	 * @param reqJson
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public JSONObject loadDtl(HttpServletRequest request,HttpServletResponse response, JSONObject reqJson) throws ServletException, IOException {
		JSONObject rj = new JSONObject();
		String trdt = reqJson.getString("trdt");
		if(!StringUtils.hasText(trdt))
			trdt = PhioH.compDate("yyyyMMdd", -1);
		
		//每天提前做掉mpos导数操作
		CmpGetDtlService.loadConfig();
		CmpGetDtlService.loadMposDtl(trdt);
		CmpGetDtlService.loadMposDtlFail(trdt);
		CmpGetDtlService.ovRevokeNo();
		//额外增加流水的费率校验
		CmpService.checkDtlFeeRate();
		
		rj.append("_respcd", "0").append("_respMsg", "");
		return rj;
	}


}
