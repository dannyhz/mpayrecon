package com.sunrun.mpayrecon.model;

import java.util.List;

public class ReconResult {
	
	List<ReconSuccessRecord> successRecords;
	List<ReconFailRecord> failRecords;
	
	public List<ReconSuccessRecord> getSuccessRecords() {
		return successRecords;
	}
	public void setSuccessRecords(List<ReconSuccessRecord> successRecords) {
		this.successRecords = successRecords;
	}
	public List<ReconFailRecord> getFailRecords() {
		return failRecords;
	}
	public void setFailRecords(List<ReconFailRecord> failRecords) {
		this.failRecords = failRecords;
	}

	
	
}
