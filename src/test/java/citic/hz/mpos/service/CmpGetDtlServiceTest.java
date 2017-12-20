package citic.hz.mpos.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.object.BatchSqlUpdate;

import citic.hz.mpos.kit.ApxLoaderListener;
import citic.hz.mpos.service.dao.MPosBatDao;

public class CmpGetDtlServiceTest {
	
	private static ApxLoaderListener apx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		apx = new ApxLoaderListener();
		apx.contextInitialized(null);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		apx.contextDestroyed(null);
	}
	
	@Test
	public void suppose_insert_excel_data_to_qq_channel() throws IOException{
		
//		String sqlstr = "insert into MPOS.BAT2_CMP_CHL_DTL(TRTM,CHANNEL_NO, CHL_ORDER_ID, MY_ORDER_ID,"
//				+ " MCH_NO, SEC_MCH_NO, TRAM, TRTP,REL_ORDER_ID, MEMO,CLDT,BNO,PAYBANK) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		//yyyyMMdd
		BatchSqlUpdate bsu = MPosBatDao.batWxTrans();	//借用微信的SQL
		
		String batno = "1";
		String trdt = "20171129"; 
		String mchNo = "1489862431";
		
		File f = new File("C:/danny/工作任务/20171127_MPayBill对账例子/1.qq/20171101-1489862431.csv");
		//根据QQ文件计算的应收QQ的交易额，注意：QQ应收是扣除手续费后的
		BigDecimal recvable = new BigDecimal(0);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
		String ln = null;
		int i = 0;
		while((ln = br.readLine()) != null){
			//处理每一行
			if(!ln.startsWith("`"))
				continue;
			String [] fs = ln.split("^`|,`|,\"`|\",`",-1);
			if(fs.length < 30+1){
				if(fs.length == 9+1){	//文件尾
					continue;
				}else{
					throw new RuntimeException("QQ文件处理失败");
				}	
			}
			String trtm = fs[1];
			String chlNo = "30";	//TPAM_TYPE	30=QQ
			String chlOrdId = fs[10];
			String myOrdId = fs[9];
			String mchNo2 = fs[2];	//文件内容中的商户号
			String secMchNo = fs[4];
			String tram = fs[15];	//取了商户应收金额(元)，未取订单金额(元)
			String trtp = "01";	//01-正常 04-退款
			String trst = fs[18];
			String relOrdId = "";
			if("转入退款".equals(trst)){
				trtp = "04";
				tram = "-"+fs[22];
				relOrdId = myOrdId;
				chlOrdId = fs[21];
				myOrdId = fs[20];
			}
			String memo = fs[27];
			String cldt = trtm.substring(0,10);	//YYYY-MM-DD
			String paybk = fs[11];	//对账单中支付银行是中文的，暂时没有用处
			System.out.println(Arrays.asList(trtm,chlNo,chlOrdId,myOrdId,secMchNo,tram,trtp,relOrdId,memo,cldt,paybk,mchNo2));
			
			//汇总计算应收金额
			String costRateStr = fs[30].trim();
			BigDecimal costRate = null;
			if(costRateStr.endsWith("%"))	//TODO 需确定格式
				costRate = new BigDecimal(costRateStr.replaceAll("%", "")).movePointLeft(2);
			else
				costRate = new BigDecimal(costRateStr);
			BigDecimal tramt = new BigDecimal(tram);
			recvable = recvable.add(tramt.subtract(tramt.multiply(costRate).setScale(2, RoundingMode.HALF_UP)));
			
			bsu.update(trtm,chlNo,chlOrdId,myOrdId,mchNo2,secMchNo,tram,trtp,relOrdId,memo,cldt,batno,paybk);
			
		}
		br.close();
		bsu.flush();
	}
	
	@Test
	public void insert_to_my_orders_from_excel() throws FileNotFoundException, IOException, SQLException{
		
		String filePath = "C:/danny/code_base/20171031_mpos_bill/MPayBill/src/test/resources/order";
		
		XSSFWorkbook workbook1 = new XSSFWorkbook(new FileInputStream(new File(filePath + File.separator + "qq_order.xlsx")));
		XSSFSheet sheet = workbook1.getSheet("Sheet1");
		
		String insertIntoMposDtl = "insert into BAT2_CMP_MPOS_DTL" 
				+ "(TRTM,CHANNEL_NO,CHL_ORDER_ID,MY_ORDER_ID,MCH_NO,SEC_MCH_NO,TRAM,TRTP,REL_ORDER_ID,MEMO,BRH_ID,MY_MCH_NO,MY_SEC_MCH_NO,TERM_NO,CLDT,"
				+ " BNO,TRADE_TYPE,MCH_ORDER_ID,FZFG,MY_MCH_RATE,MY_SEC_MCH_RATE,D0FG,TRADE_CODE,PAYBANK, BANK_CODE) "
				+ "values(?,?,?,?,?,?,?,?,?,?,"
					   + "?,?,?,?,?,?,?,?,?,?,"
					   + "?,?,?,?,?)";
				
		Connection conn = MPosDataSource.getInstance().getConnection();
		
		int  rowMax = 1000, columnMax = 25;
		
		PreparedStatement ps = conn.prepareStatement(insertIntoMposDtl);
		
		for(int rowIdx = 1; rowIdx < rowMax; rowIdx++){
			
			String TRTM = "",CHANNEL_NO = "",CHL_ORDER_ID="",MY_ORDER_ID="",MCH_NO="";
			String SEC_MCH_NO = "",TRAM = "",TRTP = "",REL_ORDER_ID="",MEMO = "",BRH_ID = "";
			String MY_MCH_NO = "",MY_SEC_MCH_NO = "",TERM_NO = "",CLDT = "", BNO = "",TRADE_TYPE = "",MCH_ORDER_ID = "";
			String FZFG = "",MY_MCH_RATE = "",MY_SEC_MCH_RATE = "",D0FG = "",TRADE_CODE = "",PAYBANK = "", BANK_CODE = "";
			
			System.out.println("current row number " + rowIdx);
			
			if(sheet.getRow(rowIdx) == null){
				System.out.println("文件结束， 共 "  + (rowIdx - 1 )+  "条记录");
				break;
			}
			
			XSSFRow row = (XSSFRow) sheet.getRow(rowIdx);
			
			if(isRowEmpty(row)){
				System.out.println("文件结束， 共 "  + (rowIdx - 1 ) +  "条记录");
				break;
			}
			
			for(int columnIdx = 0; columnIdx < columnMax; columnIdx++){
				XSSFCell cell = row.getCell(columnIdx);
				
				if(columnIdx == 0){
					TRTM = getCellValue(cell);
				}else if(columnIdx == 1){
					CHANNEL_NO = getCellValue(cell);
				}else if(columnIdx == 2){
					CHL_ORDER_ID = getCellValue(cell);
				}else if(columnIdx == 3){
					MY_ORDER_ID = getCellValue(cell);
				}else if(columnIdx == 4){
					MCH_NO = getCellValue(cell);
				}else if(columnIdx == 5){
					SEC_MCH_NO = getCellValue(cell);
				}
				else if(columnIdx == 6){
					TRAM = getCellValue(cell);
				}
				else if(columnIdx == 7){
					TRTP = getCellValue(cell);
				}
				else if(columnIdx == 8){
					REL_ORDER_ID = getCellValue(cell);
				}else if(columnIdx == 9){
					MEMO = getCellValue(cell);
				}else if(columnIdx == 10){
					BRH_ID = getCellValue(cell);
				}else if(columnIdx == 11){
					MY_MCH_NO = getCellValue(cell);
				}else if(columnIdx == 12){
					MY_SEC_MCH_NO = getCellValue(cell);
				}else if(columnIdx == 13){
					TERM_NO = getCellValue(cell);
				}else if(columnIdx == 14){
					CLDT = getCellValue(cell);
				}else if(columnIdx == 15){
					BNO = getCellValue(cell);
				}else if(columnIdx == 16){
					TRADE_TYPE = getCellValue(cell);
				}else if(columnIdx == 17){
					MCH_ORDER_ID = getCellValue(cell);
				}else if(columnIdx == 18){
					FZFG = getCellValue(cell);
				}else if(columnIdx == 19){
					MY_MCH_RATE = getCellValue(cell);
				}else if(columnIdx == 20){
					MY_SEC_MCH_RATE = getCellValue(cell);
				}else if(columnIdx == 21){
					D0FG = getCellValue(cell);
				}else if(columnIdx == 22){
					TRADE_CODE = getCellValue(cell);
				}else if(columnIdx == 23){
					PAYBANK = getCellValue(cell);
				}else if(columnIdx == 24){
					BANK_CODE = getCellValue(cell);
				}
				
				if(columnIdx >= columnMax){
					if (cell != null && cell.getCellType() != XSSFCell.CELL_TYPE_BLANK){
						
						System.out.println("value=[" + cell.getStringCellValue()+"]");
					}else{
						System.out.println("value is empty.");
					}
					break;
				}
				
			}
			
			ps.setString(1, TRTM);
			ps.setString(2, CHANNEL_NO);
			ps.setString(3, CHL_ORDER_ID);
			ps.setString(4, MY_ORDER_ID);
			ps.setString(5, MCH_NO);
			ps.setString(6, SEC_MCH_NO);
			
			if(TRAM == null ||  TRAM.length() ==0){
				ps.setNull(7, Types.DOUBLE);
			}else{
				ps.setDouble(7, TRAM == null ||  TRAM.length() ==0? null : Double.parseDouble(TRAM));	
			}
			
			ps.setString(8, TRTP);
			ps.setString(9, REL_ORDER_ID);
			ps.setString(10, MEMO);
			ps.setString(11, BRH_ID);
			ps.setString(12, MY_MCH_NO);
			ps.setString(13, MY_SEC_MCH_NO);
			ps.setString(14, TERM_NO);
			ps.setString(15, CLDT);
			ps.setString(16, BNO);
			ps.setString(17, TRADE_TYPE);
			ps.setString(18, MCH_ORDER_ID);
			ps.setString(19, FZFG);
			
			if(MY_MCH_RATE == null ||  MY_MCH_RATE.length() ==0){
				ps.setNull(20, Types.DOUBLE);
			}else{
				ps.setDouble(20, MY_MCH_RATE == null ||  MY_MCH_RATE.length() ==0? null : Double.parseDouble(MY_MCH_RATE));	
			}
			if(MY_SEC_MCH_RATE == null ||  MY_SEC_MCH_RATE.length() ==0){
				ps.setNull(21, Types.DOUBLE);
			}else{
				ps.setDouble(21, MY_SEC_MCH_RATE == null ||  MY_SEC_MCH_RATE.length() ==0? null : Double.parseDouble(MY_SEC_MCH_RATE));	
			}
			ps.setString(22, D0FG);
			ps.setString(23, TRADE_CODE);
			ps.setString(24, PAYBANK);
			ps.setString(25, BANK_CODE);
			int rslt = ps.executeUpdate();
			
		}
		
		
	}

	
	public static boolean isRowEmpty(XSSFRow row) {
	    for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
	    	XSSFCell cell = row.getCell(c);
	        if (cell != null && cell.getCellType() != XSSFCell.CELL_TYPE_BLANK)
	            return false;
	    }
	    return true;
	}
	
	/** 
     * 根据excel单元格类型获取excel单元格值 
     * @param cell 
     * @return 
     */  
    private static String getCellValue(Cell cell) {  
        String cellvalue = "";  
        if (cell != null) {  
            // 判断当前Cell的Type  
            switch (cell.getCellType()) {  
            // 如果当前Cell的Type为NUMERIC  
            case HSSFCell.CELL_TYPE_NUMERIC: {  
                short format = cell.getCellStyle().getDataFormat();  
                if(format == 14 || format == 31 || format == 57 || format == 58){   //excel中的时间格式  
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");    
                    double value = cell.getNumericCellValue();    
                    Date date = DateUtil.getJavaDate(value);    
                    cellvalue = sdf.format(date);    
                }  
                // 判断当前的cell是否为Date  
                else if (HSSFDateUtil.isCellDateFormatted(cell)) {  //先注释日期类型的转换，在实际测试中发现HSSFDateUtil.isCellDateFormatted(cell)只识别2014/02/02这种格式。  
                    // 如果是Date类型则，取得该Cell的Date值           // 对2014-02-02格式识别不出是日期格式  
                    Date date = cell.getDateCellValue();  
                    DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");  
                    cellvalue= formater.format(date);  
                } else { // 如果是纯数字  
                    // 取得当前Cell的数值  
                    cellvalue = NumberToTextConverter.toText(cell.getNumericCellValue());   
                      
                }  
                break;  
            }  
            // 如果当前Cell的Type为STRIN  
            case HSSFCell.CELL_TYPE_STRING:  
                // 取得当前的Cell字符串  
                cellvalue = cell.getStringCellValue().replaceAll("'", "''");  
                break;  
            case  HSSFCell.CELL_TYPE_BLANK:  
                cellvalue = null;  
                break;  
            // 默认的Cell值  
            default:{  
                cellvalue = " ";  
            }  
            }  
        } else {  
            cellvalue = "";  
        }  
        return cellvalue;  
    }
}
