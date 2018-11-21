package com.art1001.supply.api;


import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 组织成员管理控制器
 */
@RestController
@Slf4j
@RequestMapping("/organizations")
public class OrganizationApi {

    @Resource
    private OrganizationService organizationService;

    @Resource
    private ProjectService projectService;

    /**
     * 新增企业
     * @param orgName 企业名称
     * @param orgDes 企业描述
     * @param contact 企业联系人
     * @param contactPhone 企业联系人联系方式
     * @return
     */
    @PostMapping
    public JSONObject addOrg(@RequestParam(value = "orgName") String orgName,
                          @RequestParam(value = "orgDes") String orgDes,
                          @RequestParam(value = "contact") String contact,
                          @RequestParam(value = "contactPhone") String contactPhone){
        JSONObject jsonObject = new JSONObject();
        try {
            Organization organization = new Organization();
            organization.setOrganizationName(orgName);
            organization.setOrganizationDes(orgDes);
            organization.setContact(contact);
            organization.setContactPhone(contactPhone);
            organizationService.saveOrganization(organization);
            jsonObject.put("result",1);
        }catch (Exception e){
            log.error("企业添加失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    /**
     * 删除企业
     * @param orgId 企业id
     * @return
     */
    @DeleteMapping("/{orgId}")
    public JSONObject delOrg(@PathVariable(value = "orgId") String orgId){
        JSONObject jsonObject = new JSONObject();
        try {
            organizationService.deleteOrganizationByOrganizationId(orgId);
            jsonObject.put("result",1);
        }catch (Exception e){
            log.error("企业删除失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 获取 我参与的企业项目，我收藏的企业项目
     * @return
     */
    @GetMapping("/{orgId}")
    public JSONObject orgProjects(@PathVariable(value = "orgId",required = false)String orgId){
        JSONObject object = new JSONObject();
        try{
            String userId = ShiroAuthenticationManager.getUserId();
            List<Project> projectList = projectService.findOrgProject(userId,orgId);
            object.put("result",1);
            object.put("data",projectList);
        }catch (Exception e){
            log.error("系统异常,信息获取失败:",e);
            throw new SystemException(e);
        }
        return object;
    }

    /**
     * 更新企业
     * @param orgId 企业id
     * @param orgName 企业名称
     * @param orgDes 企业简介
     * @param isPublic 企业公开性
     * @param orgImg 企业头像
     * @param memberId 企业归属
     * @return
     */
    @PutMapping("/{orgId}")
    public JSONObject updateOrg(@PathVariable(value = "orgId") String orgId,
                                @RequestParam(value = "orgName",required = false) String orgName,
                                @RequestParam(value = "orgDes",required = false) String orgDes,
                                @RequestParam(value = "isPublic",required = false) Integer isPublic,
                                @RequestParam(value = "orgImg",required = false)String orgImg,
                                @RequestParam(value = "memberId",required = false)String memberId){
        JSONObject jsonObject = new JSONObject();
        try {
            Organization organization = new Organization();
            organization.setOrganizationId(orgId);
            organization.setOrganizationName(orgName);
            organization.setOrganizationDes(orgDes);
            organization.setIsPublic(isPublic);
            organization.setOrganizationImage(orgImg);
            organization.setOrganizationMember(memberId);
            organizationService.updateOrganization(organization);
            jsonObject.put("result",1);
        }catch (Exception e){
            log.error("企业信息更新失败:",e);
            throw  new AjaxException(e);
        }
        return jsonObject;
    }
}
