package com.art1001.supply.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.entity.wechat.WechatParam;
import com.art1001.supply.service.order.OrderService;
import com.art1001.supply.service.product.ProductService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * @Description
 * @Date:2019/8/31 11:09
 * @Author heshaohua
 **/
@Slf4j
@RestController
@RequestMapping("wechat_pay")
public class WeChatPayApi extends BaseController {

    @Resource
    private ProductService productService;

    @Resource
    private OrderService orderService;

    @Push(value = PushType.X1,type = 1)
    @GetMapping("/wxPay")
    public JSONObject wxPay(WechatParam ps,HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        ps.setTotalFee("1");
        ps.setOutTradeNo(IdGen.uuid());
        String urlCode = WeixinPay.getCodeUrl(ps);
        WeixinPay.encodeQrcode(urlCode,response);
        jsonObject.put("msgId", ShiroAuthenticationManager.getUserId());
        jsonObject.put("data", 1);
        return jsonObject;
    }

    @RequestMapping("rollback")
    public JSONObject back(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("收到微信支付回调请求");
        //读取参数
        InputStream inputStream;
        StringBuffer sb = new StringBuffer();
        inputStream = request.getInputStream();
        String s;
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        while ((s = in.readLine()) != null) {
            sb.append(s);
        }
        in.close();
        inputStream.close();

        //解析xml成map
        Map<String, String> m = new HashMap<String, String>();
        m = XMLUtil.doXMLParse(sb.toString());

        //过滤空 设置 TreeMap
        SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
        Iterator<String> it = m.keySet().iterator();
        while (it.hasNext()) {
            String parameter = it.next();
            String parameterValue = m.get(parameter);

            String v = "";
            if (null != parameterValue) {
                v = parameterValue.trim();
            }
            packageParams.put(parameter, v);
        }
        // 微信支付的API密钥
        String key = WeChatConfig.APIKEY; // key

        log.info("微信支付返回回来的参数：" + packageParams);
        //判断签名是否正确
        if (PayForUtil.isTenpaySign("UTF-8", packageParams, key)) {
            //------------------------------
            //处理业务开始
            //------------------------------
            String resXml = "";
            if ("SUCCESS".equals((String) packageParams.get("result_code"))) {
                // 生成订单
                int isOrder = orderService.generateOrder(packageParams);
                int authorization = productService.authorization(packageParams);

                //执行自己的业务逻辑结束
                log.info("支付成功");
                //通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.
                resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                        + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";

            } else {
                log.info("支付失败,错误信息：" + packageParams.get("err_code"));
                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                        + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            }
            //------------------------------
            //处理业务完毕
            //------------------------------
            BufferedOutputStream out = new BufferedOutputStream(
                    response.getOutputStream());
            out.write(resXml.getBytes());
            out.flush();
            out.close();
        } else {
            log.info("通知签名验证失败");
        }
        return success();
    }
}
