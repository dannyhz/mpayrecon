--DROP TABLE MPOS.BAT2_CMP_MPOS_DTL;

CREATE TABLE MPOS.BAT2_CMP_MPOS_DTL
(
ID	DEC(20,0)	NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1,INCREMENT BY 1,CACHE 100),
SYSTM	TIMESTAMP	with default CURRENT_TIMESTAMP,
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
CLDT VARCHAR(50) with default '',
BNO VARCHAR(50) ,
TRADE_TYPE  VARCHAR(50) ,
MCH_ORDER_ID	VARCHAR(50),
REL_MCH_ORDER_ID VARCHAR(50),
FZFG CHAR(1),  
MY_MCH_RATE DEC(10,4),
MY_SEC_MCH_RATE DEC(10,4),
D0FG CHAR(1),
TRADE_CODE VARCHAR(50),	--TXN_SUB_TYPE
PAYBANK VARCHAR(50),	--TPAM_BANK_ID
BANK_CODE VARCHAR(32) DEFAULT '',
 primary key (ID)
)
;

CREATE unique INDEX MPOS.BAT2_CMP_MPOS_DTL_IDX_1 ON MPOS.BAT2_CMP_MPOS_DTL(MY_ORDER_ID);

COMMENT ON TABLE MPOS.BAT2_CMP_MPOS_DTL IS '我方交易对账表';

COMMENT ON MPOS.BAT2_CMP_MPOS_DTL ( 
ID is 'ID',
SYSTM is '入库时间戳',
TRTM is '交易时间',
CHANNEL_NO is '渠道',
CHL_ORDER_ID is '渠道方订单号',
MY_ORDER_ID is '我方订单号',
MCH_NO is '渠道方商户号',
SEC_MCH_NO is '渠道方二级商户号',
TRAM is '金额',
TRTP is '交易类型',
REL_ORDER_ID IS '相关流水号，用于处理退款、撤销等业务',
MEMO is '交易备注',
BRH_ID is '机构号',
MY_MCH_NO is '我方一级商户号,对应渠道二级商户号',
MY_SEC_MCH_NO is '我方二级商户号',
TERM_NO IS '终端号',
CLDT IS '清算日期',
BNO IS '入库批次号（与BATID不同）',
TRADE_TYPE IS '交易方式，如JSAPI、NATIVE、MICROPAY',
MCH_ORDER_ID is '商户订单号',
REL_MCH_ORDER_ID is '商户相关订单号（预留，暂无数据）',
FZFG is '分账标记 N-不分帐 Y-分账',
MY_MCH_RATE IS '分账模式下为平台商户费率，普通模式下为普通商户费率',
MY_SEC_MCH_RATE IS '分账模式下为分账子商户费率，普通模式下为空',
D0FG IS 'D0标记，0-成功，1-余额不足，2-其他失败，3-状态未知,X-非D0,4-受理超时，5-已受理，结果未知',
TRADE_CODE IS '交易码',
PAYBANK IS '付款银行',
BANK_CODE IS '银行编号'
);
