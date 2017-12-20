package citic.hz.mpos.kit;

import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;

import citic.hz.mpos.service.SmsNotifyService;
import citic.hz.mpos.service.WxNotifyService;

/**
 * spring beanfactory 初始化listener
 * @author phio
 *
 */
//@WebListener
public class ApxLoaderListener implements ServletContextListener{
	
	protected final static Logger log = Logger.getLogger(new Object(){}.getClass().getEnclosingClass().getName());
	//private static Config config = Config.getConfig();
	
	private static DefaultListableBeanFactory bf = null;
	private static DataSource dataSource = null;
	private static PlatformTransactionManager transactionManager = null;

	@Override
	public void contextInitialized(ServletContextEvent event) {
//		bf = new DefaultListableBeanFactory();
//		bf.setAllowBeanDefinitionOverriding(false);//关闭bean id自动重载
//		XmlBeanDefinitionReader bfr = new XmlBeanDefinitionReader((DefaultListableBeanFactory)bf);
//		int cnt = bfr.loadBeanDefinitions(new ClassPathResource("mpos/beans.xml"));
//		log.info(cnt+" beans loaded");
//		DruidDataSource ds = (DruidDataSource)bf.getBean("dataSource");
//		ds.setUrl(config.get("dbUrl"));
//		ds.setDriverClassName(config.get("driverClassName"));
//		ds.setUsername(config.get("dbUser"));
//		ds.setPassword(config.get("dbPass"));
//		try {
//			ds.init();
//		} catch (SQLException e) {
//			log.error("ds init failed",e);
//		}
//		if(ds.isInited())
//			dataSource = ds;
//		
//		transactionManager = (PlatformTransactionManager)bf.getBean("txManager");
//		
//		//通知服务初始化
//		WxNotifyService.init();
//		SmsNotifyService.init();
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if(null != bf){
			DruidDataSource ds = (DruidDataSource)bf.getBean("dataSource");
			if(null != ds){
				ds.close();
				log.debug("druid ds closed");
			}
		}
	}
	
	public static Object getBean(String beanName) throws BeansException{
		return bf.getBean(beanName);
	}
	
	public static DataSource getDataSource(){
		return dataSource;
	}
	
	public static PlatformTransactionManager getTransactionManager (){
		return transactionManager;
	}


}
