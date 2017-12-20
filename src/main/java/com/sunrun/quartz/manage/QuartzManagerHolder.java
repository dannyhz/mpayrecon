package com.sunrun.quartz.manage;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;



//import org.quartz.Scheduler;

/**
 * @author tonghq
 * 任务计划类
 *
 */
public class QuartzManagerHolder {
	
	 private static  SchedulerFactory sf = new StdSchedulerFactory();
	 
	 private static Scheduler sch = getScheduler();
      

	/**
	 * 获取scheduler
	 * @return
	 */
	public static Scheduler getScheduler() {
		try {
			return sf.getScheduler();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 添加任务
	 * @param qt
	 */
	public static  void addJob(QuartTaskDO qt){
		try {
			sch.scheduleJob(createJobDetail(qt.getTaskClz(), qt.getjName(), qt.getjGroup()), 
					createCronTrigger(qt.getCornExpress(), qt.gettName(), qt.gettGroup()));
			sch.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 启动计划
	 */
	public static void startSch(){
		try {
			if(sch.isShutdown()){
				sch.start();
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
			
	}
	
	/**
	 * 停止计划
	 */
	public static void stopSch(){
		
		try {
			if(sch.isStarted()){
				sch.shutdown();
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 暂停任务
	 * @param j_name
	 * @param j_group
	 */
	public static void puseJob(String j_name,String j_group){
		try {
			JobKey jk = JobKey.jobKey(j_name, j_group);	
			if(isExistJob(jk)){
				sch.pauseJob(jk);
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 重启job
	 * @param j_name
	 * @param j_group
	 */
	public static void resumeJob(String j_name,String j_group){
		try {
			JobKey jk = JobKey.jobKey(j_name, j_group);	
			if(isExistJob(jk)){
				sch.resumeJob(jk);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 删除任务
	 * @param j_name
	 * @param j_group
	 * @param t_name
	 * @param t_group
	 */
	public static void deleteJob(String j_name,String j_group,String t_name,String t_group){
		try {
			JobKey jk = JobKey.jobKey(j_name,j_group);
			if(isExistJob(jk)){
				TriggerKey tk = TriggerKey.triggerKey(t_name, t_group);
				sch.unscheduleJob(tk);
				sch.deleteJob(jk);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 判断job是否存在
	 * @param jk
	 * @return
	 */
	public static boolean isExistJob(JobKey jk){
		JobDetail job;
		try {
			job = sch.getJobDetail(jk);
			if(job == null){
				return false;
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	/**
	 * 判断触发器是否存在
	 * @param tk
	 * @return
	 */
	public static boolean isExistTrigger(TriggerKey tk){
		try {
			Trigger trigger = sch.getTrigger(tk);
			if(trigger != null){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 创建trigger触发器
	 * @param time
	 * @return
	 */
	public static CronTrigger createCronTrigger(String time){
		  CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(time);
		  CronTrigger trigger = (CronTrigger) TriggerBuilder.newTrigger()
				  .withIdentity("TestJob","TestJobGroup")
				  .withSchedule(scheduleBuilder)
				  .build();	  
		  return trigger;
	}
	
	/**
	 * 创建触发器
	 * @param time
	 * @param JobName
	 * @param groupName
	 * @return
	 */
	public static  CronTrigger createCronTrigger(String time,String t_Name,String t_group) {
		  CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(time);
		  CronTrigger trigger = (CronTrigger) TriggerBuilder.newTrigger()
				  .withIdentity(t_Name,t_group)
				  .withSchedule(scheduleBuilder)
				  .build();	  
		  return trigger;
	}
	
	/**
	 * 创建任务
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static  JobDetail createJobDetail(@SuppressWarnings("rawtypes") Class clazz) {
		JobDetail job =JobBuilder.newJob(clazz)
				  .withIdentity(clazz.getName(),Scheduler.DEFAULT_GROUP)
				  .build();
		return job;
	}
	
	/**
	 * 创建任务
	 * @param clazz
	 * @param JobName
	 * @param groupName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static  JobDetail createJobDetail(@SuppressWarnings("rawtypes") Class clazz,String JobName,String groupName) {
		JobDetail job =JobBuilder.newJob(clazz)
				  .withIdentity(JobName,groupName)
				  .build();
		return job;
	}
	
	
}
