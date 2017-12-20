package com.sunrun.bill.compare;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sunrun.bill.db.DefaultDbOpt;
import com.sunrun.bill.exception.BillDbOptException;
import com.sunrun.bill.exception.BillFileOptException;
import com.sunrun.bill.file.YzfFileOpt;
import com.sunrun.bill.file.ZjFileOpt;
import com.sunrun.mpos.common.utils.DateUtils;
import com.sunrun.mpos.common.utils.PropertyUtils;

public class CompareTest {

    // @Ignore
    @Test
    public void test_bestpay_compareFileAndDb() throws BillDbOptException, ParseException, BillFileOptException {

        ApplicationContext act = new ClassPathXmlApplicationContext(new String[] { "config/bill-context.xml" });

        DefaultDbOpt dbOpt = (DefaultDbOpt) act.getBean("yzfDbOpt");

        HashMap dbDataSourceMap = dbOpt.queryDbMap(DateUtils.parseDate("2017-09-05"), "bestpay");

        YzfFileOpt yzfFileOpt = (YzfFileOpt) act.getBean("yzfFileOpt");
        Map fileSource = yzfFileOpt.queryFileList(DateUtils.parseDate("2017-09-05"));

        List<byte[]> list = (List<byte[]>) fileSource.get("dataList");

        Compare compare = new Compare();
        // Object dbObject,Object fileObject,String fileName,String
        // _zjFlag,String _yzfFlag,Date date,String _channelCode
        Method method = BeanUtils.findDeclaredMethod(Compare.class, "compareFileAndDb", Object.class, Object.class,
            String.class, String.class, String.class, Date.class, String.class);
        method.setAccessible(true);

        String _yzfFileName = PropertyUtils.getValue("com.sunrun.bill.yzf.fileName");

        // 翼支付控制文件FLAG
        String _yzfFlag = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.yzf.controlFlag"),
            DateUtils.formatDate(DateUtils.parseDate("2017-09-05"), "yyyyMMdd"));

        String _yzfChannelCode = PropertyUtils.getValue("com.sunrun.bill.yzf.channelCode");

        try {
            method.invoke(compare, dbDataSourceMap, list, _yzfFileName, "_zjFlag", _yzfFlag,
                DateUtils.parseDate("2017-09-05"), _yzfChannelCode);

        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test
    public void test_cpcn_compareFileAndDb() throws BillDbOptException, ParseException, BillFileOptException {

        ApplicationContext act = new ClassPathXmlApplicationContext(new String[] { "config/bill-context.xml" });

        DefaultDbOpt dbOpt = (DefaultDbOpt) act.getBean("zjDbOpt");

        // 中金数据库对账
        HashMap dbDataSourceMap = dbOpt.queryDbMap(DateUtils.parseDate("2017-09-05"), "cpcn");

        ZjFileOpt zjFileOpt = (ZjFileOpt) act.getBean("zjFileOpt");
        Map fileDataSourceNap = zjFileOpt.queryFileList(DateUtils.parseDate("2017-09-05"));
        List<byte[]> fileList = (List<byte[]>) fileDataSourceNap.get("dataList");

        Compare compare = new Compare();
        Method method = BeanUtils.findDeclaredMethod(Compare.class, "compareFileAndDb", Object.class, Object.class,
            String.class, String.class, String.class, Date.class, String.class);
        method.setAccessible(true);

        String _zjFileName = PropertyUtils.getValue("com.sunrun.bill.zj.fileName");
        // 中金控制文件FLAG
        String _zjFlag = MessageFormat.format(PropertyUtils.getValue("com.sunrun.bill.zj.controlFlag"),
            DateUtils.formatDate(DateUtils.parseDate("2017-09-05"), "yyyyMMdd"));

        // 中金渠道号
        String _zjChannelCode = PropertyUtils.getValue("com.sunrun.bill.zj.channelCode");

        try {
            method.invoke(compare, dbDataSourceMap, fileList, _zjFileName, _zjFlag, "_yzfFlag",
                DateUtils.parseDate("2017-09-05"), _zjChannelCode);

        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // @Test
    public void when_head_line_as_input_for_handleLine() {

        ApplicationContext act = new ClassPathXmlApplicationContext(new String[] { "config/bill-context.xml" });

        String headLine = "002690,298510218,301,282829108,285,15681110,16,282829108,42750";
        byte[] arr = new ZjFileOpt().handleLine(headLine);
        System.out.println(arr.length);
        System.out.println(arr[0] + "-" + arr[1] + "- " + arr[2]);
    }

}
