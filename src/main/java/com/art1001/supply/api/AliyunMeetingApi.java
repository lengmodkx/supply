package com.art1001.supply.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.aliyuncvc.model.v20191030.*;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserService;

import com.art1001.supply.util.AliyunMeeting;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;


@RestController
@RequestMapping(value = "aliyun")
public class AliyunMeetingApi {

    @Resource
    private UserService userService;

    @Resource
    private OrganizationService organizationService;


    @Resource
    private ScheduleService scheduleService;

    @Resource
    private TaskService tasksService;
    /**
     * 同步用户到阿里云，存在则更新，否则为新增
     * @return
     */
    @RequestMapping("/createUser")
    public Result createUser(@RequestParam String userId,@RequestParam String orgId){
        try {
            UserEntity userEntity = userService.getById(userId);
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
            CreateUserResponse createUserResponse = AliyunMeeting.createUser(str);
            if(createUserResponse.getSuccess()) {
                // 业务逻辑
                return Result.success();
            }else{
                System.out.println(createUserResponse.getMessage());
            }
        }catch (Exception e){
            throw new AjaxException(e.getMessage());
        }
        return Result.fail("同步失败");
    }

    /**
     * 创建会议，需要推送到界面
     * @param userId 创建会议用户id
     * @param meetingName 会议名称
     * @return
     */
    @Push(value = PushType.D15)
    @RequestMapping("createMeeting")
    public JSONObject createMeeting(@RequestParam String userId,
                                    @RequestParam String meetingName,
                                    @RequestParam String scheduleId,
                                    @RequestParam String projectId){
        JSONObject object = new JSONObject();
        try {
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAIP4MyTAbONGJx", "coCyCStZwTPbfu93a3Ax0WiVg3D4EW");
            IAcsClient client = new DefaultAcsClient(profile);
            CreateMeetingRequest request = new CreateMeetingRequest();
            //云视频API服务产品域名（接口地址固定，无需修改）
            request.setSysEndpoint("aliyuncvc.cn-hangzhou.aliyuncs.com");

            request.setUserId(userId);
            request.setMeetingName(meetingName);
            CreateMeetingResponse meetingResponse = client.getAcsResponse(request);
            if(meetingResponse.getSuccess()){
                Schedule schedule = new Schedule();
                schedule.setScheduleId(scheduleId);
                schedule.setUpdateTime(System.currentTimeMillis());
                schedule.setMeetingCode(meetingResponse.getMeetingInfo().getMeetingCode());
                scheduleService.updateById(schedule);
                object.put("data",projectId);
                object.put("meeting",meetingResponse.getMeetingInfo());
                object.put("msgId",projectId);
                object.put("result",1);
                return object;
            }else{
                System.out.println(meetingResponse.getMessage());
            }
        }catch (Exception e){
            throw new AjaxException(e.getMessage());
        }
        return object;
    }

    /**
     * 加入会议
     * @return
     */
    @RequestMapping("joinMeeting")
    public Result joinMeeting(@RequestParam String userId,@RequestParam String meetingCode){

        try  {
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAIP4MyTAbONGJx", "coCyCStZwTPbfu93a3Ax0WiVg3D4EW");
            IAcsClient client = new DefaultAcsClient(profile);
            JoinMeetingRequest request = new JoinMeetingRequest();
            //云视频API服务产品域名（接口地址固定，无需修改）
            request.setSysEndpoint("aliyuncvc.cn-hangzhou.aliyuncs.com");
            request.setUserId(userId);
            request.setMeetingCode(meetingCode);
            JoinMeetingResponse joinMeetingResponse = client.getAcsResponse(request);
            if(joinMeetingResponse.getSuccess()){
                return Result.success(joinMeetingResponse.getMeetingInfo());
            }else{
                System.out.println(joinMeetingResponse.getMessage());
            }
        }catch (Exception e){
            throw new AjaxException(e.getMessage());
        }

        return Result.fail("参会失败");
    }


    @RequestMapping("ActiveMeeting")
    public Result activeMeeting(@RequestParam String meetingUUID,@RequestParam String meetingCode){
        try {
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAIP4MyTAbONGJx", "coCyCStZwTPbfu93a3Ax0WiVg3D4EW");
            IAcsClient client = new DefaultAcsClient(profile);
            ActiveMeetingRequest request = new ActiveMeetingRequest();
            //云视频API服务产品域名（接口地址固定，无需修改）
            request.setSysEndpoint("aliyuncvc.cn-hangzhou.aliyuncs.com");

            request.setMeetingUUID(meetingUUID);
            request.setMeetingCode(meetingCode);
            ActiveMeetingResponse activeMeetingResponse = client.getAcsResponse(request);
            if(activeMeetingResponse.getSuccess()){
                return Result.success(activeMeetingResponse.getMeetingInfo());
            }else{
                System.out.println(activeMeetingResponse.getMessage());
            }
        }catch (Exception e){
            throw new AjaxException(e.getMessage());
        }

        return Result.fail("激活失败");
    }

    @RequestMapping("deleteMeeting")
    public Result deleteMeeting(@RequestParam String meetingUUID){
        try {
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAIP4MyTAbONGJx", "coCyCStZwTPbfu93a3Ax0WiVg3D4EW");
            IAcsClient client = new DefaultAcsClient(profile);
            DeleteMeetingRequest request = new DeleteMeetingRequest();
            //云视频API服务产品域名（接口地址固定，无需修改）
            request.setSysEndpoint("aliyuncvc.cn-hangzhou.aliyuncs.com");

            request.setMeetingUUID(meetingUUID);
            DeleteMeetingResponse deleteMeetingResponse = client.getAcsResponse(request);
            if(deleteMeetingResponse.getSuccess()){
                return Result.success();
            }else{
                System.out.println(deleteMeetingResponse.getMessage());
            }
        }catch (Exception e){
            throw new AjaxException(e.getMessage());
        }

        return Result.fail("删除失败");


    }


}
