package com.art1001.supply.api;


import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.organization.OrganizationGroup;
import com.art1001.supply.entity.organization.OrganizationGroupMember;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.organization.OrganizationGroupService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author shaohua
 * @since 2019-04-29
 */
@RestController
@RequestMapping("/organization_group")
public class OrganizationGroupApi {

    /**
     * 注入企业群组逻辑层Bean
     */
    @Resource
    private OrganizationGroupService organizationGroupService;

    /**
     * 创建企业群组
     * @param orgId 企业id
     * @param groupName 群组名称
     * @param memberIds 成员id数组
     * @return 结果
     */
    @RequiresPermissions("create:group")
    @PostMapping("/{orgId}")
    public JSONObject createGroup(@PathVariable String orgId, @RequestParam String groupName, @RequestParam(required = false) String[] memberIds){
        JSONObject jsonObject = new JSONObject();
        try {
            OrganizationGroup organizationGroup = new OrganizationGroup();
            organizationGroup.setOrganizationId(orgId);
            organizationGroup.setGroupName(groupName);
            if(organizationGroupService.createGroup(organizationGroup,memberIds)){
                jsonObject.put("result", 1);
                jsonObject.put("data", organizationGroup);
            }
            return jsonObject;
        } catch (ServiceException e){
            throw new AjaxException(e.getMessage(),e);
        } catch (Exception e){
            throw new AjaxException("系统异常,创建失败!",e);
        }
    }

    /**
     * 删除群组
     * @param groupId 群组id
     * @return 结果
     */
    @DeleteMapping("/{groupId}")
    public JSONObject removeGroup(@PathVariable String groupId){
        JSONObject jsonObject = new JSONObject();
        try {
            if(organizationGroupService.removeGroup(groupId)){
                jsonObject.put("result", 1);
            } else{
                jsonObject.put("result", 0);
            }
            return jsonObject;
        } catch (ServiceException e){
            throw new AjaxException(e.getMessage(),e);
        } catch (Exception e){
            throw new AjaxException("系统异常,删除失败",e);
        }
    }

    /**
     * 更新分组信息
     * @param groupId 分组id
     * @return 结果
     */
    @PutMapping("/{groupId}")
    public JSONObject updateGroup(@PathVariable String groupId, @RequestParam String groupName){
        JSONObject jsonObject = new JSONObject();
        try {
            if(organizationGroupService.getById(groupId) == null){
                jsonObject.put("result", 0);
                jsonObject.put("msg", "该分组不存在!");
                return jsonObject;
            }
            OrganizationGroup organizationGroup = new OrganizationGroup();
            organizationGroup.setGroupId(groupId);
            organizationGroup.setGroupName(groupName);
            organizationGroup.setUpdateTime(System.currentTimeMillis());
            if(organizationGroupService.updateById(organizationGroup)){
                jsonObject.put("result", 1);
            }
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,更新失败!",e);
        }
    }

    /**
     * 获取企业下的群组信息
     * @param orgId 企业id
     * @return 群组信息
     */
    @GetMapping("{orgId}")
    public JSONObject getOrgGroups(@PathVariable String orgId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",organizationGroupService.getOrgGroups(orgId));
            return jsonObject;
        } catch (ServiceException e){
            throw new SystemException(e.getMessage(),e);
        }
        catch (Exception e){
            throw new SystemException("系统异常,获取群组信息失败!");
        }
    }
}

