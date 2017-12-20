package citic.hz.mpos.test;


import java.io.IOException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import citic.hz.mpos.kit.ApxLoaderListener;
import citic.hz.mpos.service.CmpService;
import citic.hz.mpos.service.dao.MPosBatDao;

public class TestLinyq {

	private static ApxLoaderListener apx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		apx = new ApxLoaderListener();
		apx.contextInitialized(null);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		apx.contextDestroyed(null);
	}

	@Test
	public void testRewrite() throws IOException {
		String batId = "20160921091806944257";
		MPosBatDao.rewriteBankMchTotal(batId);
	}
	
	@Test
	public void testbankMchTotal() throws IOException {
		String batid = "20160921091806944258";
		String ckdt = "2017/6/27";
		String chlNo = "05";
		CmpService.bankMchTotal(batid, ckdt, chlNo);
	}
	
}
