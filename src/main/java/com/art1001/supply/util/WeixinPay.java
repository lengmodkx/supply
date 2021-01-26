package com.art1001.supply.util;

import com.art1001.supply.entity.wechat.WechatParam;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import lombok.extern.log4j.Log4j;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @Description
 * @Date:2019/8/31 11:13
 * @Author heshaohua
 **/
@Log4j
public class WeixinPay {
    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xFFFFFFFF;

    /**
     * 获取微信支付的二维码地址
     *
     * @return
     * @throws Exception
     * @author chenp
     */
    public static String getCodeUrl(WechatParam ps) throws Exception {
        /**
         * 账号信息
         */
        //微信服务号的appid
        String appid = WeChatConfig.APPID;
        //微信支付商户号
        String mch_id = WeChatConfig.MCHID;
        // 微信支付的API密钥
        String key = WeChatConfig.APIKEY;
        //回调地址【注意，这里必须要使用外网的地址】
        String notify_url = WeChatConfig.WECHAT_NOTIFY_URL_PC;
        //微信下单API地址
        String ufdoder_url = WeChatConfig.UFDODER_URL;
        //类型【网页扫码支付】
        String trade_type = "NATIVE";

        /**
         * 时间字符串
         */
        String currTime = PayForUtil.getCurrTime();
        String strTime = currTime.substring(8, currTime.length());
        String strRandom = PayForUtil.buildRandom(4) + "";
        String nonce_str = strTime + strRandom;

        /**
         * 参数封装
         */
        SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
        packageParams.put("appid", appid);
        packageParams.put("mch_id", mch_id);
        //随机字符串
        packageParams.put("nonce_str", nonce_str);
        //支付的商品名称
        packageParams.put("body", ps.getBody());
        packageParams.put("out_trade_no", ps.getOutTradeNo());
        //支付金额
        packageParams.put("total_fee", ps.getTotalFee());
        //客户端主机
        packageParams.put("spbill_create_ip", PayForUtil.localIp());
        packageParams.put("notify_url", notify_url);
        packageParams.put("trade_type", trade_type);
        packageParams.put("product_id", String.valueOf(ps.getProductId()));
        //额外的参数【业务类型+会员ID+支付类型】
        packageParams.put("attach", ps.getMemberid() + "," + ps.getProductId());

        //获取签名
        String sign = PayForUtil.createSign("UTF-8", packageParams, key);
        packageParams.put("sign", sign);

        //将请求参数转换成String类型
        String requestXML = PayForUtil.getRequestXml(packageParams);
        log.info("微信支付请求参数的报文" + requestXML);
        //解析请求之后的xml参数并且转换成String类型
        String resXml = HttpUtil.postData(ufdoder_url, requestXML);
        Map map = XMLUtil.doXMLParse(resXml);

        log.info("微信支付响应参数的报文" + resXml);
        String urlCode = (String) map.get("code_url");

        return urlCode;
    }

    /**
     * 将路径生成二维码图片
     *
     * @param content   微信支付的二维码地址
     * @param response
     * @author chenp
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void encodeQrcode(String content, HttpServletResponse response) {
        response.setContentType("image/png");
        ServletOutputStream outputStream;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Map hints = new HashMap();
        // 二维码矩阵类
        BitMatrix bitMatrix = null;
        try {
            // 使用默认设置对条形码进行编码
            bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 250, 250, hints);
            BufferedImage image = toBufferedImage(bitMatrix);
            //输出二维码图片流
            try {
                outputStream = response.getOutputStream();
                ImageIO.write(image, "png", outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (WriterException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 类型转换
     *
     * @param matrix
     * @return
     * @author chenp
     */
    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) == true ? BLACK : WHITE);
            }
        }
        return image;
    }

    // 特殊字符处理
    public static String UrlEncode(String src) throws UnsupportedEncodingException {
        return URLEncoder.encode(src, "UTF-8").replace("+", "%20");
    }
}
