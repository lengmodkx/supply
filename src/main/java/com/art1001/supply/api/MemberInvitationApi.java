package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.role.RoleEntity;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
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
    @PostMapping
    public JSONObject addMember(@RequestParam(value = "projectId") String projectId,@RequestParam(value = "memberId") String memberId){
        JSONObject object = new JSONObject();
        try{
            RoleEntity roleEntity = roleService.findByName("成员");
            ProjectMember member = new ProjectMember();
            member.setProjectId(projectId);
            member.setMemberId(memberId);
            member.setRId(roleEntity.getId());
            projectMemberService.saveProjectMember(member);
            object.put("result",1);
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
            projectMemberService.deleteProjectMemberById(memberId);
            object.put("result",1);
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
            RoleEntity roleEntity = roleService.findByName("成员");

            object.put("result",1);
            object.put("msg","更新成功");
        }catch(Exception e){
            throw new AjaxException(e);
        }
        return object;
    }



}
