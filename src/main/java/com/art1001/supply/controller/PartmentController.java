package com.art1001.supply.controller;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.partment.Partment;
import com.art1001.supply.entity.project.OrganizationMember;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.partment.PartmentService;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/partment")
public class PartmentController {

    @Resource
    private OrganizationService organizationService;

    @Resource
    private PartmentService partmentService;

    @Resource
    private OrganizationMemberService organizationMemberService;

    @Resource
    private UserService userService;

    @RequestMapping("/addPartment")
    @ResponseBody
    public JSONObject addPartment(@RequestParam(value = "orgId") String orgId,
                                  @RequestParam(value = "partmentName") String partmentName,
                                  @RequestParam(value = "partmentLogo") String partmentLogo){
        JSONObject jsonObject = new JSONObject();
        try {

            Partment partment = new Partment();
            partment.setOrganizationId(orgId);
            partment.setPartmentName(partmentName);
            partment.setPartmentLogo(partmentLogo);
            partment.setUpdateTime(System.currentTimeMillis());
            partment.setCreateTime(System.currentTimeMillis());
            partmentService.savePartment(partment);
            jsonObject.put("result",1);
            jsonObject.put("msg","创建成功");

        }catch (Exception e){
            throw new AjaxException(e);
        }

        return jsonObject;
    }


    @RequestMapping("/updatePartment")
    @ResponseBody
    public JSONObject updatePartment(@RequestParam(value = "orgId") String orgId,
                                     @RequestParam(value = "partmentName") String partmentName,
                                     @RequestParam(value = "partmentLogo") String partmentLogo){
        JSONObject jsonObject = new JSONObject();
        try {

            Partment partment = new Partment();
            partment.setOrganizationId(orgId);
            partment.setPartmentName(partmentName);
            partment.setPartmentLogo(partmentLogo);
            partment.setUpdateTime(System.currentTimeMillis());
            partment.setCreateTime(System.currentTimeMillis());
            partmentService.updatePartment(partment);
            jsonObject.put("result",1);
            jsonObject.put("msg","更新成功");

        }catch (Exception e){
            throw new AjaxException(e);
        }

        return jsonObject;
    }


    @RequestMapping("/deletePartment")
    @ResponseBody
    public JSONObject deletePartment(@RequestParam(value = "orgId") String partmentId){
        JSONObject jsonObject = new JSONObject();
        try {
            partmentService.deletePartmentByPartmentId(partmentId);
            jsonObject.put("result",1);
            jsonObject.put("msg","删除成功");

        }catch (Exception e){
            throw new AjaxException(e);
        }

        return jsonObject;
    }


    @RequestMapping("/members.html")
    public String members(@RequestParam String orgId, Model model){
        try {
            OrganizationMember organizationMember = new OrganizationMember();
            organizationMember.setOrganizationId(orgId);
            List<OrganizationMember> memberAllList = organizationMemberService.findOrganizationMemberAllList(organizationMember);
            model.addAttribute("memberList",memberAllList);
        }catch (Exception e) {

            throw new SystemException(e);
        }
        return "";
    }

    @RequestMapping("/addMember")
    @ResponseBody
    public JSONObject addMember(@RequestParam(value = "partmentId") String partmentId,
                                @RequestParam(value = "memberId") String memberId){
        JSONObject jsonObject = new JSONObject();
        try {

            OrganizationMember member = organizationMemberService.findOrgByMemberId(memberId);

            if(member.getMemberLock()==0){
                jsonObject.put("result",0);
                jsonObject.put("msg","邀请失败，该成员已被停用");
            }else{
                OrganizationMember member1 = new OrganizationMember();
                member1.setPartmentId(partmentId);
                member1.setPartmentLable(0);
                organizationMemberService.updateOrganizationMember(member1);
                jsonObject.put("result",1);
                jsonObject.put("msg","添加成功");
            }
        }catch (Exception e){
            throw new AjaxException(e);
        }

        return jsonObject;
    }


}
