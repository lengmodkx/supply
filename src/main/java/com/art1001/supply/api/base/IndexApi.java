package com.art1001.supply.api.base;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.aliyuncvc.model.v20191030.CreateUserResponse;
import com.aliyuncs.aliyuncvc.model.v20191030.JoinMeetingRequest;
import com.aliyuncs.aliyuncvc.model.v20191030.JoinMeetingResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.util.AliyunMeeting;
import com.art1001.supply.util.ObjectsUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("index")
@Controller
public class IndexApi {

    @Resource
    private UserService userService;

    @Resource
    private OrganizationService organizationService;

    @GetMapping
    public String ResulttestIndex(@RequestParam String code,
                                  @RequestParam(required = false) String userId,
                                  Model model,
                                  HttpServletRequest request){
        try {
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAIP4MyTAbONGJx", "coCyCStZwTPbfu93a3Ax0WiVg3D4EW");
            IAcsClient client = new DefaultAcsClient(profile);
            JoinMeetingRequest joinMeetingRequest = new JoinMeetingRequest();
            //云视频API服务产品域名（接口地址固定，无需修改）
            joinMeetingRequest.setSysEndpoint("aliyuncvc.cn-hangzhou.aliyuncs.com");
            String cookieUserId = ObjectsUtil.getValue(request.getCookies(),"userId");
            String orgId = ObjectsUtil.getValue(request.getCookies(),"orgId");
            System.out.println("cookieUserId = " + cookieUserId);
            System.out.println("orgId = " + orgId);
            if(StringUtils.isNotEmpty(cookieUserId)){
                UserEntity userEntity = userService.getById(cookieUserId);
                Organization organization = organizationService.getById(orgId);
                //设置请求参数
                List<Map<String,String>> list = new ArrayList<>();
                Map<String,String> user = new HashMap<>();
                user.put("userId",userEntity.getUserId());
                user.put("userName",userEntity.getUserName());
                user.put("groupId",organization.getOrganizationId());
                user.put("groupName",organization.getOrganizationName());
                list.add(user);
                String str = JSON.toJSON(list).toString();
                //获取请求结果
                AliyunMeeting.createUser(str);
                joinMeetingRequest.setUserId(cookieUserId);
            }else{
                joinMeetingRequest.setUserId("-1");
            }
            joinMeetingRequest.setMeetingCode(code);

            JoinMeetingResponse joinMeetingResponse = client.getAcsResponse(joinMeetingRequest);
            if(joinMeetingResponse.getSuccess()){
                model.addAttribute("meetingInfo",joinMeetingResponse.getMeetingInfo());
                model.addAttribute("userId",StringUtils.isNotEmpty(cookieUserId)?cookieUserId:-1);
                return "meeting.html";
            }else{
                System.out.println(joinMeetingResponse.getMessage());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return "";
    }
}
