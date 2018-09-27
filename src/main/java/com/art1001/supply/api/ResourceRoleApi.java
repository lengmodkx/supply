package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.role.ResourcesRole;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.role.ResourcesRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
                jsonObject.put("error","必须选择至少一个资源!");
                return jsonObject;
            }
            resourcesRoleService.remove(new QueryWrapper<ResourcesRole>().eq("r_id",roleId));
            String[] sIds = resources.split(",");
            List<ResourcesRole> resourcesRoleList = new ArrayList<ResourcesRole>();
            for (int i = 0;i < sIds.length;i++){
                ResourcesRole resourcesRole = new ResourcesRole();
                resourcesRole.setRId(Integer.valueOf(roleId));
                resourcesRole.setSId(Integer.valueOf(sIds[i]));
                resourcesRole.setTCreateTime(LocalDateTime.now());
                resourcesRoleList.add(resourcesRole);
            }
            resourcesRoleService.saveBatch(resourcesRoleList);
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("角色资源分配出现异常:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }



}
