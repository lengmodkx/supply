package com.art1001.supply.controller;


import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.project.OrganizationMember;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.partment.PartmentService;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 组织成员管理控制器
 */
@Controller
@Slf4j
@RequestMapping("/organization")
public class OrganizationController {

    @Resource
    private OrganizationService organizationService;

    @Resource
    private PartmentService partmentService;

    @Resource
    private OrganizationMemberService organizationMemberService;

    @Resource
    private UserService userService;

    //增加一个企业
    @RequestMapping("/addOrg")
    @ResponseBody
    public JSONObject  addOrg(@RequestParam String orgName,@RequestParam String orgDes){
        JSONObject jsonObject = new JSONObject();

        try {
            String userId = ShiroAuthenticationManager.getUserId();

            Organization organization = new Organization();
            organization.setOrganizationName(orgName);
            organization.setOrganizationDes(orgDes);
            organization.setIsPublic(0);
            organization.setOrganizationImgae("");
            organization.setOrganizationMember(userId);
            organization.setCreateTime(System.currentTimeMillis());
            organizationService.saveOrganization(organization);
            jsonObject.put("result",1);
            jsonObject.put("msg","保存成功");
        }catch (Exception e){
            throw new AjaxException(e);
        }

        return jsonObject;
    }



    //更新企业
    @RequestMapping("/updateOrg")
    @ResponseBody
    public JSONObject updateOrg(Organization organization){
        JSONObject jsonObject = new JSONObject();
        try {

            organization.setUpdateTime(System.currentTimeMillis());
            organizationService.updateOrganization(organization);
            jsonObject.put("result",1);
            jsonObject.put("msg","更新成功");
        }catch (Exception e){
            throw  new AjaxException(e);
        }

        return jsonObject;
    }


    //删除企业
    @RequestMapping("/delOrg")
    @ResponseBody
    public JSONObject delOrg(@RequestParam String orgId){
        JSONObject jsonObject = new JSONObject();
        try {

            organizationService.deleteOrganizationByOrganizationId(orgId);
            jsonObject.put("result",1);
            jsonObject.put("msg","删除成功");
        }catch (Exception e){
            throw  new AjaxException(e);
        }

        return jsonObject;
    }


    //给企业添加员工
    @RequestMapping("/addMember")
    @ResponseBody
    public JSONObject addMember(@RequestParam String orgId,@RequestParam String[] memberIds){
        JSONObject jsonObject = new JSONObject();
        try {
            for (String memberId:memberIds) {
                OrganizationMember member = organizationMemberService.findOrgByMemberId(memberId);
                UserEntity userEntity = userService.findById(memberId);
                OrganizationMember organizationMember = new OrganizationMember();
                organizationMember.setOrganizationId(orgId);
                organizationMember.setMemberEmail(userEntity.getUserInfo().getEmail());
                organizationMember.setMemberImg(userEntity.getUserInfo().getImage());
                organizationMember.setMemberId(memberId);
                organizationMember.setMemberName(userEntity.getUserName());
                organizationMember.setMemberPhone(userEntity.getUserInfo().getTelephone());
                organizationMember.setOrganizationLable(1);
                if(member==null){
                    //新增成员的部门id统一为0
                    organizationMember.setPartmentId("0");
                    organizationMemberService.saveOrganizationMember(organizationMember);
                    jsonObject.put("result",1);
                    jsonObject.put("msg","添加成功");
                }else{
                    if(member.getMemberLock()==1){
                        organizationMember.setId(member.getId());
                        organizationMemberService.updateOrganizationMember(organizationMember);
                        jsonObject.put("result",1);
                        jsonObject.put("msg","添加成功");
                    }else{
                        jsonObject.put("result",0);
                        jsonObject.put("msg","邀请失败，该成员已被停用");
                    }
                }
            }
        }catch (Exception e){
            throw  new AjaxException(e);
        }

        return jsonObject;
    }



    @RequestMapping("/members")
    @ResponseBody
    public JSONObject members(@RequestParam(value = "pageNo",defaultValue = "1") Integer pageNo,
                        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                        @RequestParam(value = "flag",defaultValue = "1") Integer flag,
                        @RequestParam(value = "orgId") String orgId){
        JSONObject jsonObject = new JSONObject();

        try {

            Pager pager = new Pager();
            pager.setPageNo(pageNo);
            pager.setPageSize(pageSize);
            OrganizationMember organizationMember = new OrganizationMember();
            organizationMember.setMemberLock(1);
            organizationMember.setId(orgId);

            //未分配部门的员工
            if(flag==1){
                organizationMember.setPartmentId("0");
            }else if(flag==2){//账号停用的员工
                organizationMember.setMemberLock(0);
            }

            pager.setCondition(organizationMember);
            List<OrganizationMember> memberList = organizationMemberService.findOrganizationMemberPagerList(pager);
            jsonObject.put("result",1);
            jsonObject.put("msg","获取成功");
            jsonObject.put("data",memberList);

        }catch (Exception e){
            throw new AjaxException(e);
        }

        return jsonObject;

    }




    @RequestMapping("/addPartment")
    @ResponseBody
    public JSONObject addPartment(@RequestParam(value = "orgId") String orgId,
                                  @RequestParam(value = "partmentName") String partmentName,
                                  @RequestParam(value = "partmentLogo") String partmentLogo){
        JSONObject jsonObject = new JSONObject();

        try {



            jsonObject.put("result",1);
            jsonObject.put("msg","创建成功");

        }catch (Exception e){
            throw new AjaxException(e);
        }

        return jsonObject;
    }








}
