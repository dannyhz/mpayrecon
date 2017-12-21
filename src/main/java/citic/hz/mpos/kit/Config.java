package citic.hz.mpos.kit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import citic.hz.phio.kit.AbstractConfig;

public class Config extends AbstractConfig<Properties> {

	protected final static Logger log = Logger.getLogger(new Object(){}.getClass().getEnclosingClass().getName());

	private static Config config = new Config();
	
	private AtomicInteger reloadCnt = new AtomicInteger(0);
	
	public static synchronized Config getConfig(){
		return config;
	}
	
	public Config() {
		
		File cfgfile = null;
		if('/' == File.separatorChar)	//linux
			cfgfile = new File("/etc/_CONFIG/mposBat.conf");
		else{	//windows
			String realPath = "";
			try {
				realPath = new ClassPathResource("mpos/mposBat.conf").getFile().getAbsolutePath();
				System.out.println(new ClassPathResource("mpos/mposBat.conf").getFile().getAbsolutePath());
			} catch (IOException e) {
				log.error("MPayBill 启动 , 加载数据失败", e);
			}
			
			cfgfile = new File(realPath );
		}
		if(!cfgfile.exists()){
			log.error("mposBat config file not found!");
		}
		setCfgfile(cfgfile);
	}
	
	
	@Override
	protected Properties parseCfg(File cfgfile) {
		Properties ps=new Properties();
		try {
			ps.load(new InputStreamReader(new FileInputStream(cfgfile), "utf-8"));
			log.info("mposBat config properties reload:"+reloadCnt.incrementAndGet());
			return ps;
		} catch (IOException e) {
			log.error("mposBat config properties load error:",e);
			//reload失败报警，返回老配置
			//无法报警，报警本身需要读配置，会死循环
			//CommonService.notifyMsg("xytPlatform", "xytBpp配置文件解析失败");
			return cfg;
		}
	}
	
	public String get(String key){
		return getCfg().getProperty(key);
	} 

}
