DROP TABLE MPOS.BAT2_CMP_CHL_DTL;

CREATE TABLE MPOS.BAT2_CMP_CHL_DTL
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
CLDT VARCHAR(50) with default '',
BNO VARCHAR(50) ,
PAYBANK VARCHAR(50),
 primary key (ID)
)
;

CREATE unique INDEX MPOS.BAT2_CMP_CHL_DTL_IDX_1 ON MPOS.BAT2_CMP_CHL_DTL(MY_ORDER_ID) ;

COMMENT ON TABLE MPOS.BAT2_CMP_CHL_DTL IS '渠道方交易对账表';

COMMENT ON MPOS.BAT2_CMP_CHL_DTL ( 
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
CLDT IS '清算日期',
BNO IS '入库批次号（与BATID不同）',
PAYBANK IS '付款银行'
);

