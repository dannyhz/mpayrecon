package com.sunrun.bill.holder;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class HolderContainer {
	 private List<CompareDataParamHolder> holderList ;
	 
	 public List<CompareDataParamHolder> getHolderList() {
			return holderList;
		}

		public void setHolderList(List<CompareDataParamHolder> holderList) {
			this.holderList = holderList; 
		}
}
