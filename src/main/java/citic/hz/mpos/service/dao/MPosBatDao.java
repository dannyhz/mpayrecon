package citic.hz.mpos.service.dao;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.object.BatchSqlUpdate;

import citic.hz.mpos.kit.CmdSp;
import citic.hz.mpos.kit.XytHelper;
import citic.hz.mpos.service.MPosDataSource;
import citic.hz.phio.kit.PhioH;

public class MPosBatDao {
	
	private static final Logger log = Logger.getLogger(MPosBatDao.class);
	
	private static JdbcTemplate jt = new JdbcTemplate(MPosDataSource.getInstance()); 
	
	/**
	 * 查询微信主商户号
	 * @return
	 */
	public static List<Map<String, Object>> lstWxMchs(){
		String sqlstr = "select MAIN_MCHT_NO from MPOS.IMP_MAIN_MCHT_INFO where MAIN_CHANNEL_NO='00'";
		return jt.queryForList(sqlstr);
	}
	
	/**
	 * 查询支付宝主商户号
	 * @return
	 */
	public static List<Map<String, Object>> lstZfbMchs(){
		String sqlstr = "select MAIN_MCHT_NO from MPOS.IMP_MAIN_MCHT_INFO where MAIN_CHANNEL_NO='10'";
		return jt.queryForList(sqlstr);
	}

	/**
	 * 查询百付宝主商户号
	 * @return
	 */
	public static List<Map<String, Object>> lstBfbMchs(){
		String sqlstr = "select MAIN_MCHT_NO from MPOS.IMP_MAIN_MCHT_INFO where MAIN_CHANNEL_NO='20'";
		return jt.queryForList(sqlstr);
	}

	/**
	 * 查询QQ主商户号
	 * @return
	 */
	public static List<Map<String, Object>> lstQQMchs(){
		String sqlstr = "select MAIN_MCHT_NO from MPOS.IMP_MAIN_MCHT_INFO where MAIN_CHANNEL_NO='30'";
		return jt.queryForList(sqlstr);
	}

	/**
	 * 查询京东主商户号
	 * @return
	 */
	public static List<Map<String, Object>> lstJdMchs(){
		String sqlstr = "select MAIN_MCHT_NO from MPOS.IMP_MAIN_MCHT_INFO where MAIN_CHANNEL_NO='40'";
		return jt.queryForList(sqlstr);
	}

	/**
	 * 批量插入渠道对账表
	 * @return
	 */
	public static BatchSqlUpdate batWxTrans(){
		String sqlstr = "insert into MPOS.BAT2_CMP_CHL_DTL(TRTM,CHANNEL_NO, CHL_ORDER_ID, MY_ORDER_ID, MCH_NO, SEC_MCH_NO, TRAM, TRTP,REL_ORDER_ID, MEMO,CLDT,BNO,PAYBANK) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		int[] ps=new int[13];
		for(int i=0;i<ps.length;i++){
			ps[i] = Types.VARCHAR;
		}
		BatchSqlUpdate bsu = new BatchSqlUpdate(MPosDataSource.getInstance(), sqlstr, ps);
		bsu.setBatchSize(7000);
		return bsu;
	}
	
	/**
	 * 导入上日MPOS订单数据
	 * @param trdt
	 */
	public static int loadMposDtl(String trdt,String bno){
		String loadCmd = "load from (select TXN_DT||' '||TXN_TM,TPAM_TYPE,TPAM_TXN_ID,TXN_SEQ_ID,TPAM_MCHT_NO,TPAM_SEC_MCHT_NO,"
				+ "	case when TXN_TYPE='01' then cast(TXN_AMT as dec(17,2))/100 else cast(TXN_AMT as dec(17,2))/100*(-1) end,"	//消费金额为正，退款和撤销为负
				+ " TXN_TYPE,ORIG_TXN_SEQ_ID,ORDER_BODY,BRH_ID,MER_ID,MER_SEC_ID,TERM_ID,TPAM_TXN_DT,'"+bno+"',TRADE_TYPE,TXN_ORDER_ID,"
				+ " case when FEN_ZHANG_FLAG='Y' then 'Y' when FEN_ZHANG_FLAG='D' then 'D' else 'N' end,FEE_RATE,FEE_RATE_FZZSH,case when FLAG_D0='0' then FLAG_SUCCESS_D0 else 'X' end,"
				+ " TXN_SUB_TYPE,TPAM_BANK_ID, BANK_CODE from mpos.NK_ONL_ORDER_TXN_INFO_HIS "	//从历史表取
				+ " where TPAM_TXN_DT='"+trdt+"' "
				+ " and TPAM_TYPE in ('05','10','20','30','40') "	//微信 、支付宝二清、百付宝、手Q、京东
				+ " and TXN_TYPE<>'00' " //查询
				+ " and TXN_STATE in ('00','01','10','08') for read only)" //正常、退款、撤销	为了性能，只取成功记录，未对上的结果要考虑有状态不一样的情况
				+ " of cursor messages on server insert into BAT2_CMP_MPOS_DTL"
				+ "(TRTM,CHANNEL_NO,CHL_ORDER_ID,MY_ORDER_ID,MCH_NO,SEC_MCH_NO,TRAM,TRTP,REL_ORDER_ID,MEMO,BRH_ID,MY_MCH_NO,MY_SEC_MCH_NO,TERM_NO,CLDT,"
				+ " BNO,TRADE_TYPE,MCH_ORDER_ID,FZFG,MY_MCH_RATE,MY_SEC_MCH_RATE,D0FG,TRADE_CODE,PAYBANK, BANK_CODE) nonrecoverable";
		CmdSp cmdSp =new CmdSp(MPosDataSource.getInstance());
		Map<String, Object> rt = cmdSp.db2load(loadCmd);
		JSONObject jort = new JSONObject();
		jort.putAll(rt);
		int sqlcode = jort.getIntLike("SQLCODE");
		if(sqlcode != 0){
			log.error("导入MPOS流水失败:"+jort.toString());
			throw new RuntimeException("导入MPOS流水失败");
		}else{
			try{
				int cnt = jort.getIntLike("ROWS_LOADED");
				return cnt;
			}catch(Throwable e){
				log.error("获取ROWS_LOADED失败",e);
				log.error(jort.toString());
				return 0;
			}
		}
	}

	
	/**
	 * 导入上日MPOS订单数据（非成功的数据，包含疑似掉单的数据）
	 * @param trdt
	 */
	public static int loadMposDtlFail(String trdt,String bno){
		String loadCmd = "load from (select TXN_DT||' '||TXN_TM,TPAM_TYPE,TPAM_TXN_ID,TXN_SEQ_ID,TPAM_MCHT_NO,TPAM_SEC_MCHT_NO,"
				+ "	case when TXN_TYPE='01' then cast(TXN_AMT as dec(17,2))/100 else cast(TXN_AMT as dec(17,2))/100*(-1) end,"	//消费金额为正，退款和撤销为负
				+ " TXN_TYPE,ORIG_TXN_SEQ_ID,ORDER_BODY,BRH_ID,MER_ID,MER_SEC_ID,TERM_ID,TXN_DT,'"+bno+"',TRADE_TYPE,TXN_ORDER_ID,TXN_STATE,"
				+ " case when FEN_ZHANG_FLAG='Y' then 'Y' when FEN_ZHANG_FLAG='D' then 'D' else 'N' end,FEE_RATE,FEE_RATE_FZZSH,case when FLAG_D0='0' then FLAG_SUCCESS_D0 else 'X' end,"
				+ " TXN_SUB_TYPE,TPAM_BANK_ID, BANK_CODE from mpos.NK_ONL_ORDER_TXN_INFO_HIS "	//从历史表取
				+ " where TXN_DT='"+trdt+"' "	//要使用我方交易日期
				+ " and TPAM_TYPE in ('05','10','20','30','40') "	//微信 、支付宝二清、百付宝、手Q、京东
				+ " and TXN_TYPE<>'00' " //查询
				+ " and TXN_STATE in ('04','06','05','09','99','13') for read only)"	//13-未知 
				+ " of cursor messages on server replace into BAT2_CMP_MPOS_DTL_FAIL" //不用所有日期的数据，使用replace
				+ "(TRTM,CHANNEL_NO,CHL_ORDER_ID,MY_ORDER_ID,MCH_NO,SEC_MCH_NO,TRAM,TRTP,REL_ORDER_ID,MEMO,BRH_ID,MY_MCH_NO,MY_SEC_MCH_NO,TERM_NO,CLDT,"
				+ " BNO,TRADE_TYPE,MCH_ORDER_ID,TXN_STATE,FZFG,MY_MCH_RATE,MY_SEC_MCH_RATE,D0FG,TRADE_CODE,PAYBANK, BANK_CODE) nonrecoverable";
		CmdSp cmdSp =new CmdSp(MPosDataSource.getInstance());
		Map<String, Object> rt = cmdSp.db2load(loadCmd);
		JSONObject jort = new JSONObject();
		jort.putAll(rt);
		int sqlcode = jort.getIntLike("SQLCODE");
		if(sqlcode != 0){
			log.error("导入MPOS非成功流水失败:"+jort.toString());
			try{
				loadReset("MPOS.BAT2_CMP_MPOS_DTL_FAIL");
				log.info("try reset load pending: Ok");
			}catch(Throwable e){
				log.error("try reset load pending: Failed",e);
			}	
			throw new RuntimeException("导入MPOS非成功流水失败");
		}else{
			try{
				int cnt = jort.getIntLike("ROWS_LOADED");
				return cnt;
			}catch(Throwable e){
				log.error("获取ROWS_LOADED失败",e);
				log.error(jort.toString());
				return 0;
			}
		}
	}
	
	
	/**
	 * 以replace方式 load mpos的表
	 * @param nickname
	 * @param localTabName
	 */
	public static int loadMposTableReplace(String nickname,String localTabName){
		String loadCmd = "load from (select * from "+nickname+" for read only)" 
				+ " of cursor messages on server replace into "+localTabName+" nonrecoverable";
		CmdSp cmdSp =new CmdSp(MPosDataSource.getInstance());
		Map<String, Object> rt = cmdSp.db2load(loadCmd);
		JSONObject jort = new JSONObject();
		jort.putAll(rt);
		int sqlcode = jort.getIntLike("SQLCODE");
		if(sqlcode != 0){
			log.error("LOAD失败["+localTabName+"]:"+jort.toString());
			throw new RuntimeException("LOAD失败:" + sqlcode);
		}else{
			try{
				int cnt = jort.getIntLike("ROWS_LOADED");
				return cnt;
			}catch(Throwable e){
				log.error("获取ROWS_LOADED失败",e);
				log.error(jort.toString());
				return 0;
			}
		}
	}

	/**
	 * mpos和渠道（微信、支付宝等）对账
	 * @return
	 */
	public static String cmpChl(final String batid,final String chlNo){
		String sqlstr = "{call MPOS.SP_CHL_CMP(?,?,?)}";
		return jt.execute(sqlstr, new CallableStatementCallback<String>() {
			public String doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				cs.setString(1, chlNo);
				cs.setString(2, batid);
				cs.registerOutParameter(3, Types.VARCHAR);
				cs.execute();
				return cs.getString(3);
			}
		});
	}

	/**
	 * 按对账批次对对账成功的交易进行手续费计算
	 * @param batid 为0时系统自动计算最大的批次号
	 * @return
	 */
	public static String feeClear(final String batid,final String ckdt,final String chlNo){
		String sqlstr = "{call MPOS.SP_FEE_CLEAR(?,?,?,?)}";
		return jt.execute(sqlstr, new CallableStatementCallback<String>() {
			public String doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				cs.setString(1, batid);
				cs.setString(2, ckdt);
				cs.setString(3, chlNo);
				cs.registerOutParameter(4, Types.VARCHAR);
				cs.execute();
				return cs.getString(4);
			}
		});
	}

	/**
	 * 按对账批次对对账成功的交易进行交易汇总计算_多机构
	 * @param batid 对账批次号，为0时系统自动计算最大的批次号
	 * @param ckdt 对账日期
	 * @param chlNo 渠道号
	 * @return errmsg
	 * @author LINYQ 
	 * @date 20170425
	 */
	public static String bankMchTotal(final String batid,final String ckdt,final String chlNo) {
		String sqlstr = "{CALL MPOS.SP_BANK_MCH_TOTAL(?,?,?,?)}";
		return jt.execute(sqlstr, new CallableStatementCallback<String>() {
			public String doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				cs.setString(1, batid);
				cs.setString(2, ckdt);
				cs.setString(3, chlNo);
				cs.registerOutParameter(4, Types.VARCHAR);
				cs.execute();
				return cs.getString(4);
			}
		});
	}
	
	/**
	 * 回写BAT2_CMP_RESULT
	 * @param ckdt
	 * @param batid
	 */
	public static void rewriteCmpResult(String ckdt,String batid){
		String sqlstr = "select ID,SYSTM, BATID, CLDT, SRC, TRTM, CHANNEL_NO, CHL_ORDER_ID, MY_ORDER_ID, MCH_NO, SEC_MCH_NO, TRAM, TRTP, REL_ORDER_ID, MEMO, BRH_ID, MY_MCH_NO, MY_SEC_MCH_NO, TERM_NO, COST_RATE, TOTAL_RATE, COST, TOTAL_FEE, RZAMT, CKTURN, CKFG, CKDT,TRADE_TYPE,MCH_ORDER_ID,REL_MCH_ORDER_ID,"
				+ " PAYBANK,FZFG,MY_PTMCH_RATE,MY_PTMCH_FEE,D0FG,TRADE_CODE, BANK_CODE "
				+ "	from MPOS.BAT2_CMP_RESULT where CKDT=? and BATID=? for read only ";
		String sqlstr2 = "insert into NK_BAT2_CMP_RESULT(ID,SYSTM, BATID, CLDT, SRC, TRTM, CHANNEL_NO, CHL_ORDER_ID, MY_ORDER_ID, MCH_NO, SEC_MCH_NO, TRAM, TRTP, REL_ORDER_ID, MEMO, BRH_ID, MY_MCH_NO, MY_SEC_MCH_NO, TERM_NO, COST_RATE, TOTAL_RATE, COST, TOTAL_FEE, RZAMT, CKTURN, CKFG, CKDT,TRADE_TYPE,MCH_ORDER_ID,REL_MCH_ORDER_ID,"
				+ " PAYBANK,FZFG,MY_PTMCH_RATE,MY_PTMCH_FEE,D0FG,TRADE_CODE, BANK_CODE) "
				+ "	values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		int[] ps=new int[37];
		for(int i=0;i<ps.length;i++){
			ps[i] = Types.VARCHAR;
		}
		final BatchSqlUpdate bsu = new BatchSqlUpdate(MPosDataSource.getInstance(), sqlstr2, ps);
		bsu.setBatchSize(10000);
		jt.query(sqlstr, new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				bsu.update(
					rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),
					rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),
					rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15),
					rs.getString(16),rs.getString(17),rs.getString(18),rs.getString(19),rs.getString(20),
					rs.getString(21),rs.getString(22),rs.getString(23),rs.getString(24),rs.getString(25),
					rs.getString(26),rs.getString(27),rs.getString(28),rs.getString(29),rs.getString(30),
					rs.getString(31),rs.getString(32),rs.getString(33),rs.getString(34),rs.getString(35),
					rs.getString(36),rs.getString(37)
				);
			}
		},ckdt,batid);
		bsu.flush();
	}

	
	/**
	 * 回写BAT2_CMP_RESULT_FAIL
	 * @param ckdt
	 * @param batid
	 */
	public static void rewriteCmpResultFail(String ckdt,String batid){
		String sqlstr = "select ID,SYSTM, BATID, CLDT, SRC, TRTM, CHANNEL_NO, CHL_ORDER_ID, MY_ORDER_ID, MCH_NO, SEC_MCH_NO, TRAM, TRTP, REL_ORDER_ID, MEMO, BRH_ID, MY_MCH_NO, MY_SEC_MCH_NO, TERM_NO, CKTURN, CKFG, CKDT,TRADE_TYPE,MCH_ORDER_ID,REL_MCH_ORDER_ID,CKSC_BATID, PAYBANK, BANK_CODE "
				+ "	from MPOS.BAT2_CMP_RESULT_FAIL where CKDT=? and BATID=? for read only ";
		String sqlstr2 = "insert into NK_BAT2_CMP_RESULT_FAIL(ID,SYSTM, BATID, CLDT, SRC, TRTM, CHANNEL_NO, CHL_ORDER_ID, MY_ORDER_ID, MCH_NO, SEC_MCH_NO, TRAM, TRTP, REL_ORDER_ID, MEMO, BRH_ID, MY_MCH_NO, MY_SEC_MCH_NO, TERM_NO, CKTURN, CKFG, CKDT,TRADE_TYPE,MCH_ORDER_ID,REL_MCH_ORDER_ID,CKSC_BATID, PAYBANK, BANK_CODE) "
				+ "	values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		int[] ps = new int[28];
		for(int i=0;i<ps.length;i++){
			ps[i] = Types.VARCHAR;
		}
		final BatchSqlUpdate bsu = new BatchSqlUpdate(MPosDataSource.getInstance(), sqlstr2, ps);
		bsu.setBatchSize(10000);
		jt.query(sqlstr, new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				bsu.update(
					rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),
					rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),
					rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15),
					rs.getString(16),rs.getString(17),rs.getString(18),rs.getString(19),rs.getString(20),
					rs.getString(21),rs.getString(22),rs.getString(23),rs.getString(24),rs.getString(25),
					rs.getString(26),rs.getString(27),rs.getString(28)
				);
			}
		},ckdt,batid);
		bsu.flush();
	}

	
	/**
	 * 回写BAT2_MCH_FEE_CLEAR
	 * @param ckdt
	 * @param batid
	 */
	public static void rewriteFeeClear(String batid){
		String sqlstr = "select ID, SYSTM, BATID, SMDT, MY_MCH_NO,MY_SEC_MCH_NO, CHANNEL_NO, TCNT, TTRAM, ACNT, ATRAM, RCNT, RTRAM, COST, TOTAL_FEE, RZAMT, RZST, RZDT, RZFN, MCH_NO, SEC_MCH_NM, SETTLE_BANK_FLAG, SETTLE_ACCT, SETTLE_ACCT_NM, SETTLE_BANK_ALL_NAME, SETTLE_BANK_CODE,SETTLE_CYCLE,RZTP "
				+ "	from MPOS.BAT2_MCH_FEE_CLEAR where BATID=? for read only ";
		String sqlstr2 = "insert into NK_BAT2_MCH_FEE_CLEAR(ID, SYSTM, BATID, SMDT,MY_MCH_NO, MY_SEC_MCH_NO, CHANNEL_NO, TCNT, TTRAM, ACNT, ATRAM, RCNT, RTRAM, COST, TOTAL_FEE, RZAMT, RZST, RZDT, RZFN, MCH_NO, SEC_MCH_NM, SETTLE_BANK_FLAG, SETTLE_ACCT, SETTLE_ACCT_NM, SETTLE_BANK_ALL_NAME, SETTLE_BANK_CODE,SETTLE_CYCLE,RZTP) "
				+ "	values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		int[] ps=new int[28];
		for(int i=0;i<ps.length;i++){
			ps[i] = Types.VARCHAR;
		}
		final BatchSqlUpdate bsu = new BatchSqlUpdate(MPosDataSource.getInstance(), sqlstr2, ps);
		bsu.setBatchSize(10000);
		jt.query(sqlstr, new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				bsu.update(
					rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),
					rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),
					rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15),
					rs.getString(16),rs.getString(17),rs.getString(18),rs.getString(19),rs.getString(20),
					rs.getString(21),rs.getString(22),rs.getString(23),rs.getString(24),rs.getString(25),
					rs.getString(26),rs.getString(27),rs.getString(28)
				);
			}
		},batid);
		bsu.flush();
	}

	
	/**
	 * 回写BAT2_TERM_FEE_CLEAR
	 * @param batid
	 */
	public static void rewriteTermFee(String batid){
		String sqlstr = "insert into NK_BAT2_TERM_FEE_CLEAR(ID, SYSTM, BATID, SMDT, MY_MCH_NO, MY_SEC_MCH_NO, TERM_NO, CHANNEL_NO, TCNT, TTRAM, ACNT, ATRAM, RCNT, RTRAM, COST, TOTAL_FEE, RZAMT, RZST, RZDT, RZFN, MCH_NO, SEC_MCH_NM, SETTLE_BANK_FLAG, SETTLE_ACCT, SETTLE_ACCT_NM, SETTLE_BANK_ALL_NAME, SETTLE_BANK_CODE)"
				+ " select ID, SYSTM, BATID, SMDT, MY_MCH_NO, MY_SEC_MCH_NO, TERM_NO, CHANNEL_NO, TCNT, TTRAM, ACNT, ATRAM, RCNT, RTRAM, COST, TOTAL_FEE, RZAMT, RZST, RZDT, RZFN, MCH_NO, SEC_MCH_NM, SETTLE_BANK_FLAG, SETTLE_ACCT, SETTLE_ACCT_NM, SETTLE_BANK_ALL_NAME, SETTLE_BANK_CODE "
				+ " from BAT2_TERM_FEE_CLEAR where BATID=? ";
		jt.update(sqlstr,batid);		
	}
	
	/**
	 * 回写 BAT2_BANK_MCH_TOTAL
	 * @param batid
	 * @author LINYQ
	 * @date 20170425
	 */
	public static void rewriteBankMchTotal(String batid){
		String sqlstr = "INSERT INTO NK_BAT2_BANK_MCH_TOTAL(ID, CRT_TM, MDF_TM, BATID, SMDT, BANK_CODE, CHANNEL_NO, MY_MCH_NO, MY_SEC_MCH_NO, MY_SEC_MCH_NM, ACNT, ATRAM, RCNT, RTRAM, TOTAL_FEE, TTRAM, RZAMT, TCNT, MCHTP) "
					+ " SELECT ID, CRT_TM, MDF_TM, BATID, SMDT, BANK_CODE, CHANNEL_NO, MY_MCH_NO, MY_SEC_MCH_NO, MY_SEC_MCH_NM, ACNT, ATRAM, RCNT, RTRAM, TOTAL_FEE, TTRAM, RZAMT, TCNT, MCHTP "
					+ "	FROM BAT2_BANK_MCH_TOTAL WHERE BATID = ? ";
		jt.update(sqlstr,batid);	
	}

	/**
	 * fail表中有隔日对账成功的记录
	 * 按照ID更新主库
	 * @param batid
	 */
	public static void rewriteCmpResultFailCksc(final String batid){
		String sqlstr = "select ID from BAT2_CMP_RESULT_FAIL where CKSC_BATID = ? and ckfg=0 for read only";
		String sqlstr2 = "update NK_BAT2_CMP_RESULT_FAIL set CKFG=0,CKSC_BATID=? where ID=?";
		int[] ps=new int[2];
		for(int i=0;i<ps.length;i++){
			ps[i] = Types.VARCHAR;
		}
		final BatchSqlUpdate bsu = new BatchSqlUpdate(MPosDataSource.getInstance(), sqlstr2, ps);
		bsu.setBatchSize(200);
		jt.query(sqlstr, new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				bsu.update(batid,rs.getString(1));
			}
		},batid);
		bsu.flush();
	}
	
	/**
	 * 日对账成功失败笔数统计
	 * @param day
	 * @return
	 */
	public static JSONArray dayStatistics(String day){
		String sqlstr="select 'CMP_SC',count(1) from BAT2_CMP_RESULT where smdt=? "
				+ " union all select 'CMP_FAIL',count(1) from BAT2_CMP_RESULT_FAIL where smdt=?";
		return XytHelper.cvtDbList2Json(jt.queryForList(sqlstr, day,day));
	}

	
	/**
	 * 分润计算
	 * @param batid 可为0
	 * @param ckdt 必输，对CKDT对账日期的数据计算分润，对某日计算时，batid可不输
	 * @return
	 */
	public static String profitClear(final String batid,final String ckdt){
		String sqlstr = "{call MPOS.SP_PROFIT_CLEAR(?,?,?)}";
		return jt.execute(sqlstr, new CallableStatementCallback<String>() {
			public String doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				cs.setString(1, batid);
				cs.setString(2, ckdt);
				cs.registerOutParameter(3, Types.VARCHAR);
				cs.execute();
				return cs.getString(3);
			}
		});
	}

	
	/**
	 * 导入上日分润费率数据
	 */
	public static int loadProfitRate(){
		String snapdt = PhioH.nowDate("yyyyMMdd");
		//以防万一，先清理该日数据
		String clearCmd = "delete from IMP_SECD_PROFIT_RATE_HIS where snapdt=?";
		jt.update(clearCmd,snapdt);
		//load insert
		String loadCmd = "load from (select '"+snapdt+"',ID, SUB_ZX_MCHT_NO, MAIN_MCHT_NO, AGNT_BRH_ID, AGNT_BRH_NAME, PROFIT_RATE, STCD, ORG_ID, ORG_NAME, SECD_ORG_ID, SECD_ORG_NAME, ACT_TP, CRT_OPR_ID, CRT_TM, UPD_OPR_ID, UPD_TM "
				+ "		from NK_IMP_SECD_PROFIT_RATE for read only)"
				+ " of cursor messages on server insert into IMP_SECD_PROFIT_RATE_HIS nonrecoverable";
		CmdSp cmdSp =new CmdSp(MPosDataSource.getInstance());
		Map<String, Object> rt = cmdSp.db2load(loadCmd);
		JSONObject jort = new JSONObject();
		jort.putAll(rt);
		int sqlcode = jort.getIntLike("SQLCODE");
		if(sqlcode != 0){
			log.error("导入分润费率失败:"+jort.toString());
			throw new RuntimeException("导入分润费率失败");
		}else{
			try{
				int cnt = jort.getIntLike("ROWS_LOADED");
				return cnt;
			}catch(Throwable e){
				log.error("获取ROWS_LOADED失败",e);
				log.error(jort.toString());
				return 0;
			}
		}
	}
	
	/**
	 * 用于每天对账前测试邦联数据库的连接
	 */
	public static void testFredLink(){
		String sqlstr = "select 1 from NK_IMP_MAIN_MCHT_INFO fetch first 1 rows only";
		jt.queryForList(sqlstr);
	}
	
	
	/**
	 * 记录跑批对账的阶段
	 * 目前用于通知主库，对账单明细已经回写完成
	 * @param key
	 * @param val
	 */
	public static void addFlag(String key,String val){
		String sqlstr = "insert into NK_BAT2_FLAG(KEY,VAL1) VALUES(?,?)";
		jt.update(sqlstr, key,val);
	}

	
	/**
	 * 尝试恢复load pending状态
	 * @param tbNameWithSchema
	 */
	public static void loadReset(String tbNameWithSchema){
		String loadCmd = "load from /dev/null of del terminate into "+tbNameWithSchema+" nonrecoverable";
		CmdSp cmdSp =new CmdSp(MPosDataSource.getInstance());
		Map<String, Object> rt = cmdSp.db2load(loadCmd);
		JSONObject jort = new JSONObject();
		jort.putAll(rt);
		int sqlcode = jort.getIntLike("SQLCODE");
		if(sqlcode != 0){
			log.error("["+tbNameWithSchema+"]load reset failed:"+jort.toString());
			throw new RuntimeException("["+tbNameWithSchema+"]load reset failed");
		}
	}
	
	/**
	 * 回写BAT2_AGT_PROFIT
	 * @param batid
	 */
	public static void rewriteProfit(String batid){
		String sqlstr = "insert into NK_BAT2_AGT_PROFIT(ID, SYSTM, BATID, CALDT, MCH_NO, MY_SEC_MCH_NO, AGNT_BRH_ID, AGNT_BRH_NAME, PROFIT_RATE, PROFIT_FEE, CNT, TRAM, COST, TOTAL_FEE )"
				+ " select ID, SYSTM, BATID, CALDT, MCH_NO, MY_SEC_MCH_NO, AGNT_BRH_ID, AGNT_BRH_NAME, PROFIT_RATE, PROFIT_FEE, CNT, TRAM, COST, TOTAL_FEE  "
				+ " from BAT2_AGT_PROFIT where BATID=? ";
		jt.update(sqlstr,batid);		
	}
	
	/**
	 * 检查分润总体情况
	 * @param batid
	 * @return
	 */
	public static JSONObject checkProfitBalance(String batid){
		String sqlstr = "select cast(sum(avail_profit) as dec(15,2)) 待分润,sum(profit_fee) 分润,cast(sum(avail_profit-profit_fee) as dec(15,2)) 差额  from "
				+ "(select mch_no,my_sec_mch_no,sum(profit_fee) profit_fee,avg(total_fee)-avg(cost)-avg(my_ptmch_fee) avail_profit "
				+ "	from BAT2_AGT_PROFIT where BATID=? group by mch_no,my_sec_mch_no)";
		List<Map<String, Object>> rows = jt.queryForList(sqlstr,batid);
		if(rows.size() == 0)
			return new JSONObject();
		else
			return XytHelper.cvtDbMap2Json(rows.get(0));
	}

	/**
	 * 批量插入渠道对账DEBUG表
	 * @return
	 */
	public static BatchSqlUpdate batWxTransDebug(){
		String sqlstr = "insert into MPOS.BAT2_CMP_CHL_DTL_DEBUG(TRTM,CHANNEL_NO, CHL_ORDER_ID, MY_ORDER_ID, MCH_NO, SEC_MCH_NO, TRAM, TRTP,REL_ORDER_ID, MEMO,CLDT,BNO,PAYBANK) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		int[] ps=new int[13];
		for(int i=0;i<ps.length;i++){
			ps[i] = Types.VARCHAR;
		}
		BatchSqlUpdate bsu = new BatchSqlUpdate(MPosDataSource.getInstance(), sqlstr, ps);
		bsu.setBatchSize(7000);
		return bsu;
	}
	
	/**
	 * 获取指定主商户在我方对账表中是否有交易
	 * 用于对账单为空的辅助判断
	 * @param mchNo
	 * @return
	 */
	public static int countMchOrders(String mchNo){
		String sqlstr = "select count(1) from MPOS.BAT2_CMP_MPOS_DTL where MCH_NO=?";
		return jt.queryForInt(sqlstr, mchNo);
	}
	
	/**
	 * 检查流水中费率是否为空
	 * @return json _respcd=0 检查通过，=1有异常
	 */
	public static JSONObject checkDtlFeeRate(){
		JSONObject rt = new JSONObject().append("_respcd", 0);
		String sqlstr = "select MY_ORDER_ID from MPOS.BAT2_CMP_MPOS_DTL where MY_MCH_RATE is null or (FZFG in ('Y','D') and MY_SEC_MCH_RATE is null) fetch first 1 rows only";
		List<Map<String, Object>> lst = jt.queryForList(sqlstr);
		if(lst.size() > 0){
			rt.append("dtlSampleMyOrderId", lst.get(0).get("MY_ORDER_ID")).append("_respcd", 1);
		}
		sqlstr = "select MY_ORDER_ID from MPOS.BAT2_CMP_MPOS_DTL_FAIL where MY_MCH_RATE is null or (FZFG in ('Y','D') and MY_SEC_MCH_RATE is null) fetch first 1 rows only";
		lst = jt.queryForList(sqlstr);
		if(lst.size() > 0){
			rt.append("dtlFailSampleMyOrderId", lst.get(0).get("MY_ORDER_ID")).append("_respcd", 1);
		}
		return rt;
	}
	
	/**
	 * 批量插入微信冻结单
	 * @return
	 */
	public static BatchSqlUpdate batWxFrzs(){
		String sqlstr = "insert into MPOS.BAT2_CMP_CHL_FROZEN(TRTM, CHANNEL_NO, CHL_ORDER_ID, MY_ORDER_ID, MCH_NO, SEC_MCH_NO, TRAM, FROZ_FG, FRDT, UFDT, MEMO, BNO,FREEZER) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		int[] ps=new int[13];
		for(int i=0;i<ps.length;i++){
			ps[i] = Types.VARCHAR;
		}
		BatchSqlUpdate bsu = new BatchSqlUpdate(MPosDataSource.getInstance(), sqlstr, ps);
		bsu.setBatchSize(1000);
		return bsu;
	}

	
	/**
	 * 渠道冻结单处理
	 * @return
	 */
	public static String frozen(final String batno,final String chlNo){
		String sqlstr = "{call MPOS.SP_CHL_FROZEN(?,?,?)}";
		return jt.execute(sqlstr, new CallableStatementCallback<String>() {
			public String doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				cs.setString(1, chlNo);
				cs.setString(2, batno);
				cs.registerOutParameter(3, Types.VARCHAR);
				cs.execute();
				return cs.getString(3);
			}
		});
	}

	/**
	 * 回写BAT2_CMP_CHL_FROZEN
	 * @param batid
	 */
	public static void rewriteFrozen(String batno){
		String sqlstr = "insert into NK_BAT2_CMP_CHL_FROZEN(ID, CRT_TM, MDF_TM, TRTM, CHANNEL_NO, CHL_ORDER_ID, MY_ORDER_ID, MCH_NO, SEC_MCH_NO, TRAM, FROZ_FG, FRDT,UFDT, MEMO, BNO, MY_MCH_NO, MY_SEC_MCH_NO, MCH_ORDER_ID, FREEZER, BANK_CODE)"
				+ " select ID, CRT_TM, MDF_TM, TRTM, CHANNEL_NO, CHL_ORDER_ID, MY_ORDER_ID, MCH_NO, SEC_MCH_NO, TRAM, FROZ_FG, FRDT,UFDT, MEMO, BNO, MY_MCH_NO, MY_SEC_MCH_NO, MCH_ORDER_ID, FREEZER, BANK_CODE "
				+ " from BAT2_CMP_CHL_FROZEN where BNO=? ";
		jt.update(sqlstr,batno);
	}

	/**
	 * 多次撤销会生成多笔成功的撤销流水，对应同一笔原交易，在通过原交易号进行对账时，会造成多退款的问题
	 * 增加该步骤，将多余的撤销流水号改写 
	 * @param batid
	 */
	public static int ovRevokeNo(){
		String sqlstr = "update BAT2_CMP_MPOS_DTL set REL_ORDER_ID=REL_ORDER_ID||'-'||ID where id in"
				+ "(select id from (select row_number() over(PARTITION BY REL_ORDER_ID order by id) rn,id from BAT2_CMP_MPOS_DTL where TRTP='21' and REL_ORDER_ID is not null) where rn>1)";
		return jt.update(sqlstr);
	}

	
}
