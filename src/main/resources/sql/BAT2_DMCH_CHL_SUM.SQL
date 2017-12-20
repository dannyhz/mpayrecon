--mposbat
CREATE TABLE MPOS.BAT2_DMCH_CHL_SUM
(
ID	bigint	NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1,INCREMENT BY 1,CACHE 100),
SYSTM	TIMESTAMP	with default CURRENT_TIMESTAMP,
BATID DECFLOAT(16) NOT NULL,
CALDT INTEGER NOT NULL,
TRDT INTEGER NOT NULL,
CHANNEL_NO	VARCHAR(50) with default '',
MCH_NO	VARCHAR(50) with default '',
MY_MCH_NO	VARCHAR(50) with default '',
TCNT INTEGER  with default 0,
TTRAM	DEC(15,2) with default 0,
ACNT INTEGER  with default 0,
ATRAM	DEC(15,2) with default 0,
RCNT INTEGER  with default 0,
RTRAM	DEC(15,2) with default 0,
COST	DEC(15,2) with default 0,
TOTAL_FEE	DEC(15,2) with default 0,
BANK_PROFIT_FEE	DEC(15,2) with default 0,
AGT_PROFIT_FEE	DEC(15,2) with default 0,
RZAMT	DEC(15,2) with default 0,
RZST	SMALLINT default 1,
RZDT	INTEGER,
MEMO	VARCHAR(100) with default '',
 primary key (ID)
)
;

CREATE unique INDEX MPOS.UIDX_BAT2_DMCH_CHL_SUM_1 ON MPOS.BAT2_DMCH_CHL_SUM(TRDT,MCH_NO) ;

COMMENT ON TABLE MPOS.BAT2_DMCH_CHL_SUM IS '直连商户交易汇总表';
	
COMMENT ON MPOS.BAT2_DMCH_CHL_SUM ( 
	ID is 'ID',
	SYSTM is '入库时间戳',
	BATID IS 'BATID',
	CALDT IS '计算日期',
	TRDT is '交易日期',
	CHANNEL_NO is '渠道',
	MCH_NO is '渠道方商户号',
	MY_MCH_NO is '我方一级商户号(预留)',
	TCNT IS '总笔数',
	TTRAM IS '轧差金额',
	ACNT IS '正交易笔数',
	ATRAM IS '正交易总金额',
	RCNT IS '反交易笔数',
	RTRAM IS '反交易总金额',
	COST is '渠道成本',
	TOTAL_FEE is '总手续费',
	BANK_PROFIT_FEE is '营销行分润',
	AGT_PROFIT_FEE is '代理分润',
	RZAMT is '商户入账金额',
	RZST IS '入账状态 0=完成 1=待入账',
	RZDT IS '入账日期',
	MEMO is '备注'
	);
	
	
--主库	
CREATE TABLE MPOS.BAT2_DMCH_CHL_SUM
(
ID	bigint	NOT NULL ,
SYSTM	TIMESTAMP,
BATID DECFLOAT(16) NOT NULL,
CALDT INTEGER NOT NULL,
TRDT INTEGER NOT NULL,
CHANNEL_NO	VARCHAR(50) with default '',
MCH_NO	VARCHAR(50) with default '',
MY_MCH_NO	VARCHAR(50) with default '',
TCNT INTEGER  with default 0,
TTRAM	DEC(15,2) with default 0,
ACNT INTEGER  with default 0,
ATRAM	DEC(15,2) with default 0,
RCNT INTEGER  with default 0,
RTRAM	DEC(15,2) with default 0,
COST	DEC(15,2) with default 0,
TOTAL_FEE	DEC(15,2) with default 0,
BANK_PROFIT_FEE	DEC(15,2) with default 0,
AGT_PROFIT_FEE	DEC(15,2) with default 0,
RZAMT	DEC(15,2) with default 0,
RZST	SMALLINT default 1,
RZDT	INTEGER,
MEMO	VARCHAR(100) with default '',
 primary key (ID)
)
--ORGANIZE BY (TRDT,BATID)
;

CREATE unique INDEX MPOS.UIDX_BAT2_DMCH_CHL_SUM_1 ON MPOS.BAT2_DMCH_CHL_SUM(TRDT,MCH_NO) ;

COMMENT ON TABLE MPOS.BAT2_DMCH_CHL_SUM IS '直连商户交易汇总表';
	
COMMENT ON MPOS.BAT2_DMCH_CHL_SUM ( 
	ID is 'ID',
	SYSTM is '入库时间戳',
	BATID IS 'BATID',
	CALDT IS '计算日期',
	TRDT is '交易日期',
	CHANNEL_NO is '渠道',
	MCH_NO is '渠道方商户号',
	MY_MCH_NO is '我方一级商户号(预留)',
	TCNT IS '总笔数',
	TTRAM IS '轧差金额',
	ACNT IS '正交易笔数',
	ATRAM IS '正交易总金额',
	RCNT IS '反交易笔数',
	RTRAM IS '反交易总金额',
	COST is '渠道成本',
	TOTAL_FEE is '总手续费',
	BANK_PROFIT_FEE is '营销行分润',
	AGT_PROFIT_FEE is '代理分润',
	RZAMT is '商户入账金额',
	RZST IS '入账状态 0=完成 1=待入账',
	RZDT IS '入账日期',
	MEMO is '备注'
	);

--nickname
CREATE OR REPLACE NICKNAME MPOS.NK_BAT2_DMCH_CHL_SUM
  FOR SVRWR.MPOS.BAT2_DMCH_CHL_SUM;		