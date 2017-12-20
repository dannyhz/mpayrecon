package com.sunrun.quartz.job;

import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sunrun.bill.compare.Compare;
import com.sunrun.bill.fileOpr.BillFileOpr;
import com.sunrun.bill.holder.CompareDataParamHolder;
import com.sunrun.bill.holder.HolderContainer;
import com.sunrun.mpos.common.utils.PropertyUtils;
import com.sunrun.mpos.core.SpringContextHolder;

public class BillJob implements Job {
	private Compare compare = SpringContextHolder.getBean(Compare.class);
	
	private HolderContainer container = SpringContextHolder.getBean(HolderContainer.class);
	private static final String _sftpHost = PropertyUtils.getValue("com.sunrun.bill.sftp.host");
	private static final String _sftpPort = PropertyUtils.getValue("com.sunrun.bill.sftp.port");
	private static final String _sftpName = PropertyUtils.getValue("com.sunrun.bill.sftp.name");
	private static final String _sftpPsw  = PropertyUtils.getValue("com.sunrun.bill.sftp.psw");
//	private String _sfptPath = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.sftp.zj.filepath"),date);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		//翼支付以及中金的文件读取并对账、把平账的数据分别写入fq开头文件、错账的数据写入数据库
		List<CompareDataParamHolder> holderList = container.getHolderList();
		Date billDate = new Date();
    	for(CompareDataParamHolder h:holderList) {//for(Holder h:holderList)
			try {
				compare.core(h,billDate);
			} catch (Exception e) {
				throw new JobExecutionException(e.getMessage(),e);
			}
		}
    	//判断并合并支付宝与中金的平账文件 分别合并成 网商与支付宝的平账文件
    	new BillFileOpr(billDate).mergeFileControl();
    
	}
	
	
	
	 
	

}
