package com.art1001.supply.wechat.util;

import com.alibaba.fastjson.JSON;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.util.ObjectsUtil;
import com.art1001.supply.wechat.login.context.WeChatAppLoginContext;
import com.art1001.supply.wechat.login.dto.AppLoginResponse;
import com.art1001.supply.wechat.message.configuration.WeChatAppMessageConfig;
import com.art1001.supply.wechat.message.dto.request.PushRequestParam;
import com.art1001.supply.wechat.message.dto.result.MessageResponseEntity;
import com.art1001.supply.wechat.message.template.MessageToken;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;

/**
 * @author heshaohua
 * @date 2019/11/7 15:13
 **/
@Slf4j
@Data
@Component
public class WeChatUtil {

    @Resource
    private WeChatAppMessageConfig weChatAppMessageConfig;

    @Resource
    private WeChatAppLoginContext weChatAppLoginContext;

    private String accessToken;

    public MessageToken getAccessToken() {
        return this.requestAccessToken();
    }

    public AppLoginResponse getOpenIdAndSessionKey(String code){
        StringBuilder url = new StringBuilder("https://api.weixin.qq.com/sns/jscode2session?");

        url.append("appid=")
                .append(weChatAppLoginContext.getAppId())
                .append("&secret=").append(weChatAppLoginContext.getSecret())
                .append("&js_code=").append(code)
                .append("&grant_type=").append(weChatAppMessageConfig.getGrantType());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new WxMappingJackson2HttpMessageConverter());
        AppLoginResponse body = restTemplate.getForEntity(url.toString(), AppLoginResponse.class).getBody();
        log.info("请求返回结果：[{}]" ,body);

        if(ObjectsUtil.isNotEmpty(body)){
            if(ObjectsUtil.isEmpty(body.getErrcode())){
                log.info("小程序授权登录成功");
            } else {
                log.error("授权失败！[{},{}]", body.getErrcode(), body.getErrmsg());
                throw new ServiceException(body.getErrmsg());
            }
        }

        return body;
    }

    private MessageToken requestAccessToken(){
        StringBuilder url = new StringBuilder();

        url.append(weChatAppMessageConfig.getGetAccessTokenRootUrl())
                .append("grant_type=").append(weChatAppMessageConfig.getGrantType())
                .append("&").append("appid=").append(weChatAppMessageConfig.getAppId())
                .append("&").append("secret=").append(weChatAppMessageConfig.getSecret());

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(url.toString());
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(get);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                String s = entityToString(entity);
                return JSON.parseObject(s, MessageToken.class);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public MessageResponseEntity sendWeChatAppMessageRequest(PushRequestParam pushRequestParam){
        StringBuilder url = new StringBuilder();

        url.append(weChatAppMessageConfig.getSendMessageRootUrl())
                .append("access_token=").append(pushRequestParam.getTokenInfo().getAccess_token());

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForEntity(url.toString(), pushRequestParam, MessageResponseEntity.class).getBody();
    }


    private static String entityToString(HttpEntity entity) throws IOException {
        String result = null;
        if(entity != null)
        {
            long lenth = entity.getContentLength();
            if(lenth != -1 && lenth < 2048) {
                result = EntityUtils.toString(entity,"UTF-8");
            } else {
                InputStreamReader reader1 = new InputStreamReader(entity.getContent(), "UTF-8");
                CharArrayBuffer buffer = new CharArrayBuffer(2048);
                char[] tmp = new char[1024];
                int l;
                while((l = reader1.read(tmp)) != -1) {
                    buffer.append(tmp, 0, l);
                }
                result = buffer.toString();
            }
        }
        return result;
    }

    /**
     * 解密小程序UserInfo的加密数据
     * @param key key
     * @param iv 偏移量
     * @param encData 加密数据
     * @return 用户信息
     * @throws Exception 异常信息
     */
    public static String decrypt(byte[] key, byte[] iv, byte[] encData) throws Exception {
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        //解析解密后的字符串  
        return new String(cipher.doFinal(encData), StandardCharsets.UTF_8);
    }

    public static <T> T deciphering(String data, String iv, String key, Class<T> cls) throws Exception{
        byte[] encrypData = Base64.decode(data);
        byte[] ivData = Base64.decode(iv);
        byte[] sessionKey = Base64.decode(key);
        String str = WeChatUtil.decrypt(sessionKey,ivData,encrypData);
        log.info("解密后的小程序用户数据[{}]", str);
        Class<String> stringClass = String.class;

        return JSON.parseObject(str, cls);
    }
}
