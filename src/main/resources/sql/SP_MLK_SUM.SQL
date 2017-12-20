--call MPOS.SP_MLK_SUM('384984159660',?)
--DROP PROCEDURE "MPOS"."SP_MLK_SUM";
CREATE OR REPLACE PROCEDURE "MPOS"."SP_MLK_SUM" (
    IN "I_BATID"	DECFLOAT(16),	--�������κ�
    OUT "O_ERRMSG"	VARCHAR(128)
)
LANGUAGE SQL
BEGIN 
/*=====================================================================+           
  ����������õ�տ�ֱ���̻��˵�����
            
  ��д��Ա��phio
  ������ڣ�2017/02/20
  �޸���ʷ��
  �޸�����  �޸���Ա   �޸�ԭ��
  
-----------------------------------------------------------------------*/

--------------------------���ñ�������--------------------------
DECLARE IRET INTEGER DEFAULT 0 ;
--����SQL�쳣������Ϣ��׽����
DECLARE SQLCODE INT DEFAULT 0 ;		        --SQL���ش���
DECLARE SQLSTATE CHAR(5) DEFAULT '00000' ;--SQLĬ�Ϸ��ش���'00000'�����سɹ�
--------------------------Ӧ�ñ�������---------------------------


----------------------------------�쳣��־����-------------------------
--SQL�����ķ������쳣�Ĵ���
DECLARE EXIT HANDLER FOR SQLEXCEPTION 
BEGIN
	SET O_ERRMSG = O_ERRMSG ||',SQLCODE='||TRIM(CHAR(SQLCODE))||',SQLSTATE='||SQLSTATE;	
	--ROLLBACK ;
END;

SET O_ERRMSG = '-1';
--�������������
LOCK TABLE MPOS.BAT2_DMCH_CHL_DTL IN EXCLUSIVE MODE;

SET O_ERRMSG = '-2';
--����
INSERT INTO MPOS.BAT2_DMCH_CHL_SUM (BATID, CALDT, TRDT, CHANNEL_NO, MCH_NO, MY_MCH_NO, TCNT, TTRAM, ACNT, ATRAM, RCNT, RTRAM, COST, 
		TOTAL_FEE, AGT_PROFIT_FEE, BANK_PROFIT_FEE, RZAMT, RZST, RZDT, MEMO)
	SELECT BATID,CALDT,TRDT,CHANNEL_NO,MCH_NO,MY_MCH_NO,
		COUNT(1), --�ܱ���
		SUM(TRAM - REFUND_TRAM), --������
	  SUM(CASE WHEN TRTP = '01' THEN 1 ELSE 0 END),	--�����ױ���
	  SUM(TRAM), --�������ܽ��
		SUM(CASE WHEN TRTP IN ('04','21') THEN 1 ELSE 0 END), --�����ױ���
	  SUM(REFUND_TRAM), --�������ܽ��
	  SUM(COST),	--�����ɱ�
	  SUM(TOTAL_FEE),	--��������
	  SUM(AGT_PROFIT_FEE),	--��������
	  SUM(BANK_PROFIT_FEE),	--Ӫ���з���
	  SUM(RZAMT),	--�̻����˽��
		1,0,'' FROM MPOS.BAT2_DMCH_CHL_DTL WHERE BATID=I_BATID
		GROUP BY TRDT,BATID,CALDT,CHANNEL_NO,MCH_NO,MY_MCH_NO;

COMMIT;
SET O_ERRMSG = '0';

END;