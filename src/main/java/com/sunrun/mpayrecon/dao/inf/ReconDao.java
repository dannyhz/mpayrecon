package com.sunrun.mpayrecon.dao.inf;

import java.util.List;

import com.sunrun.mpayrecon.model.ReconFailRecord;
import com.sunrun.mpayrecon.model.ReconSuccessRecord;

public interface ReconDao {
	
	public void saveReconSuccessRecord(List<ReconSuccessRecord> successRecords);
	public void saveReconFailRecord(List<ReconFailRecord> failRecords);

}
