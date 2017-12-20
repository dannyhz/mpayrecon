package com.sunrun.quartz.job;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.sunrun.quartz.manage.QuartTaskDO;
import com.sunrun.quartz.manage.QuartzManagerHolder;


public class JobRegiste implements ServletContextListener{
	
	private Map<String,String> jobMap = new HashMap<String,String>();

	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	public void contextInitialized(ServletContextEvent arg0) {
//		QuartzManagerHolder.addJob(new QuartTaskDO(JobCst.Test.TEST_JOB,JobCst.Test.TEST_GROUP
//				,JobCst.Test.TEST_TRIGGER,JobCst.Test.TEST_TRIGGER_GROUP,JobCst.Test.CORN_EXPRESSION,TestJob.class));
		/**
		for(String key : jobMap.keySet()){
			try {
				QuartzManagerHolder.addJob(new QuartTaskDO(key+"_job",key+"_group" ,key+"_trigger",key+"_triggerGroup",jobMap.get(key),Class.forName(key)));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		QuartzManagerHolder.startSch();
		**/
	}
	
	public void init(){
		for(String key : jobMap.keySet()){
			try {
				QuartzManagerHolder.addJob(new QuartTaskDO(key+"_job",key+"_group" ,key+"_trigger",key+"_triggerGroup",jobMap.get(key),Class.forName(key)));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		QuartzManagerHolder.startSch();
	}

	public Map<String, String> getJobMap() {
		return jobMap;
	}

	public void setJobMap(Map<String, String> jobMap) {
		this.jobMap = jobMap;
	}

	
	
}
