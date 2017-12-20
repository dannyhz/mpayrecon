package citic.hz.mpos.kit;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class DruidSqlLogFilter extends Filter{
	
	@Override
	public int decide(LoggingEvent evt) {
		String loggerName = evt.getLoggerName();
		if(loggerName.indexOf("druid.sql") == -1)
			return NEUTRAL;
		String msg = evt.getRenderedMessage();
		if(null == msg)
			return DENY;
		
		//除了时间和参数信息，其他都屏蔽
		if(msg.indexOf("millis.") != -1)
			return NEUTRAL;
		if(msg.indexOf("Parameters :") != -1)
			return NEUTRAL;
		return DENY;
	}

}
