package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.resource.ResourceEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.resource.ResourceService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 资源api
 * @author 汪亚锋
 * [POST]   // 新增
 * [GET]    // 查询
 * [PATCH]  // 更新
 * [PUT]    // 覆盖，全部更新
 * [DELETE] // 删除
 */
@Slf4j
@RestController
@RequestMapping("resources")
public class ResourceApi {

    @Resource
    private ResourceService resourceService;

    @PostMapping
    public JSONObject addResource(@RequestParam(value = "resourceType") Integer resourceType,
                                  @RequestParam(value = "parentId",defaultValue = "0") Integer parentId,
                                  @RequestParam(value = "resourceName") String resourceName,
                                  @RequestParam(value = "resourceKey") String resourceKey,
                                  @RequestParam(value = "resourceUrl") String resourceUrl,
                                  @RequestParam(value = "resourceDes") String resourceDes){
        JSONObject object = new JSONObject();
        try{
            ResourceEntity resourceEntity = new ResourceEntity();
            resourceEntity.setName(resourceName);
            resourceEntity.setSourceKey(resourceKey);
            resourceEntity.setSourceUrl(resourceUrl);
            resourceEntity.setDescription(resourceDes);
            resourceEntity.setParentId(parentId);
            resourceEntity.setType(resourceType);
            if(parentId!=0){
                resourceEntity.setLevel(2);
            }else{
                resourceEntity.setLevel(1);
            }

            resourceEntity.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
            resourceEntity.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
            resourceService.save(resourceEntity);
            object.put("result",1);
            object.put("msg","插入成功");
        }catch(Exception e){
            log.error("插入失败，{}",e);
            throw new AjaxException(e);
        }
        return object;
    }

    @DeleteMapping("/{resourceId}")
    public JSONObject deleteResource(@PathVariable Integer resourceId){
        JSONObject object = new JSONObject();
        try{
            try {
                int result = resourceService.deleteResource(resourceId);
                if(result==0){
                    object.put("msg","存在子资源，不允许删除");
                    object.put("result",0);
                }else{
                    object.put("msg","删除成功");
                    object.put("result",1);
                }

            }catch (Exception e){
                log.error("删除失败，{}",e);
                throw new AjaxException(e);
            }
        }catch(Exception e){
            throw new AjaxException(e);
        }
        return object;
    }

    @PutMapping("/{resourceId}")
    public JSONObject updateResource(@PathVariable Integer resourceId,
                                     @RequestParam(value = "resourceType") Integer resourceType,
                                     @RequestParam(value = "parentId") Integer parentId,
                                     @RequestParam(value = "resourceName") String resourceName,
                                     @RequestParam(value = "resourceKey") String resourceKey,
                                     @RequestParam(value = "resourceUrl") String resourceUrl,
                                     @RequestParam(value = "resourceDes") String resourceDes,
                                     @RequestParam(value = "resourceLevel") Integer resourceLevel){
        JSONObject object = new JSONObject();
        try{
            ResourceEntity resourceEntity = new ResourceEntity();
            resourceEntity.setId(resourceId);
            resourceEntity.setName(resourceName);
            resourceEntity.setSourceKey(resourceKey);
            resourceEntity.setSourceUrl(resourceUrl);
            resourceEntity.setDescription(resourceDes);
            resourceEntity.setParentId(parentId);
            resourceEntity.setType(resourceType);
            resourceEntity.setLevel(resourceLevel);
            resourceEntity.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
            resourceService.updateById(resourceEntity);
            object.put("result",1);
            object.put("msg","");
        }catch(Exception e){
            throw new AjaxException(e);
        }
        return object;
    }

    
    @GetMapping("/{current}/{size}")
    public JSONObject resourceList(@RequestParam(value = "resourceName",required = false)String resourceName,
                                   @PathVariable(value = "current")Long current,
                                   @PathVariable(value = "size")Long size){
        JSONObject object = new JSONObject();
        try{
            ResourceEntity resourceEntity = new ResourceEntity();
            resourceEntity.setName(resourceName);
            Page<ResourceEntity> roleList = resourceService.selectListPage(current, size, resourceEntity);
            object.put("data",roleList);
            object.put("result",1);
            object.put("msg","");
        } catch(Exception e){
            throw new AjaxException(e);
        }
        return object;
    }

}
