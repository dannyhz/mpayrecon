package citic.hz.mpos.service.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import citic.hz.mpos.service.MPosDataSource;
import citic.hz.mpos.service.dao.po.BillingOrder;

public class MPayBillDao {
	
	private static final Logger log = Logger.getLogger(MPayBillDao.class);
	
	private JdbcTemplate jt = new JdbcTemplate(MPosDataSource.getInstance()); 
	
	public List<BillingOrder> getChannelOrders(String channelNo){

		String args[] = {channelNo};
		
		return jt.query("select TRTM,CHANNEL_NO,CHL_ORDER_ID,MY_ORDER_ID,MCH_NO,"
				+ "SEC_MCH_NO,TRAM,TRTP,REL_ORDER_ID,MEMO,CLDT,BNO,PAYBANK "
				+ "from BAT2_CMP_CHL_DTL where CHANNEL_NO = ? ", args, new RowMapper<BillingOrder>(){

			@Override
			public BillingOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
				BillingOrder chOrder = new BillingOrder();
				chOrder.setTRTM(rs.getString("TRTM"));
				chOrder.setCHANNEL_NO(rs.getString("CHANNEL_NO"));
				chOrder.setCHL_ORDER_ID(rs.getString("CHL_ORDER_ID"));
				chOrder.setMY_ORDER_ID(rs.getString("MY_ORDER_ID"));
				chOrder.setMCH_NO(rs.getString("MCH_NO"));
				chOrder.setSEC_MCH_NO(rs.getString("SEC_MCH_NO"));
				chOrder.setTRAM(rs.getString("TRAM"));
				chOrder.setTRTP(rs.getString("TRTP"));
				chOrder.setREL_ORDER_ID(rs.getString("REL_ORDER_ID"));
				chOrder.setMEMO(rs.getString("MEMO"));
				chOrder.setCLDT(rs.getString("CLDT"));
				chOrder.setBNO(rs.getString("BNO"));
				chOrder.setPAYBANK(rs.getString("PAYBANK"));
				return chOrder;
			}
			
		});
		
		
	}
	

	public List<BillingOrder> getMyOrders(String channelNo){

		String args[] = {channelNo};
		
		return jt.query("select TRTM, CHANNEL_NO, CHL_ORDER_ID, MY_ORDER_ID, MCH_NO," +
						"SEC_MCH_NO,TRAM,TRTP,REL_ORDER_ID,MEMO,BRH_ID,MY_MCH_NO,"+
						"MY_SEC_MCH_NO, TERM_NO,CLDT,BNO,TRADE_TYPE,MCH_ORDER_ID,"+
						"REL_MCH_ORDER_ID,FZFG,MY_MCH_RATE,MY_SEC_MCH_RATE,D0FG,TRADE_CODE,"+
						"PAYBANK,BANK_CODE "
				+ "from BAT2_CMP_MPOS_DTL where CHANNEL_NO = ? ", args, new RowMapper<BillingOrder>(){

			@Override
			public BillingOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
				BillingOrder myOrder = new BillingOrder();
				myOrder.setTRTM(rs.getString("TRTM"));
				myOrder.setCHANNEL_NO(rs.getString("CHANNEL_NO"));
				myOrder.setCHL_ORDER_ID(rs.getString("CHL_ORDER_ID"));
				myOrder.setMY_ORDER_ID(rs.getString("MY_ORDER_ID"));
				myOrder.setMCH_NO(rs.getString("MCH_NO"));
				myOrder.setSEC_MCH_NO(rs.getString("SEC_MCH_NO"));
				myOrder.setTRAM(rs.getString("TRAM"));
				myOrder.setTRTP(rs.getString("TRTP"));
				myOrder.setREL_ORDER_ID(rs.getString("REL_ORDER_ID"));
				myOrder.setMEMO(rs.getString("MEMO"));
				myOrder.setBRH_ID(rs.getString("BRH_ID"));
				myOrder.setMY_MCH_NO(rs.getString("MY_MCH_NO"));
				myOrder.setMY_SEC_MCH_NO(rs.getString("MY_SEC_MCH_NO"));
				myOrder.setTERM_NO(rs.getString("TERM_NO"));
				myOrder.setCLDT(rs.getString("CLDT"));
				myOrder.setBNO(rs.getString("BNO"));
				myOrder.setTRADE_TYPE(rs.getString("TRADE_TYPE"));
				myOrder.setMCH_ORDER_ID(rs.getString("MCH_ORDER_ID"));
				myOrder.setREL_MCH_ORDER_ID(rs.getString("REL_MCH_ORDER_ID"));
				myOrder.setFZFG(rs.getString("FZFG"));
				myOrder.setMY_MCH_RATE(rs.getString("MY_MCH_RATE"));
				myOrder.setD0FG(rs.getString("D0FG"));
				myOrder.setTRADE_CODE(rs.getString("TRADE_CODE"));
				myOrder.setPAYBANK(rs.getString("PAYBANK"));
				myOrder.setBANK_CODE(rs.getString("BANK_CODE"));
				
				return myOrder;
			}
			
		});
		
		
	}

	
	public List<BillingOrder> getMyFailOrders(String channelNo){

		String args[] = {channelNo};
		
		return jt.query("select TRTM, CHANNEL_NO, CHL_ORDER_ID, MY_ORDER_ID, MCH_NO," +
						"SEC_MCH_NO,TRAM,TRTP,REL_ORDER_ID,MEMO,BRH_ID,MY_MCH_NO,"+
						"MY_SEC_MCH_NO, TERM_NO,CLDT,BNO,TRADE_TYPE,MCH_ORDER_ID,"+
						"REL_MCH_ORDER_ID,FZFG,MY_MCH_RATE,MY_SEC_MCH_RATE,D0FG,TRADE_CODE,"+
						"PAYBANK,BANK_CODE "
				+ "from BAT2_CMP_MPOS_DTL where CHANNEL_NO = ? ", args, new RowMapper<BillingOrder>(){

			@Override
			public BillingOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
				BillingOrder myOrder = new BillingOrder();
				myOrder.setTRTM(rs.getString("TRTM"));
				myOrder.setCHANNEL_NO(rs.getString("CHANNEL_NO"));
				myOrder.setCHL_ORDER_ID(rs.getString("CHL_ORDER_ID"));
				myOrder.setMY_ORDER_ID(rs.getString("MY_ORDER_ID"));
				myOrder.setMCH_NO(rs.getString("MCH_NO"));
				myOrder.setSEC_MCH_NO(rs.getString("SEC_MCH_NO"));
				myOrder.setTRAM(rs.getString("TRAM"));
				myOrder.setTRTP(rs.getString("TRTP"));
				myOrder.setREL_ORDER_ID(rs.getString("REL_ORDER_ID"));
				myOrder.setMEMO(rs.getString("MEMO"));
				myOrder.setBRH_ID(rs.getString("BRH_ID"));
				myOrder.setMY_MCH_NO(rs.getString("MY_MCH_NO"));
				myOrder.setMY_SEC_MCH_NO(rs.getString("MY_SEC_MCH_NO"));
				myOrder.setTERM_NO(rs.getString("TERM_NO"));
				myOrder.setCLDT(rs.getString("CLDT"));
				myOrder.setBNO(rs.getString("BNO"));
				myOrder.setTRADE_TYPE(rs.getString("TRADE_TYPE"));
				myOrder.setMCH_ORDER_ID(rs.getString("MCH_ORDER_ID"));
				myOrder.setREL_MCH_ORDER_ID(rs.getString("REL_MCH_ORDER_ID"));
				myOrder.setFZFG(rs.getString("FZFG"));
				myOrder.setMY_MCH_RATE(rs.getString("MY_MCH_RATE"));
				myOrder.setD0FG(rs.getString("D0FG"));
				myOrder.setTRADE_CODE(rs.getString("TRADE_CODE"));
				myOrder.setPAYBANK(rs.getString("PAYBANK"));
				myOrder.setBANK_CODE(rs.getString("BANK_CODE"));
				
				return myOrder;
			}
			
		});
		
		
	}
	
}
