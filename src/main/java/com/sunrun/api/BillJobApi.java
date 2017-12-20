package com.sunrun.api;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunrun.bill.compare.Compare;
import com.sunrun.bill.fileOpr.BillFileOpr;
import com.sunrun.bill.holder.CompareDataParamHolder;
import com.sunrun.bill.holder.HolderContainer;
import com.sunrun.mpos.common.utils.DateUtils;
import com.sunrun.mpos.common.utils.PropertyUtils;
import com.sunrun.mpos.core.SpringContextHolder;

public class BillJobApi extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(BillJobApi.class);

	private Compare compare = SpringContextHolder.getBean(Compare.class);
	
	private HolderContainer container = SpringContextHolder.getBean(HolderContainer.class);
	private static final String _sftpHost = PropertyUtils.getValue("com.sunrun.bill.sftp.host");
	private static final String _sftpPort = PropertyUtils.getValue("com.sunrun.bill.sftp.port");
	private static final String _sftpName = PropertyUtils.getValue("com.sunrun.bill.sftp.name");
	private static final String _sftpPsw  = PropertyUtils.getValue("com.sunrun.bill.sftp.psw");
//	private String _sfptPath = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.sftp.zj.filepath"),date);

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		execute(req);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		execute(req);
	}
	
	private void execute(HttpServletRequest req) {
		//翼支付以及中金的文件读取并对账、把平账的数据分别写入fq开头文件、错账的数据写入数据库
		List<CompareDataParamHolder> holderList = container.getHolderList();
		
		String reqDateStr = req.getParameter("date");
		
		Date billDate = new Date();
		//如果传入日期 则去对账传入日期的帐，如果不传入日期则默认去对昨天的帐
		if (reqDateStr!=null) {
			try {
				billDate = DateUtils.addDay(DateUtils.parse(reqDateStr,"yyyyMMdd"),1);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}  
		
    	for(CompareDataParamHolder h:holderList) {//for(Holder h:holderList)
			try {
				compare.core(h,billDate);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
    	//判断并合并易支付与中金的平账文件 分别合并成 网商与支付宝的平账文件
    	new BillFileOpr(billDate).mergeFileControl();
    	 
    	//文件合并完成之后 将sftp上的文件 通过外联分别推送到网商、支付宝和聚有财
    	new DkPutBillfileOpr(billDate).putFile();
    
	}
	
	
	
	 
	

}
