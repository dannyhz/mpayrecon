
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

CREATE UNIQUE INDEX MPOS.BAT2_MCH_FEE_CLEAR_IDX_1  ON MPOS.BAT2_MCH_FEE_CLEAR (BATID,MY_SEC_MCH_NO,MY_MCH_NO,CHANNEL_NO,RZTP);	--��֤ÿ���ε��̻�������ֻ����һ��

COMMENT ON TABLE MPOS.BAT2_MCH_FEE_CLEAR IS '�̻������ѻ��ܱ�';

COMMENT ON MPOS.BAT2_MCH_FEE_CLEAR ( 
ID is 'ID',
SYSTM is '���ʱ���',
BATID is '�������κ�,ÿ��ֻ��һ�����εĶ������ݻ���',
SMDT is '��������',
MY_MCH_NO is '�ҷ�һ���̻���,��Ӧ���������̻���',
MY_SEC_MCH_NO is '�ҷ������̻��ţ�RZTP=3ʱΪƽ̨�̻��ţ�',
CHANNEL_NO is '����',
TCNT IS '�ܱ���',
TTRAM IS '������',
ACNT IS '�����ױ���',
ATRAM IS '�������ܽ��',
RCNT IS '�����ױ���',
RTRAM IS '�������ܽ��',
COST is '�����ɱ�',
TOTAL_FEE is '��������',
RZAMT is '�̻����˽��',
RZST IS '����״̬ 0=��� 1=������',
RZDT IS '��������',
RZFN IS '�����ļ���',
MCH_NO IS '�������̻���(��Ψһ������)',
SEC_MCH_NM IS '�ҷ������̻�����',
SETTLE_BANK_FLAG IS '�����˺��Ƿ�Ϊ�����˺� Y-���н����˺� N-���н����˺�',
SETTLE_ACCT IS '�����˺�',
SETTLE_ACCT_NM IS '�����˻�����',
SETTLE_BANK_ALL_NAME IS '�����˺ŵ��տ�������',
SETTLE_BANK_CODE IS '�����˺ŵ��տ����к�',
SETTLE_CYCLE is '��������,1=T1,2=D1',
RZTP IS '�������ͣ�1���߿�=��ͨ������,2=�������̻�����,3=������ƽ̨�̻��� ,4=ƽ̨�̻���������'
);









--����汾
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

CREATE UNIQUE INDEX MPOS.BAT2_MCH_FEE_CLEAR_IDX_1  ON MPOS.BAT2_MCH_FEE_CLEAR (BATID,MY_SEC_MCH_NO,MY_MCH_NO,CHANNEL_NO,RZTP);	--��֤ÿ���ε��̻�������ֻ����һ��
CREATE INDEX MPOS.BAT2_MCH_FEE_CLEAR_IDX_2 ON MPOS.BAT2_MCH_FEE_CLEAR(SMDT);	

COMMENT ON TABLE MPOS.BAT2_MCH_FEE_CLEAR IS '�̻������ѻ��ܱ�';

COMMENT ON MPOS.BAT2_MCH_FEE_CLEAR ( 
ID is 'ID',
SYSTM is '���ʱ���',
BATID is '�������κ�,ÿ��ֻ��һ�����εĶ������ݻ���',
SMDT is '��������',
MY_MCH_NO is '�ҷ�һ���̻���,��Ӧ���������̻���',
MY_SEC_MCH_NO is '�ҷ������̻��ţ�RZTP=3ʱΪƽ̨�̻��ţ�',
CHANNEL_NO is '����',
TCNT IS '�ܱ���',
TTRAM IS '������',
ACNT IS '�����ױ���',
ATRAM IS '�������ܽ��',
RCNT IS '�����ױ���',
RTRAM IS '�������ܽ��',
COST is '�����ɱ�',
TOTAL_FEE is '��������',
RZAMT is '�̻����˽��',
RZST IS '����״̬ 0=��� 1=������',
RZDT IS '��������',
RZFN IS '�����ļ���',
MCH_NO IS '�������̻���(��Ψһ������)',
SEC_MCH_NM IS '�ҷ������̻�����',
SETTLE_BANK_FLAG IS '�����˺��Ƿ�Ϊ�����˺� Y-���н����˺� N-���н����˺�',
SETTLE_ACCT IS '�����˺�',
SETTLE_ACCT_NM IS '�����˻�����',
SETTLE_BANK_ALL_NAME IS '�����˺ŵ��տ�������',
SETTLE_BANK_CODE IS '�����˺ŵ��տ����к�',
SETTLE_CYCLE is '��������,1=T1,2=D1',
RZTP IS '�������ͣ�1���߿�=��ͨ������,2=�������̻�����,3=������ƽ̨�̻���  ,4=ƽ̨�̻���������'
);

