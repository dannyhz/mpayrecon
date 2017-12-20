--call MPOS.SP_MLK_SUM('384984159660',?)
--DROP PROCEDURE "MPOS"."SP_MLK_SUM";
CREATE OR REPLACE PROCEDURE "MPOS"."SP_MLK_SUM" (
    IN "I_BATID"	DECFLOAT(16),	--导入批次号
    OUT "O_ERRMSG"	VARCHAR(128)
)
LANGUAGE SQL
BEGIN 
/*=====================================================================+           
  功能描述：玫琳凯直连商户账单汇总
            
  编写人员：phio
  设计日期：2017/02/20
  修改历史：
  修改日期  修改人员   修改原因
  
-----------------------------------------------------------------------*/

--------------------------常用变量定义--------------------------
DECLARE IRET INTEGER DEFAULT 0 ;
--定义SQL异常警告信息捕捉变量
DECLARE SQLCODE INT DEFAULT 0 ;		        --SQL返回代码
DECLARE SQLSTATE CHAR(5) DEFAULT '00000' ;--SQL默认返回代码'00000'，返回成功
--------------------------应用变量定义---------------------------


----------------------------------异常日志处理-------------------------
--SQL产生的非上述异常的处理
DECLARE EXIT HANDLER FOR SQLEXCEPTION 
BEGIN
	SET O_ERRMSG = O_ERRMSG ||',SQLCODE='||TRIM(CHAR(SQLCODE))||',SQLSTATE='||SQLSTATE;	
	--ROLLBACK ;
END;

SET O_ERRMSG = '-1';
--锁表，最大化性能
LOCK TABLE MPOS.BAT2_DMCH_CHL_DTL IN EXCLUSIVE MODE;

SET O_ERRMSG = '-2';
--进行
INSERT INTO MPOS.BAT2_DMCH_CHL_SUM (BATID, CALDT, TRDT, CHANNEL_NO, MCH_NO, MY_MCH_NO, TCNT, TTRAM, ACNT, ATRAM, RCNT, RTRAM, COST, 
		TOTAL_FEE, AGT_PROFIT_FEE, BANK_PROFIT_FEE, RZAMT, RZST, RZDT, MEMO)
	SELECT BATID,CALDT,TRDT,CHANNEL_NO,MCH_NO,MY_MCH_NO,
		COUNT(1), --总笔数
		SUM(TRAM - REFUND_TRAM), --轧差金额
	  SUM(CASE WHEN TRTP = '01' THEN 1 ELSE 0 END),	--正交易笔数
	  SUM(TRAM), --正交易总金额
		SUM(CASE WHEN TRTP IN ('04','21') THEN 1 ELSE 0 END), --反交易笔数
	  SUM(REFUND_TRAM), --反交易总金额
	  SUM(COST),	--渠道成本
	  SUM(TOTAL_FEE),	--总手续费
	  SUM(AGT_PROFIT_FEE),	--代理分润
	  SUM(BANK_PROFIT_FEE),	--营销行分润
	  SUM(RZAMT),	--商户入账金额
		1,0,'' FROM MPOS.BAT2_DMCH_CHL_DTL WHERE BATID=I_BATID
		GROUP BY TRDT,BATID,CALDT,CHANNEL_NO,MCH_NO,MY_MCH_NO;

COMMIT;
SET O_ERRMSG = '0';

END;
