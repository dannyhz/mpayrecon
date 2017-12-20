package citic.hz.mpos.test.flow;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import citic.hz.mpos.flow.QQCmpFlow;
import citic.hz.mpos.kit.ApxLoaderListener;

public class QQCmpFlowTest {
	
	@Test
	public void suppose_compare_with_bill() throws ServletException, IOException{
		
		ApxLoaderListener apxLoader = new ApxLoaderListener();
		apxLoader.contextInitialized(null);
		  QQCmpFlow flow = new QQCmpFlow();
		  JSONObject json = new JSONObject();
	      json.put("trdt", "20171106");
	      json.put("fileType", "1");
		  
	      HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
	      HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
	      
		  flow.cmpStd(request, response, json);
		
	}
	
	

}
