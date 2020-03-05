package com.art1001.supply.api;


import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ApiParamsCheckException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.ValidatorUtils;
import com.art1001.supply.validation.organization.SaveOrg;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
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
     * @param organization 企业信息
     * @return
     */
    @PostMapping
    public JSONObject addOrg(Organization organization){
        JSONObject jsonObject = new JSONObject();
        try {
            ValidatorUtils.validateEntity(organization, SaveOrg.class);
            organization.setOrganizationImage("https://art1001-bim-5d.oss-cn-beijing.aliyuncs.com/upload/org-img.jpg");
            organizationService.saveOrganization(organization);
            jsonObject.put("result",1);
            jsonObject.put("data", organization.getOrganizationId());
        } catch (ApiParamsCheckException e){
            throw new AjaxException(e.getMessage(),e);
        } catch (Exception e){
            log.error("企业添加失败:",e);
            throw e;
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
    @GetMapping("/{orgId}/projects")
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
     * 获取我参与者和我创建的企业
     * @param flag (1.我创建的企业 2.我参与的企业)
     * @return
     */
    @GetMapping("/my_org")
    public JSONObject getMyOrg(@RequestParam(required = false) Integer flag) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", organizationService.getMyOrg(flag));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            throw new SystemException("系统异常,数据获取失败!", e);
        }
    }

    /**
     * 获取该企业下的项目信息
     * @author heShaoHua
     * @describe 暂无
     * @param orgId 企业id
     * @updateInfo 暂无
     * @date 2019/5/29 10:13
     * @return 企业下的项目信息列表
     */
    @GetMapping("/{orgId}")
    public JSONObject getOrgProject(@NotEmpty(message = "orgId不能为空!") @PathVariable String orgId){
        JSONObject jsonObject = new JSONObject();
        try {
            List<Project> projects = organizationService.getProject(orgId);
            jsonObject.put("data", projects);
            jsonObject.put("result", 1);
        } catch (Exception e){
            throw new AjaxException("系统异常,信息获取失败!",e);
        }
        return jsonObject;
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
    //@RequiresPermissions("update:org")
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

    @PutMapping("/personal_project")
    public Result changePersonalProject(){
        log.info("change to personal project.");
        organizationService.personalProject(ShiroAuthenticationManager.getUserId());
        return Result.success();
    }

    //企业信息
    @GetMapping
    public Result<Organization> orgInfo(@RequestParam(value = "orgId") String orgId){
        Organization organization = organizationService.getById(orgId);
        return Result.success(organization);
    }

    /**
     * 删除企业
     * @param orgId 企业id
     * @return 结果
     */
    @PostMapping("deleted")
    public Result deletedOrg(String orgId){
        log.info("Delete org info [{}], operator is [{}]", orgId, ShiroAuthenticationManager.getUserId());
        organizationService.deleteOrganizationByOrganizationId(orgId);
        return Result.success();
    }


}
