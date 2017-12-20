package com.sunrun.bill.compare;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.sunrun.mpos.common.utils.SftpUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration(value = "src/main/webapp")
@ContextHierarchy({ @ContextConfiguration(name = "parent", locations = "classpath:config/bill-context.xml"),
        @ContextConfiguration(name = "child", locations = "classpath:config/bill-mvc.xml") })
public class UploadExcelTest {

    @Test
    public void uploadExcel() {
        SftpUtils sftpUtils = new SftpUtils("192.168.1.180", Integer.valueOf("22"), "dk", "");
        try {
            // sftpUtils.connect();
            sftpUtils.connectRSA();
            sftpUtils.upload("/upload/result/ws/20170908", "E:\\shengrun\\doc\\test\\6100000000000001_20170908.csv");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sftpUtils.logOut();
        }
    }
}
