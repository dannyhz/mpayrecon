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

COMMENT ON TABLE MPOS.BAT2_CMP_CHL_DTL IS '���������׶��˱�';

COMMENT ON MPOS.BAT2_CMP_CHL_DTL ( 
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
CLDT IS '��������',
BNO IS '������κţ���BATID��ͬ��',
PAYBANK IS '��������'
);
