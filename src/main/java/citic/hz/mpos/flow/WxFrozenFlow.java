package citic.hz.mpos.flow;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.util.StringUtils;

import citic.hz.mpos.kit.Env;
import citic.hz.mpos.service.CheckLimitService;
import citic.hz.mpos.service.CmpGetDtlService;
import citic.hz.mpos.service.CmpRewriteService;
import citic.hz.mpos.service.CmpService;
import citic.hz.mpos.service.WxNotifyService;
import citic.hz.phio.kit.PhioH;

/**
 * 微信冻结单处理流程
 * @author phio
 *
 */
@WebServlet("/wxFrozenFlow")
public class WxFrozenFlow extends AbstractApiSvlt{
	
	/**
	 * flow调用的入口
	 * curl -d '{"_action":"cmpStd","fileType":"gz"}' 127.0.0.1:8080/MposBat/wxFrozenFlow
	 * @param request
	 * @param response
	 * @param reqJson
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public JSONObject cmpStd(HttpServletRequest request,HttpServletResponse response, JSONObject reqJson) throws ServletException, IOException {
		JSONObject rj = new JSONObject();
		String trdt = reqJson.getString("trdt");
		if(!StringUtils.hasText(trdt))
			trdt = PhioH.compDate("yyyyMMdd", -1);
		final String fTrdt = trdt;
		String fileType = reqJson.getString("fileType");
		if(null == fileType)
			fileType = "zip";	//保持和对账文件逻辑一致，实际不支持
		final String fFileType = fileType;

		//一天只做一次
		JSONObject rt = CheckLimitService.checkLimit("MPOS_BAT_WX_FRZ", trdt, 1);	
		if(rt.getInt("respcd") != 0)
			return rj.append("_respcd", "-100").append("_respMsg", "该日取冻结文件不得重复");

		String batno = PhioH.newKey();
		
		
		if(Env.needLoadData){
			//读取文件
			if("gz".equals(fFileType)){
				CmpGetDtlService.getWxFrzGzFiles(fTrdt,batno);
			}else{
				throw new RuntimeException("微信冻结文件类型不支持:"+fFileType);
			}
		}
		//微信冻结单处理，目前仅补充mpos字段
		String chlNo = "05";	//微信
		CmpService.frozen(batno, chlNo);
		
		if(Env.needRewrite){
			//结果回写
			CmpRewriteService.rewriteFrozen(batno);
		}
		WxNotifyService.offerMsg("微信冻结单回写完成");

		rj.append("_respcd", "0").append("_respMsg", "");
		return rj;
	}


}
