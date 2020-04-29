package com.art1001.supply.util;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.aliyuncvc.model.v20191030.CreateUserRequest;
import com.aliyuncs.aliyuncvc.model.v20191030.CreateUserResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

public class AliyunMeeting {

    private static final String regionId = "cn-hangzhou";

    private static final String accessKeyId = "LTAIP4MyTAbONGJx";

    private static final String secret = "coCyCStZwTPbfu93a3Ax0WiVg3D4EW";

    private static final String sysEndpoint = "aliyuncvc.cn-hangzhou.aliyuncs.com";

    public static CreateUserResponse createUser(String userInfo) throws Exception{
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, secret);
        IAcsClient client = new DefaultAcsClient(profile);
        //组装请求对象
        CreateUserRequest createUserRequest = new CreateUserRequest();
        //云视频API服务产品域名（接口地址固定，无需修改）
        createUserRequest.setSysEndpoint(sysEndpoint);
        createUserRequest.setUserInfo(userInfo);
        createUserRequest.setCount(1);
        //获取请求结果
        return client.getAcsResponse(createUserRequest);
    }

}
