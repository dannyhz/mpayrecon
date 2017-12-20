package com.sunrun.quartz.manage;

/**
 * @author tonghq
 *	定时任务类
 */
public class QuartTaskDO {
	
	private String jName;//任务
	private String jGroup;//任务组
	private String tName;//触发器
	private String tGroup;//触发器组
	private String cornExpress;//定时的corn表达式
	private Class taskClz;
	
	
	public QuartTaskDO(){
		
	}
	
	
	
	public QuartTaskDO(String jName, String jGroup, String tName,
			String tGroup, String cornExpress, Class taskClz) {
		this.jName = jName;
		this.jGroup = jGroup;
		this.tName = tName;
		this.tGroup = tGroup;
		this.cornExpress = cornExpress;
		this.taskClz = taskClz;
	}



	public Class getTaskClz() {
		return taskClz;
	}
	public void setTaskClz(Class taskClz) {
		this.taskClz = taskClz;
	}
	public String getjName() {
		return jName;
	}
	public void setjName(String jName) {
		this.jName = jName;
	}
	public String getjGroup() {
		return jGroup;
	}
	public void setjGroup(String jGroup) {
		this.jGroup = jGroup;
	}
	public String gettName() {
		return tName;
	}
	public void settName(String tName) {
		this.tName = tName;
	}
	public String gettGroup() {
		return tGroup;
	}
	public void settGroup(String tGroup) {
		this.tGroup = tGroup;
	}
	public String getCornExpress() {
		return cornExpress;
	}
	public void setCornExpress(String cornExpress) {
		this.cornExpress = cornExpress;
	}		

}
