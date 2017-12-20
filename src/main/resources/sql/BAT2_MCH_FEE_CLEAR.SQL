
DROP TABLE MPOS.BAT2_MCH_FEE_CLEAR;

CREATE TABLE MPOS.BAT2_MCH_FEE_CLEAR
(
ID	DEC(20,0)	NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1,INCREMENT BY 1,NO CACHE),
SYSTM	TIMESTAMP	with default CURRENT_TIMESTAMP,
BATID DEC(20),
SMDT	CHAR(8) ,
MY_MCH_NO	VARCHAR(50) with default '',
MY_SEC_MCH_NO	VARCHAR(50) with default '',
CHANNEL_NO	VARCHAR(50) with default '',
TCNT DEC(12)  with default 0,
TTRAM	DEC(15,2) with default 0,
ACNT DEC(12)  with default 0,
ATRAM	DEC(15,2) with default 0,
RCNT DEC(12)  with default 0,
RTRAM	DEC(15,2) with default 0,
COST	DEC(15,2) with default 0,
TOTAL_FEE	DEC(15,2) with default 0,
RZAMT	DEC(15,2) with default 0,
RZST	INT default 1,
RZDT	CHAR(8),
RZFN	VARCHAR(200),
MCH_NO VARCHAR(50),
SEC_MCH_NM VARCHAR(100),
SETTLE_BANK_FLAG CHAR(1),
SETTLE_ACCT VARCHAR(50),
SETTLE_ACCT_NM VARCHAR(100),
SETTLE_BANK_ALL_NAME VARCHAR(150),
SETTLE_BANK_CODE VARCHAR(50),
SETTLE_CYCLE VARCHAR(50),
RZTP	INT,
 primary key (ID)
);

CREATE UNIQUE INDEX MPOS.BAT2_MCH_FEE_CLEAR_IDX_1  ON MPOS.BAT2_MCH_FEE_CLEAR (BATID,MY_SEC_MCH_NO,MY_MCH_NO,CHANNEL_NO,RZTP);	--保证每批次的商户、渠道只清算一次

COMMENT ON TABLE MPOS.BAT2_MCH_FEE_CLEAR IS '商户手续费汇总表';

COMMENT ON MPOS.BAT2_MCH_FEE_CLEAR ( 
ID is 'ID',
SYSTM is '入库时间戳',
BATID is '对账批次号,每次只对一个批次的对账数据汇总',
SMDT is '汇总日期',
MY_MCH_NO is '我方一级商户号,对应渠道二级商户号',
MY_SEC_MCH_NO is '我方二级商户号（RZTP=3时为平台商户号）',
CHANNEL_NO is '渠道',
TCNT IS '总笔数',
TTRAM IS '轧差金额',
ACNT IS '正交易笔数',
ATRAM IS '正交易总金额',
RCNT IS '反交易笔数',
RTRAM IS '反交易总金额',
COST is '渠道成本',
TOTAL_FEE is '总手续费',
RZAMT is '商户入账金额',
RZST IS '入账状态 0=完成 1=待入账',
RZDT IS '入账日期',
RZFN IS '入账文件名',
MCH_NO IS '渠道主商户号(不唯一，作废)',
SEC_MCH_NM IS '我方二级商户名称',
SETTLE_BANK_FLAG IS '结算账号是否为本行账号 Y-本行结算账号 N-他行结算账号',
SETTLE_ACCT IS '结算账号',
SETTLE_ACCT_NM IS '结算账户名称',
SETTLE_BANK_ALL_NAME IS '结算账号的收款行行名',
SETTLE_BANK_CODE IS '结算账号的收款行行号',
SETTLE_CYCLE is '结算周期,1=T1,2=D1',
RZTP IS '入账类型，1或者空=普通商入账,2=分账子商户入账,3=清算入平台商户账 ,4=平台商户利差入账'
);









--主库版本
--DROP TABLE MPOS.BAT2_MCH_FEE_CLEAR;
CREATE TABLE MPOS.BAT2_MCH_FEE_CLEAR
(
ID	DEC(20,0)	NOT NULL ,
SYSTM	TIMESTAMP	with default CURRENT_TIMESTAMP,
BATID DEC(20),
SMDT	CHAR(8) ,
MY_MCH_NO	VARCHAR(50) with default '',
MY_SEC_MCH_NO	VARCHAR(50) with default '',
CHANNEL_NO	VARCHAR(50) with default '',
TCNT DEC(12)  with default 0,
TTRAM	DEC(15,2) with default 0,
ACNT DEC(12)  with default 0,
ATRAM	DEC(15,2) with default 0,
RCNT DEC(12)  with default 0,
RTRAM	DEC(15,2) with default 0,
COST	DEC(15,2) with default 0,
TOTAL_FEE	DEC(15,2) with default 0,
RZAMT	DEC(15,2) with default 0,
RZST	INT default 1,
RZDT	CHAR(8),
RZFN	VARCHAR(200),
MCH_NO VARCHAR(50),
SEC_MCH_NM VARCHAR(100),
SETTLE_BANK_FLAG CHAR(1),
SETTLE_ACCT VARCHAR(50),
SETTLE_ACCT_NM VARCHAR(100),
SETTLE_BANK_ALL_NAME VARCHAR(150),
SETTLE_BANK_CODE VARCHAR(50),
SETTLE_CYCLE VARCHAR(50),
RZTP	INT,
 primary key (ID)
);

CREATE UNIQUE INDEX MPOS.BAT2_MCH_FEE_CLEAR_IDX_1  ON MPOS.BAT2_MCH_FEE_CLEAR (BATID,MY_SEC_MCH_NO,MY_MCH_NO,CHANNEL_NO,RZTP);	--保证每批次的商户、渠道只清算一次
CREATE INDEX MPOS.BAT2_MCH_FEE_CLEAR_IDX_2 ON MPOS.BAT2_MCH_FEE_CLEAR(SMDT);	

COMMENT ON TABLE MPOS.BAT2_MCH_FEE_CLEAR IS '商户手续费汇总表';

COMMENT ON MPOS.BAT2_MCH_FEE_CLEAR ( 
ID is 'ID',
SYSTM is '入库时间戳',
BATID is '对账批次号,每次只对一个批次的对账数据汇总',
SMDT is '汇总日期',
MY_MCH_NO is '我方一级商户号,对应渠道二级商户号',
MY_SEC_MCH_NO is '我方二级商户号（RZTP=3时为平台商户号）',
CHANNEL_NO is '渠道',
TCNT IS '总笔数',
TTRAM IS '轧差金额',
ACNT IS '正交易笔数',
ATRAM IS '正交易总金额',
RCNT IS '反交易笔数',
RTRAM IS '反交易总金额',
COST is '渠道成本',
TOTAL_FEE is '总手续费',
RZAMT is '商户入账金额',
RZST IS '入账状态 0=完成 1=待入账',
RZDT IS '入账日期',
RZFN IS '入账文件名',
MCH_NO IS '渠道主商户号(不唯一，作废)',
SEC_MCH_NM IS '我方二级商户名称',
SETTLE_BANK_FLAG IS '结算账号是否为本行账号 Y-本行结算账号 N-他行结算账号',
SETTLE_ACCT IS '结算账号',
SETTLE_ACCT_NM IS '结算账户名称',
SETTLE_BANK_ALL_NAME IS '结算账号的收款行行名',
SETTLE_BANK_CODE IS '结算账号的收款行行号',
SETTLE_CYCLE is '结算周期,1=T1,2=D1',
RZTP IS '入账类型，1或者空=普通商入账,2=分账子商户入账,3=清算入平台商户账  ,4=平台商户利差入账'
);


