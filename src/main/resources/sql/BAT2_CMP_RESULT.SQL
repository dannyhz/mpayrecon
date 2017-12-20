--每年检查分区，增加一年的日分区
drop table MPOS.BAT2_CMP_RESULT;
CREATE TABLE MPOS.BAT2_CMP_RESULT
(
ID	DEC(20,0)	NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1,INCREMENT BY 1,CACHE 100),
SYSTM	TIMESTAMP	with default CURRENT_TIMESTAMP,
BATID DEC(20),
CLDT DATE,
SRC VARCHAR(10),
TRTM VARCHAR(30) with default '',
CHANNEL_NO	VARCHAR(50) with default '',
CHL_ORDER_ID	VARCHAR(50) with default '',
MY_ORDER_ID	VARCHAR(50) with default '',
MCH_NO	VARCHAR(50) with default '',
SEC_MCH_NO	VARCHAR(50) with default '',
TRAM	DEC(15,2) with default 0,
TRTP	VARCHAR(50) with default '',
REL_ORDER_ID VARCHAR(50) with default '',
MEMO	VARCHAR(250) with default '',
BRH_ID VARCHAR(50) with default '',
MY_MCH_NO VARCHAR(50) with default '',
MY_SEC_MCH_NO VARCHAR(50) with default '',
TERM_NO VARCHAR(50) with default '',
COST_RATE	VARCHAR(12) with default '0',
TOTAL_RATE	VARCHAR(12) with default '0',
COST	DEC(15,2) with default 0,
TOTAL_FEE	DEC(15,2) with default 0,
RZAMT	DEC(15,2) with default 0,
CKTURN INT,
CKFG INT,
CKDT DATE,
TRADE_TYPE  VARCHAR(50) ,
MCH_ORDER_ID	VARCHAR(50),
REL_MCH_ORDER_ID VARCHAR(50),
--SC_CSTRT	VARCHAR(50)	GENERATED ALWAYS AS (CASE WHEN CKFG=0 THEN MY_ORDER_ID ELSE SRC||VARCHAR(ID) END),
PAYBANK VARCHAR(50),
FZFG CHAR(1),
MY_PTMCH_RATE DEC(10,4),
MY_PTMCH_FEE DEC(15,2),
D0FG CHAR(1),
TRADE_CODE VARCHAR(50),
BANK_CODE VARCHAR(32) DEFAULT '302331033102',
 primary key (ID)
)
compress yes
ORGANIZE BY (CKDT,BATID)
;


--CREATE UNIQUE INDEX MPOS.BAT2_CMP_RESULT_IDX_1 ON MPOS.BAT2_CMP_RESULT(SC_CSTRT);	--保证成功对账的订单号只出现一次，进而保证商家汇总只汇总一次
--CREATE INDEX MPOS.BAT2_CMP_RESULT_IDX_2 ON MPOS.BAT2_CMP_RESULT(BATID);
CREATE UNIQUE INDEX MPOS.BAT2_CMP_RESULT_IDX_3 ON MPOS.BAT2_CMP_RESULT(MY_ORDER_ID);

COMMENT ON TABLE MPOS.BAT2_CMP_RESULT IS '渠道对账结果成功表';

COMMENT ON MPOS.BAT2_CMP_RESULT ( 
ID is 'ID',
SYSTM is '入库时间戳',
BATID is '对账批次号',
CLDT is '清算日期,来源于订单支付完成的渠道日期',
SRC IS '来源 CHL-MPOS-BOTH',
TRTM is '交易时间',
CHANNEL_NO is '渠道',
CHL_ORDER_ID is '渠道方订单号',
MY_ORDER_ID is '我方订单号',
MCH_NO is '渠道商户号',
SEC_MCH_NO is '渠道二级商户号',
TRAM is '金额',
TRTP is '交易类型',
REL_ORDER_ID IS '相关流水号，用于处理退款、撤销等业务',
MEMO is '交易备注',
BRH_ID is '机构号',
MY_MCH_NO is '我方一级商户号,对应渠道二级商户号',
MY_SEC_MCH_NO is '我方二级商户号',
TERM_NO IS '终端号',
COST_RATE is '渠道成本费率',
TOTAL_RATE is '向商户收取费率',
COST is '渠道成本',
TOTAL_FEE is '商户手续费',
RZAMT is '商户入账金额',
CKTURN is '对账轮次',
CKFG is '对账标记',
CKDT is '对账日期(分区键)',
TRADE_TYPE IS '交易方式，如JSAPI、NATIVE、MICROPAY',
MCH_ORDER_ID is '商户订单号',
REL_MCH_ORDER_ID is '商户相关订单号（预留，暂无数据）',
PAYBANK IS '付款银行',
FZFG is '分账标记 N-不分帐 Y-分账 D-独立',
MY_PTMCH_RATE IS '平台商户利差费率',
MY_PTMCH_FEE IS '平台商户利差金额',
D0FG IS 'D0标记，0-成功，1-余额不足，2-其他失败，3-状态未知,X-非D0',
TRADE_CODE IS '交易码',
BANK_CODE IS '银行编号'
);







--主库版本
--每年检查分区，增加一年的日分区
--drop table MPOS.BAT2_CMP_RESULT;
CREATE TABLE MPOS.BAT2_CMP_RESULT
(
ID	DEC(20,0)	NOT NULL ,
SYSTM	TIMESTAMP	with default CURRENT_TIMESTAMP,
BATID DEC(20),
CLDT DATE,
SRC VARCHAR(10),
TRTM VARCHAR(30) with default '',
CHANNEL_NO	VARCHAR(50) with default '',
CHL_ORDER_ID	VARCHAR(50) with default '',
MY_ORDER_ID	VARCHAR(50) with default '',
MCH_NO	VARCHAR(50) with default '',
SEC_MCH_NO	VARCHAR(50) with default '',
TRAM	DEC(15,2) with default 0,
TRTP	VARCHAR(50) with default '',
REL_ORDER_ID VARCHAR(50) with default '',
MEMO	VARCHAR(250) with default '',
BRH_ID VARCHAR(50) with default '',
MY_MCH_NO VARCHAR(50) with default '',
MY_SEC_MCH_NO VARCHAR(50) with default '',
TERM_NO VARCHAR(50) with default '',
COST_RATE	VARCHAR(12) with default '0',
TOTAL_RATE	VARCHAR(12) with default '0',
COST	DEC(15,2) with default 0,
TOTAL_FEE	DEC(15,2) with default 0,
RZAMT	DEC(15,2) with default 0,
CKTURN INT,
CKFG INT,
CKDT DATE,
TRADE_TYPE  VARCHAR(50) ,
MCH_ORDER_ID	VARCHAR(50),
REL_MCH_ORDER_ID VARCHAR(50),
PAYBANK VARCHAR(50),
FZFG CHAR(1),
MY_PTMCH_RATE DEC(10,4),
MY_PTMCH_FEE DEC(15,2),
D0FG CHAR(1),
TRADE_CODE VARCHAR(50),
BANK_CODE VARCHAR(32) DEFAULT '',
 primary key (ID)
)PARTITION BY RANGE(CKDT)
(
STARTING MINVALUE,
STARTING '2016-08-01' ENDING '2018-01-01' EVERY 1 DAY,
ENDING MAXVALUE
);

CREATE INDEX MPOS.BAT2_CMP_RESULT_IDX_2 ON MPOS.BAT2_CMP_RESULT(BATID);
CREATE UNIQUE INDEX MPOS.BAT2_CMP_RESULT_IDX_3 ON MPOS.BAT2_CMP_RESULT(MY_ORDER_ID);
CREATE INDEX MPOS.BAT2_CMP_RESULT_IDX_5 ON MPOS.BAT2_CMP_RESULT(MY_MCH_NO);

COMMENT ON TABLE MPOS.BAT2_CMP_RESULT IS '渠道对账结果成功表';

COMMENT ON MPOS.BAT2_CMP_RESULT ( 
ID is 'ID',
SYSTM is '入库时间戳',
BATID is '对账批次号',
CLDT is '清算日期,来源于订单支付完成的渠道日期',
SRC IS '来源 CHL-MPOS-BOTH',
TRTM is '交易时间',
CHANNEL_NO is '渠道',
CHL_ORDER_ID is '渠道方订单号',
MY_ORDER_ID is '我方订单号',
MCH_NO is '渠道商户号',
SEC_MCH_NO is '渠道二级商户号',
TRAM is '金额',
TRTP is '交易类型',
REL_ORDER_ID IS '相关流水号，用于处理退款、撤销等业务',
MEMO is '交易备注',
BRH_ID is '机构号',
MY_MCH_NO is '我方一级商户号,对应渠道二级商户号',
MY_SEC_MCH_NO is '我方二级商户号',
TERM_NO IS '终端号',
COST_RATE is '渠道成本费率',
TOTAL_RATE is '向商户收取费率',
COST is '渠道成本',
TOTAL_FEE is '商户手续费',
RZAMT is '商户入账金额',
CKTURN is '对账轮次',
CKFG is '对账标记',
CKDT is '对账日期(分区键)',
TRADE_TYPE IS '交易方式，如JSAPI、NATIVE、MICROPAY',
MCH_ORDER_ID is '商户订单号',
REL_MCH_ORDER_ID is '商户相关订单号（预留，暂无数据）',
PAYBANK IS '付款银行',
FZFG is '分账标记 N-不分帐 Y-分账',
MY_PTMCH_RATE IS '平台商户利差费率',
MY_PTMCH_FEE IS '平台商户利差金额',
D0FG IS 'D0标记，0-成功，1-余额不足，2-其他失败，3-状态未知,X-非D0,4-D0发起异常',
TRADE_CODE IS '交易码',
BANK_CODE IS '银行编号'
);
