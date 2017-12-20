--ÿ�������������һ����շ���
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


--CREATE UNIQUE INDEX MPOS.BAT2_CMP_RESULT_IDX_1 ON MPOS.BAT2_CMP_RESULT(SC_CSTRT);	--��֤�ɹ����˵Ķ�����ֻ����һ�Σ�������֤�̼һ���ֻ����һ��
--CREATE INDEX MPOS.BAT2_CMP_RESULT_IDX_2 ON MPOS.BAT2_CMP_RESULT(BATID);
CREATE UNIQUE INDEX MPOS.BAT2_CMP_RESULT_IDX_3 ON MPOS.BAT2_CMP_RESULT(MY_ORDER_ID);

COMMENT ON TABLE MPOS.BAT2_CMP_RESULT IS '�������˽���ɹ���';

COMMENT ON MPOS.BAT2_CMP_RESULT ( 
ID is 'ID',
SYSTM is '���ʱ���',
BATID is '�������κ�',
CLDT is '��������,��Դ�ڶ���֧����ɵ���������',
SRC IS '��Դ CHL-MPOS-BOTH',
TRTM is '����ʱ��',
CHANNEL_NO is '����',
CHL_ORDER_ID is '������������',
MY_ORDER_ID is '�ҷ�������',
MCH_NO is '�����̻���',
SEC_MCH_NO is '���������̻���',
TRAM is '���',
TRTP is '��������',
REL_ORDER_ID IS '�����ˮ�ţ����ڴ����˿������ҵ��',
MEMO is '���ױ�ע',
BRH_ID is '������',
MY_MCH_NO is '�ҷ�һ���̻���,��Ӧ���������̻���',
MY_SEC_MCH_NO is '�ҷ������̻���',
TERM_NO IS '�ն˺�',
COST_RATE is '�����ɱ�����',
TOTAL_RATE is '���̻���ȡ����',
COST is '�����ɱ�',
TOTAL_FEE is '�̻�������',
RZAMT is '�̻����˽��',
CKTURN is '�����ִ�',
CKFG is '���˱��',
CKDT is '��������(������)',
TRADE_TYPE IS '���׷�ʽ����JSAPI��NATIVE��MICROPAY',
MCH_ORDER_ID is '�̻�������',
REL_MCH_ORDER_ID is '�̻���ض����ţ�Ԥ�����������ݣ�',
PAYBANK IS '��������',
FZFG is '���˱�� N-������ Y-���� D-����',
MY_PTMCH_RATE IS 'ƽ̨�̻��������',
MY_PTMCH_FEE IS 'ƽ̨�̻�������',
D0FG IS 'D0��ǣ�0-�ɹ���1-���㣬2-����ʧ�ܣ�3-״̬δ֪,X-��D0',
TRADE_CODE IS '������',
BANK_CODE IS '���б��'
);







--����汾
--ÿ�������������һ����շ���
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

COMMENT ON TABLE MPOS.BAT2_CMP_RESULT IS '�������˽���ɹ���';

COMMENT ON MPOS.BAT2_CMP_RESULT ( 
ID is 'ID',
SYSTM is '���ʱ���',
BATID is '�������κ�',
CLDT is '��������,��Դ�ڶ���֧����ɵ���������',
SRC IS '��Դ CHL-MPOS-BOTH',
TRTM is '����ʱ��',
CHANNEL_NO is '����',
CHL_ORDER_ID is '������������',
MY_ORDER_ID is '�ҷ�������',
MCH_NO is '�����̻���',
SEC_MCH_NO is '���������̻���',
TRAM is '���',
TRTP is '��������',
REL_ORDER_ID IS '�����ˮ�ţ����ڴ����˿������ҵ��',
MEMO is '���ױ�ע',
BRH_ID is '������',
MY_MCH_NO is '�ҷ�һ���̻���,��Ӧ���������̻���',
MY_SEC_MCH_NO is '�ҷ������̻���',
TERM_NO IS '�ն˺�',
COST_RATE is '�����ɱ�����',
TOTAL_RATE is '���̻���ȡ����',
COST is '�����ɱ�',
TOTAL_FEE is '�̻�������',
RZAMT is '�̻����˽��',
CKTURN is '�����ִ�',
CKFG is '���˱��',
CKDT is '��������(������)',
TRADE_TYPE IS '���׷�ʽ����JSAPI��NATIVE��MICROPAY',
MCH_ORDER_ID is '�̻�������',
REL_MCH_ORDER_ID is '�̻���ض����ţ�Ԥ�����������ݣ�',
PAYBANK IS '��������',
FZFG is '���˱�� N-������ Y-����',
MY_PTMCH_RATE IS 'ƽ̨�̻��������',
MY_PTMCH_FEE IS 'ƽ̨�̻�������',
D0FG IS 'D0��ǣ�0-�ɹ���1-���㣬2-����ʧ�ܣ�3-״̬δ֪,X-��D0,4-D0�����쳣',
TRADE_CODE IS '������',
BANK_CODE IS '���б��'
);