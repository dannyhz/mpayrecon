package citic.hz.mpos.kit;

/**
 * ENV 类，用于测试环境代码的调用
 * @author phio
 *
 */
public class Env {
	
	/**
	 * 1=development,2=test,3=production
	 */
	private static int env = 3;

	//private static Config config = Config.getConfig();
	//private static String envStr = config.get("env"); 
//	static{
//		if(null == envStr){}
//		else if(envStr.toLowerCase().startsWith("dev")){
//			env = 1;
//		}else if(envStr.toLowerCase().startsWith("test")){
//			env = 2;
//		}else if(envStr.toLowerCase().startsWith("prod")){
//			env = 3;
//		}
//		needRewrite = Boolean.valueOf(config.get("needRewrite"));
//		needLoadData = Boolean.valueOf(config.get("needLoadData"));
//		noprocedure = Boolean.valueOf(config.get("noprocedure"));
//	}
	
	public static boolean ifProduction(){
		return env == 3;
	}

	public static boolean ifTest(){
		return env == 2;
	}

	public static boolean ifDevelopment(){
		return env == 1;
	}

	public static String getDesc(){
		switch (env) {
		case 1:
			return "DEVELOPMENT"; 
		case 2:
			return "TEST"; 
		case 3:
			return "PRODUCTION"; 
		default:
			return "UNKNOWN"; 
		}
	}
	
	public static boolean needRewrite ;
	public static boolean needLoadData ;
	public static boolean noprocedure ;
}
