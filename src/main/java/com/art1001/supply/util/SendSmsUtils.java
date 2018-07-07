package com.art1001.supply.util;

import com.art1001.supply.util.httpclient.HttpProtocolHandler;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Map;

public class SendSmsUtils {

    /**
     * C123短信接口
     * ac:用户账号,authkey:认证密钥,cgid:通道组编号,csid:签名编号,c:短信内容,m:发送号码,如多个以英文逗号分隔
     * @return 1:操作成功,0:账户格式不正确,-1:服务器拒绝,-2:密钥不正确,-3密钥已锁定,-4:参数不正确
     * @return -5：无此账户，-6：账户已锁定或已过期，-7：账户未开启接口发送，-8：不可使用该通道组
     * @return -9账户余额不足，-10：内部错误，-11：扣费失败
     */
    public String sendSms(String mobiles, String msg) {
        String apiurl = "http://smsapi.c123.cn/OpenPlatform/OpenApi?action=sendOnce";
        Map<String ,String> postData = new HashMap<>();
        postData.put("ac", "1001@501268510001");
        postData.put("authkey", "C0F2621D50616B08AC0B728365D16F61");
        postData.put("cgid", "6075");
        postData.put("csid", "8515");
        postData.put("m", mobiles);
        postData.put("c", msg);
        postData.put("encode", "utf-8");
        String str = "";
        try {
            HttpProtocolHandler httpProto = HttpProtocolHandler.getInstance();
            String content =  httpProto.postHttpClient(apiurl, "0", postData);

            Document document = DocumentHelper.parseText(content);
            Element root = document.getRootElement();
            str = root.attributeValue("result");
            System.out.println("短息发送返回信息:" + str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static void main(String[] args) {
        SendSmsUtils c = new SendSmsUtils();
        c.sendSms("18701053211", "您的验证码是：463279");
    }
}
