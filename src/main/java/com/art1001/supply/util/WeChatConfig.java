package com.art1001.supply.util;

/**
 * @Description
 * @Date:2019/8/31 10:47
 * @Author heshaohua
 **/
public class WeChatConfig {
    /**
     * 微信服务号APPID
     */
    public static String APPID="wx0c02b06426da953e";
    /**
     * 微信支付的商户号
     */
    public static String MCHID="1444343102";
    /**
     * 微信支付的API密钥
     */
    public static String APIKEY="yishuzhimen1001artbimcomYSZM1001";
    /**
     * 微信支付成功之后的回调地址【注意：当前回调地址必须是公网能够访问的地址】
     */
    public static String WECHAT_NOTIFY_URL_PC="47.93.138.137:8086/wechat_pay/rollback";
    /**
     * 微信统一下单API地址
     */
    public static String UFDODER_URL="https://api.mch.weixin.qq.com/pay/unifiedorder";
    /**
     * true为使用真实金额支付，false为使用测试金额支付（1分）
     */
    public static String WXPAY="false";
}
