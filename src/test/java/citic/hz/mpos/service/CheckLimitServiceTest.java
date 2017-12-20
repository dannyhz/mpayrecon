package citic.hz.mpos.service;

import org.json.simple.JSONObject;
import org.junit.Test;

import citic.hz.mpos.kit.ApxLoaderListener;
import citic.hz.phio.kit.PhioH;

public class CheckLimitServiceTest {
	
	@Test
	public void suppose_insert_successful(){
		
		ApxLoaderListener apxLoader = new ApxLoaderListener();
		apxLoader.contextInitialized(null);
		String nowdt = PhioH.nowDate("yyyyMMdd");
		JSONObject rt = CheckLimitService.checkLimit("MPOS_BAT_LOADCONFIG", nowdt, 1);	
		
		System.out.println(rt);
	}

}
