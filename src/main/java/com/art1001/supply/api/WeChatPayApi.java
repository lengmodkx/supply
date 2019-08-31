package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.wechat.WechatParam;
import com.art1001.supply.util.WeixinPay;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description
 * @Date:2019/8/31 11:09
 * @Author heshaohua
 **/
@Controller
@RequestMapping("wechat_pay")
public class WeChatPayApi {

    @RequestMapping("/wxPay")
    public String wxPay() throws Exception {
        WechatParam ps = new WechatParam();
        ps.setBody("测试商品3");
        ps.setTotalFee("1");
        ps.setOutTradeNo("hw5409550792199899");
        ps.setAttach("xiner");
        ps.setMemberid("888");
        String urlCode = WeixinPay.getCodeUrl(ps);
        System.out.println(urlCode);
        return "";
    }

    @RequestMapping("rollback")
    public JSONObject back(){
        System.out.println("收到回调请求");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", 1);
        return jsonObject;
    }
}
