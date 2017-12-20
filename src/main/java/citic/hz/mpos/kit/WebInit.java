package citic.hz.mpos.kit;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

@WebServlet(urlPatterns = {"/do.init"},loadOnStartup=1)
public class WebInit extends HttpServlet{
	
	protected final static Logger log = Logger.getLogger(new Object(){}.getClass().getEnclosingClass().getName());
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		log.debug("begin system init ...");
		log.debug("ENV = "+Env.getDesc());
		try {
			Globle.LOCAL_HOSTNAME = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			log.warn("hostname not found.",e);
			Globle.LOCAL_HOSTNAME = "UNKNOWN";
		}
		log.debug("HOSTNAME = "+Globle.LOCAL_HOSTNAME);
		
		log.debug("end system init");
	}
	
	
	@Override
	public void destroy() {
		super.destroy();
	}
}
