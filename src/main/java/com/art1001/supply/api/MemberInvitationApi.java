package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.role.ProRoleUser;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.role.ProRoleUserService;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * 项目成员邀请
 * @author 汪亚锋
 * [POST]   // 新增
 * [GET]    // 查询
 * [PATCH]  // 更新
 * [PUT]    // 覆盖，全部更新
 * [DELETE] // 删除
 */
@Slf4j
@Validated
@RestController
@RequestMapping("members")
public class MemberInvitationApi extends BaseController {

    @Resource
    private UserService userService;

    @Resource
    private ProjectMemberService projectMemberService;

    @Resource
    private RoleService roleService;

    @Resource
    private ProRoleUserService proRoleUserService;

    @Resource
    private OrganizationMemberService organizationMemberService;
    /**
     * 通过用户账户查询用户
     * @param keyword 关键字
     * @return
     */
    @GetMapping("/{keyword}")
    public JSONObject searchMember(@PathVariable(value = "keyword") String keyword,
                                   @RequestParam(value = "orgId",required = false) String orgId){
        JSONObject object = new JSONObject();
        try{

            List<UserEntity> userEntityList = userService.findByKey(keyword);

            object.put("result",1);
            object.put("data",userEntityList);
        }catch(Exception e){
            log.error("系统异常:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
    * @Author: 邓凯欣
    * @Email： dengkaixin@art1001.com
    * @Param: projectId 项目id
    * @param： memberId 用户id
    * @param： orgId 企业id
    * @return:
    * @Description: 给项目添加成员，同时将数据存储到企业详情信息表
    * @create: 15:33 2020/4/22
    */
    //@RequiresPermissions("create:member")
    @PostMapping
    public JSONObject addMember(@RequestParam(value = "projectId") String projectId,
                                @RequestParam(value = "memberId") String memberId,
                                @RequestParam(value = "orgId") String orgId){
        JSONObject object = new JSONObject();
        int exist = projectMemberService.findMemberIsExist(projectId,memberId);
        if(exist>0){
            object.put("result",0);
            object.put("msg","项目成员已存在，请勿重复添加");
            return  object;
        }
        object.put("result",projectMemberService.saveMember(projectId,memberId,orgId));
        object.put("msg","添加成功");
        return object;
    }

    /**
     * 移除项目参与者
     * @param memberId 成员id
     * @return
     */
    @DeleteMapping("/{memberId}")
    public JSONObject deleteMember(@PathVariable(value = "memberId") String memberId,
                                   @NotNull(message = "projectId不能为空!") String projectId){
        JSONObject object = new JSONObject();
        try{
            projectMemberService.remove(new QueryWrapper<ProjectMember>()
                    .lambda().eq(ProjectMember::getMemberId,memberId)
                    .eq(ProjectMember::getProjectId, projectId));

            proRoleUserService.remove(new QueryWrapper<ProRoleUser>().lambda()
                    .eq(ProRoleUser::getUId,memberId).eq(ProRoleUser::getProjectId, projectId));

            object.put("result",1);
            object.put("msg","移除成功");
        }catch(Exception e){
            log.error("系统异常,成员移除失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    @PutMapping("/{memberId}")
    public JSONObject upadteMemberRole(@PathVariable(value = "memberId") String memberId,@RequestParam(value = "projectId") String projectId){
        JSONObject object = new JSONObject();
        try{
            Role roleEntity = roleService.getOne(new QueryWrapper<Role>().eq("name","成员"));

            object.put("result",1);
            object.put("msg","更新成功");
        }catch(Exception e){
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新项目成员的默认分组
     * @param groupId 分组id
     * @param projectId 项目id
     * @return
     */
    @PutMapping("/{groupId}/group")
    public JSONObject updateMemberGroup(@PathVariable(value = "groupId")String groupId,@RequestParam(value = "projectId") String projectId){
        JSONObject object = new JSONObject();
        try{
            String userId = ShiroAuthenticationManager.getUserId();
            projectMemberService.updateDefaultGroup(projectId,userId,groupId);
            object.put("result",1);
            object.put("msg","更新成功");
        }catch(Exception e){
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 获取某个项目的所有成员
     * @param projectId 项目id
     * @return
     */
    @GetMapping("/{projectId}/member")
    public JSONObject getProjectMembers(@PathVariable("projectId") String projectId){
        JSONObject jsonObject = new JSONObject();
        try{
            List<UserEntity> users = userService.getProjectMembers(projectId);
            jsonObject.put("data",users);
            jsonObject.put("result",1);
            jsonObject.put("msg","获取成功!");
        }catch(Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 获取到模块在当前项目的的参与者信息与非参与者信息
     * @param type 模块类型
     * @param id 信息id
     * @param projectId 所在项目id
     * @return 该项目成员在当前模块信息中的参与者信息与非参与者信息
     */
    @GetMapping("/member_info")
    public JSONObject getModelProjectMember(String type, String id, String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("joinInfo",projectMemberService.getModelProjectMember(type,id,projectId));
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常,项目成员信息获取失败!");
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 获取一个用户的星标项目和非星标项目
     * @return 项目集合
     */
    @GetMapping("/star")
    public JSONObject getStarProject(){
        JSONObject jsonObject = new JSONObject();
        try {
            String userId = ShiroAuthenticationManager.getUserId();
            jsonObject.put("starProject",projectMemberService.getStarProject(userId));
            jsonObject.put("notStarProject",projectMemberService.getNotStarProject(userId));
            jsonObject.put("result",1);
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException(e);
        }
    }

    /**
     * 获取当前用户参加或者创建的项目中的所有成员信息
     * @return 项目成员信息
     */
    @GetMapping
    public JSONObject getMembers(){
        String userId = ShiroAuthenticationManager.getUserId();
        return success(projectMemberService.getMembers(userId));
    }

    /**
     * 获取项目中的某个成员信息
     * @param projectId 项目id
     * @param keyWord 用户名
     * @return 用户信息
     */
    @GetMapping("/project/{projectId}")
    public JSONObject getProjectUser(@Validated @PathVariable
                                     @NotNull(message = "项目id不能为空！") String projectId,

                                     @Validated @RequestParam
                                     @NotNull(message = "用户名不能为空") String keyWord){

        log.info("Get project user Info. [{},{}]",projectId, keyWord);

        return success(projectMemberService.getProjectUserInfo(projectId, keyWord));
    }
}
