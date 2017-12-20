package citic.hz.mpos.kit;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class CmdSp extends JdbcDaoSupport{
	
	private static final Logger log=Logger.getLogger(CmdSp.class);
	private final static String ADMIN_CMD="call SYSPROC.ADMIN_CMD(?)";
	private static Pattern rowPattern1 = Pattern.compile(".+number \"(\\d+)\"\\.");
	private static Pattern rowPattern2 = Pattern.compile(".+row \"(\\d+)\".+");
	private static int FAILEDROWS_LIMIT=100;
	private static int MSGS_LIMIT=500;
	
	public CmdSp(DataSource ds) {
		setDataSource(ds);
	}

	private Map<String,Object> excuteCmdSp(String cmd){
		Connection con=DataSourceUtils.getConnection(getDataSource());
		Map<String,Object> rt=new HashMap<String, Object>();
		try {
			CallableStatement cs = con.prepareCall(ADMIN_CMD);
			cs.setString(1, cmd);
			boolean results = cs.execute();
			//int rsCount = 0;
			ResultSet rs = null;
		    if(results)
		    	rs = cs.getResultSet();
			ColumnMapRowMapper mapper=new ColumnMapRowMapper();
			if(rs.next())
				rt = mapper.mapRow(rs, 0);
			SQLWarning w = cs.getWarnings();
			if(w!=null){
				log.warn(w.getMessage());
				if(w.getMessage().indexOf("SQL0668")>=0)
					rt.put("sqlcode", -668);
				else
					rt.put("sqlcode", w.getErrorCode());
				w.printStackTrace();
			}else{
				rt.put("sqlcode", 0);
			}
		}catch (SQLException e) {
			log.warn(e.getMessage());
			if(e.getMessage().indexOf("SQL0668")>=0)
				rt.put("sqlcode", -668);
			else
				rt.put("sqlcode", e.getErrorCode());
			e.printStackTrace();
		}finally{
			DataSourceUtils.releaseConnection(con, getDataSource());
		}
		return rt;
	}
	
	/*
	 * 用于import
	 */
	public Map<String,Object> db2import(String cmd){
		Map<String,Object> rt=excuteCmdSp(cmd);
		return preCheckRetImport(rt);
	}
	
	/*
	 * 用于load
	 */
	public Map<String,Object> db2load(String cmd){
		Map<String,Object> rt=excuteCmdSp(cmd);
		return preCheckRetLoad(rt);
	}
	
	private Map<String,Object> preCheckRetLoad(Map<String,Object> rt){
		return preCheckRet(rt,"LOAD");
	}
	private Map<String,Object> preCheckRetImport(Map<String,Object> rt){
		return preCheckRet(rt,"IMPORT");
	}
	
	private Map<String,Object> preCheckRet(final Map<String,Object> rt,final String loadTp){
		assert rt.get("sqlcode")!=null;
		int sqlcode=(Integer)rt.get("sqlcode");
		rt.put("SQLCODE", sqlcode);
		if(rt.get("MSG_RETRIEVAL")!=null){
			String msg_retrieval=(String)rt.get("MSG_RETRIEVAL");
			//final Map<String, Integer> sqlcodeCnt=new HashMap<String, Integer>();
			final List<String> msg_lst=new ArrayList<String>();
			final Set<String> rows=new HashSet<String>();
			getJdbcTemplate().query(msg_retrieval, new ResultSetExtractor<Object>() {
				public Object extractData(ResultSet rs) throws SQLException,
						DataAccessException {
					while(rs.next()){
						String sqlcode=rs.getString("SQLCODE");
						String msg=rs.getString("MSG");
						if(sqlcode == null || msg == null)
							continue;
						msg=msg.replaceAll("\n", " ");
						msg_lst.add(sqlcode+":"+msg);
						if("LOAD".equals(loadTp)){	//LOAD
							if("SQL3227W".equals(sqlcode)){
								Matcher matcher = rowPattern1.matcher(msg);
								if(matcher.matches())
									rows.add(matcher.group(1));
							}
						}else{	//IMPORT
							Matcher matcher = rowPattern2.matcher(msg);
							if(matcher.matches())
								rows.add(matcher.group(1));
						}
						if(rows.size()>=FAILEDROWS_LIMIT || msg_lst.size()>=MSGS_LIMIT){
							msg_lst.add("太多错了...I quit");
							rt.put("LOST_DETAIL",true);
							break;
						}	
					}
					return null;
				}
			});
			if(msg_lst.size()>0)
				rt.put("MSG_LIST", msg_lst);
			if(rows.size()>0)
				rt.put("FAILED_ROWS_NO", rows);
			rt.remove("MSG_RETRIEVAL");
		}
		if(rt.get("MSG_REMOVAL")!=null){
			String msg_removal=(String)rt.get("MSG_REMOVAL");
			getJdbcTemplate().execute(msg_removal);
			rt.remove("MSG_REMOVAL");
		}
		return rt;
	}
}
