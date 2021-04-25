package com.art1001.supply.api;


import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ApiParamsCheckException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.organization.OrganizationMemberInfoService;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.resource.ResourceService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.RedisUtil;
import com.art1001.supply.util.ValidatorUtils;
import com.art1001.supply.validation.organization.SaveOrg;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

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

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private OrganizationMemberService organizationMemberService;

    @Resource
    private ResourceService resourceService;

    @Resource
    private UserService userService;

    @Resource
    private OrganizationMemberInfoService organizationMemberInfoService;



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
     * @describe
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
     * @param orgDes 企业规模
     * @param orgIntro 企业简介
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
                                @RequestParam(value = "orgIntro",required = false) String orgIntro,
                                @RequestParam(value = "isPublic",required = false) Integer isPublic,
                                @RequestParam(value = "orgImg",required = false)String orgImg,
                                @RequestParam(value = "memberId",required = false)String memberId){
        JSONObject jsonObject = new JSONObject();
        try {
            Organization organization = new Organization();
            organization.setOrganizationId(orgId);
            organization.setOrganizationName(orgName);
            organization.setOrganizationDes(orgDes);
            organization.setOrganizationIntro(orgIntro);
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
        OrganizationMember organizationMember = organizationMemberService.getOne(new QueryWrapper<OrganizationMember>().eq("organization_id", orgId).eq("organization_lable",1));
        UserEntity userEntity = userService.findById(organizationMember.getMemberId());
        organization.setUserImg(userEntity.getImage());
        organization.setContact(userEntity.getUserName());
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

    /**
     *查询首页所需用户信息
     * @param orgId
     * @return
     */
    @GetMapping("/getUserInfo/{orgId}")
    public JSONObject getHeadUserInfo(@PathVariable String orgId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result",1);
            jsonObject.put("data",userService.getHeadUserInfo(ShiroAuthenticationManager.getUserId(),orgId));
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 小程序获取企业下项目列表
     * @return
     */
    @GetMapping("/getProjects")
    public JSONObject getProjects(@RequestParam(value = "userId") String userId){
        JSONObject jsonObject = new JSONObject();
        try {
            OrganizationMember one = organizationMemberService.getOne(new QueryWrapper<OrganizationMember>().eq("member_id", userId).eq("user_default", 1));
            if (one!=null) {
                List<Project> list=projectService.findOrgProject(userId,one.getOrganizationId());
                list = list.stream().filter(project -> project.getProjectDel()==0).collect(Collectors.toList());
                jsonObject.put("result",1);
                jsonObject.put("data",list);
            }
            else {
                jsonObject.put("result",0);
                jsonObject.put("msg","你还没有加入任何项目，请加入或创建一个项目后再试吧");
            }
        } catch (Exception e) {
            throw  new AjaxException(e);
        }
        return jsonObject;
    }

}
