package com.sunrun.mpayrecon.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sunrun.mpayrecon.dao.master.ReconResultDaoImpl;
import com.sunrun.mpayrecon.dao.slave.IResultDao;
import com.alibaba.druid.pool.DruidDataSource;
import com.sunrun.mpayrecon.dao.master.IReconResultDao;
import com.sunrun.mpayrecon.model.ReconSuccessRecord;

import citic.hz.mpos.service.dao.po.ResultOrder;

public class IReconResultDaoTest {

	//INSERT INTO BAT2_CMP_RESULT( BATID, MY_ORDER_ID,  CKDT, BANK_CODE) VALUES (3, '1001', '2017-12-11', '302331033102');
		@Test
		public void suppose_insert_BAT2_CMP_RESULT_successful(){
		
			 ApplicationContext act = new ClassPathXmlApplicationContext(new String[] { "config/bill-context.xml" });

			 IReconResultDao ireconDao = act.getBean(IReconResultDao.class);
			 
//			 ReconSuccessRecord  record1 =  ireconDao.querySuccessRecord("1001");
//			 System.out.println(record1.getBATID());
			 
			
			 ReconSuccessRecord record = new ReconSuccessRecord();
			 record.setBATID("001");
			// record.setId(106);
			 record.setMY_ORDER_ID("18");
			 record.setCKDT("20171222");
			 record.setBANK_CODE("3030303");
			 ireconDao.insertReconSuccessRecord(record);
			 
			 System.out.println(record.getID());
		}
	
	//INSERT INTO BAT2_CMP_RESULT( BATID, MY_ORDER_ID,  CKDT, BANK_CODE) VALUES (3, '1001', '2017-12-11', '302331033102');
	@Test
	public void suppose_insert_list_successful(){
	
		 ApplicationContext act = new ClassPathXmlApplicationContext(new String[] { "config/bill-context.xml" });

		 IReconResultDao ireconDao = act.getBean(IReconResultDao.class);
		 
		 ReconResultDaoImpl impl = act.getBean(ReconResultDaoImpl.class);

		 impl.addReconResultBatch(null);
		 
		 
		 DruidDataSource ds = (DruidDataSource) act.getBean("mpayReconDataSource");
		 try {
			Connection conn = ds.getConnection().getConnection();
			
			//ds.getConnection()
			if(conn != null){
				System.out.println("raw connect");
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
//		 ReconSuccessRecord  record1 =  ireconDao.querySuccessRecord("1001");
//		 System.out.println(record1.getBATID());
		 
		 List<ReconSuccessRecord> successRecordList = new ArrayList<ReconSuccessRecord>();
		 ReconSuccessRecord record = new ReconSuccessRecord();
//		 record.setBATID("003");
//		 record.setMY_ORDER_ID("7");
//		 record.setCKDT("20171222");
//		 record.setBANK_CODE("3030303");
//		 successRecordList.add(record);
		 record.setBATID("003");
		 record.setMY_ORDER_ID("17");
		 record.setCKDT("20171222");
		 record.setBANK_CODE("3030303");
		 successRecordList.add(record);
		 ireconDao.insertReconSuccessRecordList(successRecordList);
	     
	}
	
	@Test
	public void suppose_get_single_connection_insert_list_successful(){
		 ApplicationContext act = new ClassPathXmlApplicationContext(new String[] { "config/bill-context.xml" });

		 IReconResultDao ireconDao = act.getBean(IReconResultDao.class);
		 
		 ReconResultDaoImpl impl = act.getBean(ReconResultDaoImpl.class);

		 impl.addReconResultBatch(null);
		 
		 
		 DruidDataSource ds = (DruidDataSource) act.getBean("mpayReconDataSource");
		 try {
			Connection conn = ds.getConnection().getConnection();
			
			if(conn != null){
				System.out.println("raw connect");
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void getResult(){
	
		 ApplicationContext act = new ClassPathXmlApplicationContext(new String[] { "config/bill-context.xml" });

		 IResultDao dao = (IResultDao) act.getBean(IResultDao.class);
	     
	     //List<ResultOrder> list =  dao.queryTxn("1");
		 List<ResultOrder> list =  dao.queryTxn("1477859");
	     System.out.println(list.size());
	     
	     System.out.println(((ResultOrder)list.get(0)).getMY_ORDER_ID()); 
	     
	}
}
