package com.sunrun.bill.service.impl;

import org.junit.Test;

public class BillServiceImplTest {
	
	@Test
	public void test_split(){
		String aa = "dfsdfsd222ss";
		String arr[] =  aa.split("s",-1);
		for(String ss : arr){
			System.out.println(arr.length + " - " + ss);
		}
		
	}

}
