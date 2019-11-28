package com.art1001.supply.aliyun.message.context.aliyun;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author heshaohua
 * @date 2019/11/21 14:24
 **/
@Data
@SuppressWarnings("all")
@Component
public class AliyunMessgaeContext {

    @Value("${aliyun.message.access-key-id}")
    private String accessKey;

    @Value("${aliyun.message.access-key-secret}")
    private String accessKeySecret;


    public DefaultProfile buidDefaultProfile(){
        return DefaultProfile.getProfile("cn-hangzhou", this.accessKey, accessKeySecret);
    }

    public CommonRequest buidCommonRequest(){
        CommonRequest request = new CommonRequest();
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.setMethod(MethodType.POST);
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("TemplateCode", "SMS_177539779");
        request.putQueryParameter("SignName", "壹仟零壹艺科技有限公司");
        return request;
    }
}
