package com.sunrun.mpayrecon.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sunrun.mpayrecon.processor.inf.SplitStrategy;

public class SplitStrategyMachine {
	
	public List<Map<String, String>> generateIteratorList(){
		List<Map<String, String>> queryParamList = new ArrayList<Map<String, String>>();
		
		HashMap<String, String> iter00_01 =	new HashMap<String, String>();
		iter00_01.put("startTime", "000000");
		iter00_01.put("endTime", "005959");
		HashMap<String, String> iter01_02 =	new HashMap<String, String>();
		iter01_02.put("startTime", "010000");
		iter01_02.put("endTime", "015959");
		
		HashMap<String, String> iter02_03 =	new HashMap<String, String>();
		iter02_03.put("startTime", "020000");
		iter02_03.put("endTime", "025959");
		
		HashMap<String, String> iter03_04 =	new HashMap<String, String>();
		iter03_04.put("startTime", "030000");
		iter03_04.put("endTime", "035959");
		
		HashMap<String, String> iter04_05 =	new HashMap<String, String>();
		iter03_04.put("startTime", "040000");
		iter03_04.put("endTime", "045959");
		
		HashMap<String, String> iter05_06 =	new HashMap<String, String>();
		iter03_04.put("startTime", "050000");
		iter03_04.put("endTime", "055959");
		
		HashMap<String, String> iter06_07 =	new HashMap<String, String>();
		iter03_04.put("startTime", "060000");
		iter03_04.put("endTime", "065959");
		
		
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
