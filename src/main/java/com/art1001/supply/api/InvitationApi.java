package com.art1001.supply.api;

import com.art1001.supply.entity.organization.InvitationLink;
import com.art1001.supply.service.organization.InvitationLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName InvitationApi
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/5/20 15:41
 * @Discription 邀请api
 */
@Slf4j
@RestController
//@RequestMapping("/invite")
public class InvitationApi {

    @Resource
    private InvitationLinkService invitationLinkService;

    @GetMapping("/{hash}")
    public String redirectUrl(HttpServletResponse response, @PathVariable String hash) throws IOException {
        InvitationLink invitationLink = invitationLinkService.getRedrectUrl(hash);
        if (invitationLink == null) {
            return "链接已过期，请联系邀请人重新发送";
        }
        response.sendRedirect(invitationLink.getCompleteUrl());
        return "";
    }

//    @GetMapping("/{hash}")
//        public String setReferer(HttpServletRequest request,HttpServletResponse response,@PathVariable String hash){
//        InvitationLink invitationLink = invitationLinkService.getRedrectUrl(hash);
//        if (invitationLink == null) {
//            return "链接已过期，请联系邀请人重新发送";
//        }
//
//    }
}
