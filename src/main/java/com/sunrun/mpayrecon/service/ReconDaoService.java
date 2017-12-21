package com.sunrun.mpayrecon.service;

import com.sunrun.mpayrecon.dao.inf.ReconDao;

public class ReconDaoService {
	
	private ReconDao reconDao;
	
	public void batchSaveReconSuccessRecords(){
		reconDao.saveReconSuccessRecord(null);
	}
	
	
	public void batchSaveReconFailRecords(){
		reconDao.saveReconFailRecord(null);
	}

}
