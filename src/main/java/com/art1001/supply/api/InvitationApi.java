package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.organization.InvitationLink;
import com.art1001.supply.entity.user.UserVO;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.organization.InvitationLinkService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * @ClassName InvitationApi
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/5/20 15:41
 * @Discription 邀请api
 */
@Slf4j
@RestController
@RequestMapping("/invite")
public class InvitationApi {

    @Resource
    private InvitationLinkService invitationLinkService;

    @Resource
    private RedisUtil redisUtil;

    @GetMapping("/{hash}")
    public String redirectUrl(HttpServletResponse response, @PathVariable String hash) throws IOException {
        InvitationLink invitationLink = invitationLinkService.getRedrectUrl(hash);
        if (invitationLink == null) {
            return "链接已过期，请联系邀请人重新发送";
        }
        response.sendRedirect(invitationLink.getCompleteUrl());
        return "";
    }

    /**
     * 设置保存时间
     * @param longUrl
     * @return
     */
    @GetMapping("/saveTime")
    public JSONObject saveTime(String longUrl){
        JSONObject jsonObject=new JSONObject();
        try {
            LocalDate localDate = LocalDate.now().plusDays(1);
            long time = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            redisUtil.set("saveTime:"+ ShiroAuthenticationManager.getUserId(),longUrl,time);
            jsonObject.put("result",1);
            jsonObject.put("data",localDate+"");
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 获取 项目/企业邀请人信息
     * @param memberId
     * @param orgId
     * @param projectId
     * @return
     */
    @GetMapping("/getInviteMemberInfo/{memberId}")
    public JSONObject getInviteMemberInfo(@PathVariable(value = "memberId") String memberId,
                                          @RequestParam(value = "orgId",required = false)String orgId,
                                          @RequestParam(value = "projectId",required = false)String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result",1);
            jsonObject.put("data",invitationLinkService.getInviteMemberInfo(memberId,orgId,projectId));
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }

    }

}
