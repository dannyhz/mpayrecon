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
	
	COMMENT ON TABLE MPOS.BAT2_DMCH_CHL_DTL IS 'ֱ���̻������嵥��(Դ�����������ļ�)';
	
	COMMENT ON MPOS.BAT2_DMCH_CHL_DTL ( 
	ID is 'ID',
	UKEY is '���ڱ�������Ψһ',
	SYSTM is '���ʱ���',
	BATID IS 'BATID',
	CALDT IS '��������',
	TRDT is '��������',
	TRTM is '����ʱ��',
	CHANNEL_NO is '����',
	CHL_ORDER_ID is '������������',
	MCH_ORDER_ID is '�̻�������',
	MCH_NO is '�������̻���',
	MY_MCH_NO is '�ҷ�һ���̻���(Ԥ��)',
	TRAM is '���',
	TRTP is '��������',
	MEMO is '���ױ�ע',
	PAYBANK IS '��������',
	TRADE_TYPE IS '���׷�ʽ����JSAPI��NATIVE��MICROPAY',
	USERID is '�û����',
	TR_RESP is '���׽��',
	REFUND_ORDER_ID	is '�˿����',
	REFUND_TRAM is '�˿���',
	REFUND_RESP is '�˿�׽��',
	COST_RATE is '�����ɱ�����',
	COST is '�����ɱ�',
	TOTAL_RATE is '���̻���ȡ����',
	TOTAL_FEE is '�̻�������',
	BANK_PROFIT_RATE is 'Ӫ���з������',
	BANK_PROFIT_FEE is 'Ӫ���з���',
	RZAMT is '�̻����˽��',
	AGT_PROFIT_FEE is '��������'
	);
	
	
	
--����
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
	
	COMMENT ON TABLE MPOS.BAT2_DMCH_CHL_DTL IS 'ֱ���̻������嵥��(Դ�����������ļ�)';
	
	COMMENT ON MPOS.BAT2_DMCH_CHL_DTL ( 
	ID is 'ID',
	UKEY is '���ڱ�������Ψһ',
	SYSTM is '���ʱ���',
	BATID IS 'BATID',
	CALDT IS '��������',
	TRDT is '��������',
	TRTM is '����ʱ��',
	CHANNEL_NO is '����',
	CHL_ORDER_ID is '������������',
	MCH_ORDER_ID is '�̻�������',
	MCH_NO is '�������̻���',
	MY_MCH_NO is '�ҷ�һ���̻���(Ԥ��)',
	TRAM is '���',
	TRTP is '��������',
	MEMO is '���ױ�ע',
	PAYBANK IS '��������',
	TRADE_TYPE IS '���׷�ʽ����JSAPI��NATIVE��MICROPAY',
	USERID is '�û����',
	TR_RESP is '���׽��',
	REFUND_ORDER_ID	is '�˿����',
	REFUND_TRAM is '�˿���',
	REFUND_RESP is '�˿�׽��',
	COST_RATE is '�����ɱ�����',
	COST is '�����ɱ�',
	TOTAL_RATE is '���̻���ȡ����',
	TOTAL_FEE is '�̻�������',
	BANK_PROFIT_RATE is 'Ӫ���з������',
	BANK_PROFIT_FEE is 'Ӫ���з���',
	RZAMT is '�̻����˽��',
	AGT_PROFIT_FEE is '��������'
	);
	
--nickname
CREATE OR REPLACE NICKNAME MPOS.NK_BAT2_DMCH_CHL_DTL
  FOR SVRWR.MPOS.BAT2_DMCH_CHL_DTL;	