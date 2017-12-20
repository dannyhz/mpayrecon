package com.sunrun.compare;

import java.util.HashMap;

import org.junit.Test;

import citic.hz.mpos.kit.BeTimer;

public class CompareInMemory {
	
	

	@Test
	public void putInInfoIntoMap(){
		
		HashMap ordersInMySystem = new HashMap();
		
		
		BeTimer tm = new BeTimer();
		tm.reset();
		for(int i=0, seq = 0, amt = 100 ; i<1000000; i++){
			String type = "01";
			ordersInMySystem.put( seq++ + "_" + amt + "_" + type, seq++ + "_" + amt + "_" + type);
		}
		
		System.out.println("put to map : takes " + tm.stop() + " ms,");
		
		
		tm.reset();
		
		Object value = ordersInMySystem.get("0_100_01");
		
		System.out.println(" get value : " + value + ", takes "+ tm.stop() + " ms.");
		
		
		
	}

}
