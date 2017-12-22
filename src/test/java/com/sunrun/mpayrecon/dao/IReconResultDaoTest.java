package com.sunrun.mpayrecon.dao;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sunrun.mpayrecon.dao.master.IReconResultDao;
import com.sunrun.mpayrecon.model.ReconSuccessRecord;

public class IReconResultDaoTest {

	//INSERT INTO BAT2_CMP_RESULT( BATID, MY_ORDER_ID,  CKDT, BANK_CODE) VALUES (3, '1001', '2017-12-11', '302331033102');
	@Test
	public void suppose_insert_list_successful(){
	
		 ApplicationContext act = new ClassPathXmlApplicationContext(new String[] { "config/bill-context.xml" });

		 IReconResultDao ireconDao =	act.getBean(IReconResultDao.class);
		 
		 ReconSuccessRecord  record1 =  ireconDao.querySuccessRecord("1001");
		 System.out.println(record1.getBATID());
		 
		 List<ReconSuccessRecord> successRecordList = new ArrayList<ReconSuccessRecord>();
		 ReconSuccessRecord record = new ReconSuccessRecord();
		 record.setBATID("001");
		 record.setNum(106);
		 record.setMY_ORDER_ID("4");
		 record.setCKDT("20171222");
		 record.setBANK_CODE("3030303s");
		 successRecordList.add(record);
		 long i = ireconDao.insertReconSuccessRecordList(successRecordList);
		 System.out.println(i);
		 
	     
	}
}
