package citic.hz.mpos.service;

import javax.sql.DataSource;

import citic.hz.mpos.kit.ApxLoaderListener;

public class MPosDataSource {
	
	/**
	 * 获取xytdb的datasource
	 * @return
	 */
	public static DataSource getInstance(){
		return ApxLoaderListener.getDataSource();
	}


}
