package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
@RestController
@RequestMapping("members")
public class MemberInvitationApi {

    @Resource
    private UserService userService;

    @Resource
    private ProjectMemberService projectMemberService;

    @Resource
    private RoleService roleService;

    @Resource
    private ProjectService projectService;


    /**
     * 通过用户账户查询用户
     * @param keyword 关键字
     * @return
     */
    @GetMapping("/{keyword}")
    public JSONObject searchMember(@PathVariable(value = "keyword") String keyword){
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
     * 给项目添加成员
     * @param projectId 项目id
     * @param memberId 用户id
     * @return
     */
    @RequiresPermissions("create:member")
    @PostMapping
    public JSONObject addMember(@RequestParam(value = "projectId") String projectId,@RequestParam(value = "memberId") String memberId){
        JSONObject object = new JSONObject();
        try{
            int exist = projectMemberService.findMemberIsExist(projectId,memberId);
            if(exist>0){
                object.put("result",0);
                object.put("msg","项目成员已存在，请勿重复添加");
                return  object;
            }

            //查询出当前项目的默认分组
            String groupId = projectService.findDefaultGroup(projectId);
            Role roleEntity = roleService.getOne(new QueryWrapper<Role>().eq("role_name","成员"));
            ProjectMember member = new ProjectMember();
            member.setDefaultGroup(groupId);
            member.setProjectId(projectId);
            member.setMemberId(memberId);
            member.setRoleId(roleEntity.getRoleId());
            member.setCreateTime(System.currentTimeMillis());
            member.setUpdateTime(System.currentTimeMillis());
            member.setMemberLabel(0);
            projectMemberService.save(member);
            object.put("result",1);
            object.put("msg","添加成功");
        }catch(Exception e){
            log.error("系统异常,成员添加失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 移除项目参与者
     * @param memberId 成员id
     * @return
     */
    @DeleteMapping("/{memberId}")
    public JSONObject deleteMember(@PathVariable(value = "memberId") String memberId){
        JSONObject object = new JSONObject();
        try{
            projectMemberService.remove(new QueryWrapper<ProjectMember>().eq("member_id",memberId));
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
}
