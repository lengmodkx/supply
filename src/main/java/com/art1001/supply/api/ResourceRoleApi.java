package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.resource.ResourceEntity;
import com.art1001.supply.entity.role.ResourcesRole;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.resource.ResourceService;
import com.art1001.supply.service.role.ResourcesRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author heshaohua
 * @Title: ResourceRoleApi
 * @Description: TODO
 * @date 2018/9/26 15:34
 **/
@RequestMapping("resource_role")
@Slf4j
@RestController
public class ResourceRoleApi {

    /**
     * 角色资源逻辑层
     */
    @Resource
    private ResourcesRoleService resourcesRoleService;

    @Resource
    private ResourceService resourceService;

    /**
     * 分配权限
     * @return
     */
    @PostMapping("/{role}/edit_resource")
    public JSONObject editResource(@PathVariable(value = "role") String roleId,
                                   @RequestParam(value = "resources") String resources){
        JSONObject jsonObject = new JSONObject();
        try {
            if(StringUtils.isEmpty(resources)){
                jsonObject.put("result",0);
                jsonObject.put("msg","必须选择至少一个资源!");
            } else{
                resourcesRoleService.remove(new QueryWrapper<ResourcesRole>().eq("r_id",roleId));
                String[] sNames = resources.split(",");
                List<Integer> collect = resourceService.list(new QueryWrapper<ResourceEntity>().lambda().in(ResourceEntity::getResourceName, Arrays.asList(sNames))).stream().map(ResourceEntity::getResourceId).collect(Collectors.toList());
                List<ResourcesRole> resourcesRoleList = new ArrayList<ResourcesRole>();
                for (Integer integer : collect) {
                    ResourcesRole resourcesRole = new ResourcesRole();
                    resourcesRole.setRoleId(Integer.valueOf(roleId));
                    resourcesRole.setResourceId(integer);
                    resourcesRole.setCreateTime(LocalDateTime.now());
                    resourcesRoleList.add(resourcesRole);
                }
                resourcesRoleService.saveBatch(resourcesRoleList);
                jsonObject.put("result",1);
            }
        } catch (Exception e){
            log.error("角色资源分配出现异常:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 点击编辑角色的 界面数据展示
     * @param roleId 角色id
     * @return
     */
    @GetMapping("/{role}")
    public JSONObject showRoleResources(@PathVariable(value = "role") String roleId){
        JSONObject jsonObject = new JSONObject();
        try {
            List<ResourceEntity> resources = resourcesRoleService.showRoleResources(roleId);
            jsonObject.put("data",resources);
        } catch (Exception e){
            log.error("系统异常,角色权限数据拉取失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }



}
