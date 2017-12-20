package citic.hz.mpos.dao;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import citic.hz.mpos.kit.ApxLoaderListener;
import citic.hz.mpos.service.dao.MPayBillDao;
import citic.hz.mpos.service.dao.po.ChannelOrder;
import citic.hz.mpos.service.dao.po.MyOrder;
import citic.hz.phio.kit.PhioH;

public class MPayBillDaoTest {
	
	@Test
	public void query_channel_order_successful(){
		
		ApxLoaderListener apxLoader = new ApxLoaderListener();
		apxLoader.contextInitialized(null);
		
		List rslt = new MPayBillDao().getChannelOrders("30");
		
		System.out.println(rslt.size() );
		
		System.out.println(((ChannelOrder)rslt.get(0)).getCHANNEL_NO());
		
	}
	
	
	@Test
	public void query_myorder_successful(){
		
		ApxLoaderListener apxLoader = new ApxLoaderListener();
		apxLoader.contextInitialized(null);
		
		List rslt = new MPayBillDao().getMyOrders("30");
		
		System.out.println(rslt.size() );
		
		System.out.println(((MyOrder)rslt.get(0)).getCHANNEL_NO());
		
	}
	
	 private static long seed = new Date().getTime();
	  
    public static synchronized String newKey() { String base = "1095595987701";
      long id = seed++ - Long.parseLong(base);
      return String.valueOf(id);
    }
	
	@Test
	public void get_batno(){
		String batno = newKey();
		
		System.out.println(batno);
	}
}
