package com.sunrun.mpayrecon.model;

import java.util.List;
import java.util.Map;

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

	private List<TxnOrder> oddTxnOrdersHistory;
	private List<ChannelOrder> oddChannelOrdersHistory;
	
	public List<ChannelOrder> getOddChannelOrdersHistory() {
		return oddChannelOrdersHistory;
	}
	public void setOddChannelOrdersHistory(List<ChannelOrder> oddChannelOrdersHistory) {
		this.oddChannelOrdersHistory = oddChannelOrdersHistory;
	}

	public List<TxnOrder> getOddTxnOrdersHistory() {
		return oddTxnOrdersHistory;
	}
	public void setOddTxnOrdersHistory(List<TxnOrder> oddTxnOrdersHistory) {
		this.oddTxnOrdersHistory = oddTxnOrdersHistory;
	}
	
	
}
