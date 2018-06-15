package com.art1001.supply.controller;


import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.partment.PartmentService;
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
    private UserService userService;

    @Resource
    private OrganizationService organizationService;

    @Resource
    private PartmentService partmentService;




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


    @RequestMapping("/addMember")
    @ResponseBody
    public JSONObject addMember(){
        JSONObject jsonObject = new JSONObject();
        try {
            
            jsonObject.put("result",1);
            jsonObject.put("msg","删除成功");
        }catch (Exception e){
            throw  new AjaxException(e);
        }

        return jsonObject;
    }










    @RequestMapping("/members")
    public void members(@RequestParam(value = "pageNo",defaultValue = "1") Integer pageNo,
                        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                        @RequestParam(value = "flag",defaultValue = "1") Integer flag){
        JSONObject jsonObject = new JSONObject();
        List<UserEntity> userEntityList = new ArrayList<>();
        try {








            jsonObject.put("result",1);
            jsonObject.put("msg","获取成功");
            jsonObject.put("data",userEntityList);

        }catch (Exception e){
            throw new AjaxException(e);
        }



    }













}
