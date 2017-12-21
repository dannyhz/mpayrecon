package com.sunrun.mpayrecon.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sunrun.mpayrecon.processor.inf.SplitStrategy;

public class SplitStrategyMachine {
	
	public List<Map<String, String>> generateIteratorList(){
		List<Map<String, String>> queryParamList = new ArrayList<Map<String, String>>();
		HashMap<String, String> iter11_12 =	new HashMap<String, String>();
		
		iter11_12.put("startTime", "110000");
		iter11_12.put("endTime", "115959");
		HashMap<String, String> iter12_13 =	new HashMap<String, String>();
		iter11_12.put("startTime", "120000");
		iter11_12.put("endTime", "125959");
		queryParamList.add(iter11_12);
		queryParamList.add(iter12_13);
				
		return queryParamList;
	} 

	public List<Map<String, String>> generateIteratorList(SplitStrategy strategy){
		return new ArrayList<Map<String, String>>();
	} 
	
}
