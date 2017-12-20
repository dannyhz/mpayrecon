package citic.hz.mpos.flow;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.util.StringUtils;

import citic.hz.mpos.kit.Env;
import citic.hz.mpos.kit.Globle;
import citic.hz.mpos.service.CheckLimitService;
import citic.hz.mpos.service.CmpGetDtlService;
import citic.hz.mpos.service.CmpRewriteService;
import citic.hz.mpos.service.CmpService;
import citic.hz.mpos.service.ProfitService;
import citic.hz.mpos.service.WxNotifyService;
import citic.hz.mpos.service.dao.MPayBillDao;
import citic.hz.phio.kit.PhioH;

/**
 * 每日标准对账流程(QQ)
 * @author phio
 *
 */
@WebServlet("/qqCmpFlow")
public class QQCmpFlow extends AbstractApiSvlt{
	
	public JSONObject cmpStd(HttpServletRequest request,HttpServletResponse response, JSONObject reqJson) throws ServletException, IOException {
		JSONObject rj = new JSONObject();
		if("0".equals(Globle.config.get("qqCmpFlow"))){
			log.warn("QQ对账配置为暂停");
			return rj.append("_respcd", "-101").append("_respMsg", "QQ对账配置为暂停");
		}	
		String trdt = reqJson.getString("trdt");
		if(!StringUtils.hasText(trdt)){
			trdt = PhioH.compDate("yyyyMMdd", -1);
		}
		final String fTrdt = trdt;
		String fileType = reqJson.getString("fileType");

		final String fFileType = fileType;

		//一天只做一次
		JSONObject rt = CheckLimitService.checkLimit("MPOS_BAT_QQCMP", trdt, 1);	
		if(rt.getInt("respcd") != 0){
			return rj.append("_respcd", "-100").append("_respMsg", "该日对账不得重复");
		}
		
		if(Env.needLoadData){
			//准备对账数据
			if("gz".equals(fFileType)){
				CmpGetDtlService.getQQGzFiles(fTrdt);
			}else{
				throw new RuntimeException("QQ对账文件类型不支持:"+fFileType);
			}
		}
		//ckdt为分区键，加快速度
		String ckdt = PhioH.nowDate("yyyy-MM-dd");
		//开始对账
		String chlNo = "30";	//QQ
		String batid = PhioH.newKey();
		if(Env.noprocedure){
//			MPayBillDao.getChannelOrders(chlNo);
//			MPayBillDao.getMyOrders(chlNo);
			
			
			
		}else{
			CmpService.cmpChl(batid,chlNo);
			CmpService.feeClear(batid,ckdt,chlNo);
		}
		String smdt = PhioH.nowDate("yyyyMMdd");
		if(Env.needRewrite){
			//结果回写 建议使用批量分阶段提交，减小事务的大小
			CmpRewriteService.rewriteCmpResultFail(ckdt, batid);
			
			CmpRewriteService.rewriteFeeClear(batid,chlNo,smdt);
			//隔日帐更新
			CmpRewriteService.rewriteCmpResultFailCksc(batid);
		}
		
		//到此商户入账金额计算完毕，可以清分入账了
		WxNotifyService.offerBizMsg("QQ商户清分计算完毕");

		//计算分润
		String caldt = PhioH.nowDate("yyyyMMdd");
		ProfitService.profitClear(batid, caldt);
		if(Env.needRewrite){
			CmpRewriteService.rewriteProfit(batid);
		}
		WxNotifyService.offerMsg("QQ交易分润计算完成");
		ProfitService.checkProfitBalance(batid);
		
		if(Env.needRewrite){
			//回写对账单
			CmpRewriteService.rewriteTermFee(batid);
			CmpRewriteService.rewriteCmpResult(ckdt,batid);
			CmpRewriteService.rewriteAllFinish(caldt, chlNo);
		}
		WxNotifyService.offerMsg("QQ对账结果回写完成");
		
		//多机构处理
		CmpService.bankMchTotal(batid, ckdt, chlNo);  // 多机构接入汇总  linyq 20170426
		
		if(Env.needRewrite){
			CmpRewriteService.rewriteBankMchTotal(batid, chlNo, smdt);	// 多机构接入汇总  linyq 20170426
		}
		
		rj.append("_respcd", "0").append("_respMsg", "");
		return rj;
	}


}
