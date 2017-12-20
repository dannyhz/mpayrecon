	--mposbat
	--drop table MPOS.BAT2_DMCH_CHL_DTL;
	CREATE TABLE MPOS.BAT2_DMCH_CHL_DTL
	(
	ID	bigint	NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1,INCREMENT BY 1,CACHE 100),
	UKEY VARCHAR(50) NOT NULL with default '',
	SYSTM	TIMESTAMP	with default CURRENT_TIMESTAMP,
	BATID DECFLOAT(16) NOT NULL,
	CALDT INTEGER NOT NULL,
	TRDT INTEGER NOT NULL,
	TRTM VARCHAR(30) with default '',
	CHANNEL_NO	VARCHAR(50) with default '',
	CHL_ORDER_ID	VARCHAR(50) with default '',
	MCH_ORDER_ID	VARCHAR(50) with default '',
	MCH_NO	VARCHAR(50) with default '',
	MY_MCH_NO	VARCHAR(50) with default '',
	TRAM	DEC(15,2) with default 0,
	TRTP	VARCHAR(50) with default '',
	MEMO	VARCHAR(250) with default '',
	PAYBANK VARCHAR(50) with default '',
	TRADE_TYPE VARCHAR(50) with default '',
	USERID VARCHAR(50) with default '',
	TR_RESP VARCHAR(50) with default '',
	REFUND_ORDER_ID VARCHAR(50) with default '',
	REFUND_TRAM DEC(15,2) with default 0,
	REFUND_RESP VARCHAR(50) with default '',
	COST_RATE	DECFLOAT(16) with default 0,
	COST	DEC(15,2) with default 0,
	TOTAL_RATE	DECFLOAT(16) with default 0,
	TOTAL_FEE	DEC(15,2) with default 0,
	BANK_PROFIT_RATE	DECFLOAT(16) with default 0,
	BANK_PROFIT_FEE	DEC(15,2) with default 0,
	RZAMT	DEC(15,2) with default 0,
	AGT_PROFIT_FEE	DEC(15,2) with default 0,
	 primary key (ID)
	)
	compress yes
	ORGANIZE BY (TRDT,BATID)
	;
	
	CREATE unique INDEX MPOS.UIDX_BAT2_CMP_CHL_DTL_1 ON MPOS.BAT2_DMCH_CHL_DTL(UKEY) ;
	--CREATE INDEX MPOS.IDX_BAT2_CMP_CHL_DTL_2 ON MPOS.BAT2_DMCH_CHL_DTL(CLDT) ;
	
	COMMENT ON TABLE MPOS.BAT2_DMCH_CHL_DTL IS '直连商户交易清单表(源于渠道对账文件)';
	
	COMMENT ON MPOS.BAT2_DMCH_CHL_DTL ( 
	ID is 'ID',
	UKEY is '用于保障数据唯一',
	SYSTM is '入库时间戳',
	BATID IS 'BATID',
	CALDT IS '计算日期',
	TRDT is '交易日期',
	TRTM is '交易时间',
	CHANNEL_NO is '渠道',
	CHL_ORDER_ID is '渠道方订单号',
	MCH_ORDER_ID is '商户订单号',
	MCH_NO is '渠道方商户号',
	MY_MCH_NO is '我方一级商户号(预留)',
	TRAM is '金额',
	TRTP is '交易类型',
	MEMO is '交易备注',
	PAYBANK IS '付款银行',
	TRADE_TYPE IS '交易方式，如JSAPI、NATIVE、MICROPAY',
	USERID is '用户标记',
	TR_RESP is '交易结果',
	REFUND_ORDER_ID	is '退款订单号',
	REFUND_TRAM is '退款金额',
	REFUND_RESP is '退款交易结果',
	COST_RATE is '渠道成本费率',
	COST is '渠道成本',
	TOTAL_RATE is '向商户收取费率',
	TOTAL_FEE is '商户手续费',
	BANK_PROFIT_RATE is '营销行分润费率',
	BANK_PROFIT_FEE is '营销行分润',
	RZAMT is '商户入账金额',
	AGT_PROFIT_FEE is '代理分润'
	);
	
	
	
--主库
	CREATE TABLE MPOS.BAT2_DMCH_CHL_DTL
	(
	ID	bigint	NOT NULL ,
	UKEY VARCHAR(50) NOT NULL ,
	SYSTM	TIMESTAMP	,
	BATID DECFLOAT(16) NOT NULL,
	CALDT INTEGER NOT NULL,
	TRDT INTEGER NOT NULL,
	TRTM VARCHAR(30) with default '',
	CHANNEL_NO	VARCHAR(50) with default '',
	CHL_ORDER_ID	VARCHAR(50) with default '',
	MCH_ORDER_ID	VARCHAR(50) with default '',
	MCH_NO	VARCHAR(50) with default '',
	MY_MCH_NO	VARCHAR(50) with default '',
	TRAM	DEC(15,2) with default 0,
	TRTP	VARCHAR(50) with default '',
	MEMO	VARCHAR(250) with default '',
	PAYBANK VARCHAR(50) with default '',
	TRADE_TYPE VARCHAR(50) with default '',
	USERID VARCHAR(50) with default '',
	TR_RESP VARCHAR(50) with default '',
	REFUND_ORDER_ID VARCHAR(50) with default '',
	REFUND_TRAM DEC(15,2) with default 0,
	REFUND_RESP VARCHAR(50) with default '',
	COST_RATE	DECFLOAT(16) with default 0,
	COST	DEC(15,2) with default 0,
	TOTAL_RATE	DECFLOAT(16) with default 0,
	TOTAL_FEE	DEC(15,2) with default 0,
	BANK_PROFIT_RATE	DECFLOAT(16) with default 0,
	BANK_PROFIT_FEE	DEC(15,2) with default 0,
	RZAMT	DEC(15,2) with default 0,
	AGT_PROFIT_FEE	DEC(15,2) with default 0,
	 primary key (ID)
	)
	compress yes
	ORGANIZE BY (TRDT,BATID)
	;
	
	--CREATE unique INDEX MPOS.UIDX_BAT2_CMP_CHL_DTL_1 ON MPOS.BAT2_DMCH_CHL_DTL(UKEY) ;
	--CREATE INDEX MPOS.IDX_BAT2_CMP_CHL_DTL_2 ON MPOS.BAT2_DMCH_CHL_DTL(CLDT) ;
	
	COMMENT ON TABLE MPOS.BAT2_DMCH_CHL_DTL IS '直连商户交易清单表(源于渠道对账文件)';
	
	COMMENT ON MPOS.BAT2_DMCH_CHL_DTL ( 
	ID is 'ID',
	UKEY is '用于保障数据唯一',
	SYSTM is '入库时间戳',
	BATID IS 'BATID',
	CALDT IS '计算日期',
	TRDT is '交易日期',
	TRTM is '交易时间',
	CHANNEL_NO is '渠道',
	CHL_ORDER_ID is '渠道方订单号',
	MCH_ORDER_ID is '商户订单号',
	MCH_NO is '渠道方商户号',
	MY_MCH_NO is '我方一级商户号(预留)',
	TRAM is '金额',
	TRTP is '交易类型',
	MEMO is '交易备注',
	PAYBANK IS '付款银行',
	TRADE_TYPE IS '交易方式，如JSAPI、NATIVE、MICROPAY',
	USERID is '用户标记',
	TR_RESP is '交易结果',
	REFUND_ORDER_ID	is '退款订单号',
	REFUND_TRAM is '退款金额',
	REFUND_RESP is '退款交易结果',
	COST_RATE is '渠道成本费率',
	COST is '渠道成本',
	TOTAL_RATE is '向商户收取费率',
	TOTAL_FEE is '商户手续费',
	BANK_PROFIT_RATE is '营销行分润费率',
	BANK_PROFIT_FEE is '营销行分润',
	RZAMT is '商户入账金额',
	AGT_PROFIT_FEE is '代理分润'
	);
	
--nickname
CREATE OR REPLACE NICKNAME MPOS.NK_BAT2_DMCH_CHL_DTL
  FOR SVRWR.MPOS.BAT2_DMCH_CHL_DTL;	