package com.art1001.supply.api;


import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.service.organization.OrganizationGroupMemberService;
import com.art1001.supply.service.user.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author DindDangMao
 * @since 2019-04-29
 */
@RestController
@RequestMapping("/organization_group_member")
public class OrganizationGroupMemberApi {

    /**
     * 注入群组逻辑层bean
     */
    @Resource
    private OrganizationGroupMemberService organizationGroupMemberService;

    /**
     * 注入用户逻辑层bean
     */
    @Resource
    private UserService userService;


    /**
     * 群组添加成员
     * @param groupId 分组id
     * @param memberId 成员id
     * @return 结果
     */
    @RequiresPermissions("create:group:member")
    @PostMapping("/{groupId}")
    public JSONObject addGroupMember(@PathVariable String groupId, @RequestParam String memberId){
        JSONObject jsonObject = new JSONObject();
        try {
            if(organizationGroupMemberService.addGroupMember(groupId,memberId)){
                jsonObject.put("data",userService.getOne(new QueryWrapper<UserEntity>().eq("user_id", memberId).select("user_id","user_name","image")));
                jsonObject.put("result", 1);
            } else{
                jsonObject.put("result", 0);
            }
            return jsonObject;
        } catch (ServiceException e){
            throw new AjaxException(e.getMessage(),e);
        } catch (Exception e){
            throw new AjaxException("系统异常,添加失败!",e);
        }
    }

    /**
     * 移除群组的成员
     * @param groupId 分组id
     * @param memberId 成员id
     * @return 结果
     */
    @DeleteMapping("/{groupId}/member")
    public JSONObject removeMember(@PathVariable String groupId,@RequestParam String memberId){
        JSONObject jsonObject = new JSONObject();
        try {
            if(organizationGroupMemberService.removeMember(memberId,groupId)){
                jsonObject.put("result", 1);
            } else{
                jsonObject.put("result", 0);
            }
            return jsonObject;
        } catch (ServiceException e){
            throw new AjaxException(e.getMessage(),e);
        } catch (Exception e){
            throw new AjaxException("系统异常,操作失败!",e);
        }
    }

    /**
     * 获取一个群组的所有成员信息
     * @return 成员信息
     */
    @GetMapping("/{groupId}")
    public JSONObject getGroupMembers(@PathVariable String groupId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("data",organizationGroupMemberService.getGroupMembers(groupId));
            return jsonObject;
        } catch (ServiceException e){
            throw new AjaxException(e.getMessage(),e);
        } catch (Exception e){
            throw new AjaxException("系统异常,成员信息获取失败!",e);
        }
    }
}

