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

COMMENT ON TABLE MPOS.BAT2_CMP_MPOS_DTL IS '�ҷ����׶��˱�';

COMMENT ON MPOS.BAT2_CMP_MPOS_DTL ( 
ID is 'ID',
SYSTM is '���ʱ���',
TRTM is '����ʱ��',
CHANNEL_NO is '����',
CHL_ORDER_ID is '������������',
MY_ORDER_ID is '�ҷ�������',
MCH_NO is '�������̻���',
SEC_MCH_NO is '�����������̻���',
TRAM is '���',
TRTP is '��������',
REL_ORDER_ID IS '�����ˮ�ţ����ڴ����˿������ҵ��',
MEMO is '���ױ�ע',
BRH_ID is '������',
MY_MCH_NO is '�ҷ�һ���̻���,��Ӧ���������̻���',
MY_SEC_MCH_NO is '�ҷ������̻���',
TERM_NO IS '�ն˺�',
CLDT IS '��������',
BNO IS '������κţ���BATID��ͬ��',
TRADE_TYPE IS '���׷�ʽ����JSAPI��NATIVE��MICROPAY',
MCH_ORDER_ID is '�̻�������',
REL_MCH_ORDER_ID is '�̻���ض����ţ�Ԥ�����������ݣ�',
FZFG is '���˱�� N-������ Y-����',
MY_MCH_RATE IS '����ģʽ��Ϊƽ̨�̻����ʣ���ͨģʽ��Ϊ��ͨ�̻�����',
MY_SEC_MCH_RATE IS '����ģʽ��Ϊ�������̻����ʣ���ͨģʽ��Ϊ��',
D0FG IS 'D0��ǣ�0-�ɹ���1-���㣬2-����ʧ�ܣ�3-״̬δ֪,X-��D0,4-������ʱ��5-�����������δ֪',
TRADE_CODE IS '������',
PAYBANK IS '��������',
BANK_CODE IS '���б��'
);