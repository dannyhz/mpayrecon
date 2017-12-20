package citic.hz.mpos.test.flow;

import org.junit.Test;

import citic.hz.mpos.kit.ApxLoaderListener;
import citic.hz.mpos.service.dao.MPosBatDao;

public class BfbCmpFlowTest {
	
	
	@Test
	public void testCmpChl(){
		
		ApxLoaderListener apxLoader = new ApxLoaderListener();
		apxLoader.contextInitialized(null);
		
		String result = MPosBatDao.cmpChl("123", "20");
		System.out.println("cmpChl = " + result); 
		
	}
	
	
	@Test
	public void testBankMchTotal(){
		
		ApxLoaderListener apxLoader = new ApxLoaderListener();
		apxLoader.contextInitialized(null);
		
		String result = MPosBatDao.bankMchTotal("11", "20170101", "1");
		System.out.println("bankMchTotal = " + result); 
		
	}

	
	@Test
	public void testFeeClear(){
		
		ApxLoaderListener apxLoader = new ApxLoaderListener();
		apxLoader.contextInitialized(null);
		
		String result = MPosBatDao.feeClear("1111", "20170101", "1");
		System.out.println("feeClear = " + result); 
		
	}
	
	@Test
	public void testProfileClear(){
		
		ApxLoaderListener apxLoader = new ApxLoaderListener();
		apxLoader.contextInitialized(null);
		
		String result = MPosBatDao.profitClear("1111", "20170101");
		System.out.println("profitClear = " + result); 
		
	}
	
	@Test
	public void testFrozen(){
		
		ApxLoaderListener apxLoader = new ApxLoaderListener();
		apxLoader.contextInitialized(null);
		
		String result = MPosBatDao.frozen("1111", "1");
		System.out.println("profitClear = " + result); 
		
	}
}
