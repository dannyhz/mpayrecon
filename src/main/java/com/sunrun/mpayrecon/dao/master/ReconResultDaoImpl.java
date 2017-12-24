package com.sunrun.mpayrecon.dao.master;

import java.sql.Connection;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import  org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.sunrun.mpayrecon.model.ReconSuccessRecord; 

@Component
public class ReconResultDaoImpl implements ApplicationContextAware{
	private ApplicationContext applicationContext = null;
	
	public int addReconResultBatch(List<ReconSuccessRecord> reconSuccessRecordList){  
		DefaultSqlSessionFactory sessionFactory = (DefaultSqlSessionFactory)applicationContext.getBean("sqlSessionFactoryRecon");
		try {
			SqlSession session = sessionFactory.openSession();
			Connection conn = session.getConnection();
			System.out.println(conn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		Connection conn = this.getSqlSession().getConnection();
//			System.out.println(conn);	
        //return this.getSqlSession().insert("addReconResultBatch", reconSuccessRecordList);
			return 1;
    }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	} 

}
