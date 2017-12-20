package com.sunrun.util;

public class BillConstants {
	//错账原因
	//银行多记录(渠道查无记录)
	public static String WRONG_FLAG_D = "D"; 
	//渠道多记录(银行查无记录)
	public static String WRONG_FLAG_F = "F";
	//两边金额不一致
	public static String WRONG_FLAG_W = "W";
	//金额一致，对账成功
	public static String SUCESS_FLAG = "S";
	//本地数据库订单状态和对账服务器文件交易状态不一致，错账
	public static String WRONG_FLAG_N = "N";
	

}
