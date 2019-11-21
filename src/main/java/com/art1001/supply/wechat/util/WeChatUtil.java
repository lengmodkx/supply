package com.art1001.supply.wechat.util;

import com.art1001.supply.wechat.login.context.WeChatAppLoginContext;
import com.art1001.supply.wechat.login.dto.AppLoginResponse;
import com.art1001.supply.wechat.message.configuration.WeChatAppMessageConfig;
import com.art1001.supply.wechat.message.dto.request.PushRequestParam;
import com.art1001.supply.wechat.message.dto.result.MessageResponseEntity;
import lombok.Data;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author heshaohua
 * @date 2019/11/7 15:13
 **/
@Data
@Component
public class WeChatUtil {

    @Resource
    private WeChatAppMessageConfig weChatAppMessageConfig;

    @Resource
    private WeChatAppLoginContext weChatAppLoginContext;

    private String accessToken;

    public String getAccessToken() {
        if(this.accessToken == null){
            return (this.accessToken = this.requestAccessToken());
        }

        return this.accessToken;

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
        return restTemplate.getForEntity(url.toString(), AppLoginResponse.class).getBody();
    }

    private String requestAccessToken(){
        StringBuilder url = new StringBuilder();

        url.append(weChatAppMessageConfig.getGetAccessTokenRootUrl())
                .append("grant_type=").append(weChatAppMessageConfig.getGrantType())
                .append("&").append("appid=").append(weChatAppMessageConfig.getAppId())
                .append("secret=").append(weChatAppMessageConfig.getSecret());

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(url.toString());
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(get);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                return entityToString(entity);
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
                .append("access_token=").append(this.accessToken);

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
}
