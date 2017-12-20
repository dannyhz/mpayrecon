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

COMMENT ON TABLE MPOS.BAT2_DMCH_CHL_SUM IS 'ֱ���̻����׻��ܱ�';
	
COMMENT ON MPOS.BAT2_DMCH_CHL_SUM ( 
	ID is 'ID',
	SYSTM is '���ʱ���',
	BATID IS 'BATID',
	CALDT IS '��������',
	TRDT is '��������',
	CHANNEL_NO is '����',
	MCH_NO is '�������̻���',
	MY_MCH_NO is '�ҷ�һ���̻���(Ԥ��)',
	TCNT IS '�ܱ���',
	TTRAM IS '������',
	ACNT IS '�����ױ���',
	ATRAM IS '�������ܽ��',
	RCNT IS '�����ױ���',
	RTRAM IS '�������ܽ��',
	COST is '�����ɱ�',
	TOTAL_FEE is '��������',
	BANK_PROFIT_FEE is 'Ӫ���з���',
	AGT_PROFIT_FEE is '��������',
	RZAMT is '�̻����˽��',
	RZST IS '����״̬ 0=��� 1=������',
	RZDT IS '��������',
	MEMO is '��ע'
	);
	
	
--����	
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

COMMENT ON TABLE MPOS.BAT2_DMCH_CHL_SUM IS 'ֱ���̻����׻��ܱ�';
	
COMMENT ON MPOS.BAT2_DMCH_CHL_SUM ( 
	ID is 'ID',
	SYSTM is '���ʱ���',
	BATID IS 'BATID',
	CALDT IS '��������',
	TRDT is '��������',
	CHANNEL_NO is '����',
	MCH_NO is '�������̻���',
	MY_MCH_NO is '�ҷ�һ���̻���(Ԥ��)',
	TCNT IS '�ܱ���',
	TTRAM IS '������',
	ACNT IS '�����ױ���',
	ATRAM IS '�������ܽ��',
	RCNT IS '�����ױ���',
	RTRAM IS '�������ܽ��',
	COST is '�����ɱ�',
	TOTAL_FEE is '��������',
	BANK_PROFIT_FEE is 'Ӫ���з���',
	AGT_PROFIT_FEE is '��������',
	RZAMT is '�̻����˽��',
	RZST IS '����״̬ 0=��� 1=������',
	RZDT IS '��������',
	MEMO is '��ע'
	);

--nickname
CREATE OR REPLACE NICKNAME MPOS.NK_BAT2_DMCH_CHL_SUM
  FOR SVRWR.MPOS.BAT2_DMCH_CHL_SUM;		