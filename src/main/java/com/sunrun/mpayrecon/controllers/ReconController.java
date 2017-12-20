package com.sunrun.mpayrecon.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sunrun.bill.compare.ChannelBill;
import com.sunrun.bill.service.IMerchantService;

/**
 * 对账控制类.
 *
 * @author zhuxiang
 * @since V1.0.0
 */
@Controller
public class ReconController {
    private static final Logger logger = LoggerFactory.getLogger(ReconController.class);

    @Autowired
    private List<ChannelBill> channelBillList;

    @Autowired
    private IMerchantService merchantService;

    
    @ResponseBody
    @RequestMapping("/recon")
    public void execute(HttpServletRequest req) {
       

    }

   
}
