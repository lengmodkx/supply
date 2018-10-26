package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.relation.RelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author heshaohua
 * @Title: RelationApi
 * @Description: TODO 分组/菜单 api
 * @date 2018/9/13 13:44
 **/
@Slf4j
@RequestMapping("relations")
@RestController
public class RelationApi {

    @Resource
    private RelationService relationService;

    /**
     * 添加菜单
     * @return
     */
    @PostMapping("/{projectId}/menu")
    public JSONObject addMenu(@PathVariable(value = "projectId") String projectId,
                              @RequestParam(value = "groupId") String groupId,
                              @RequestParam(value = "menuName") String menuName){
        JSONObject jsonObject = new JSONObject();
        try {
            Relation relation = new Relation();
            relation.setRelationName(menuName);
            relation.setProjectId(projectId);
            relation.setParentId(groupId);
            relationService.saveMenu(relation);
            jsonObject.put("menuId",relation.getRelationId());
            jsonObject.put("result",1);
        }catch (Exception e){
            log.error("添加关系异常:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 排序菜单顺序
     * @param menuIds 排序后的菜单数组
     * @return
     */
    @PutMapping("/{menuIds}/orderMenu")
    public JSONObject orderMenu(@PathVariable("menuIds") String[] menuIds){
        JSONObject jsonObject = new JSONObject();
        try {
            relationService.orderMenu(menuIds);
            jsonObject.put("msg","排序成功!");
            jsonObject.put("result",1);
        }catch (Exception e){
            log.error("排序异常:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 添加分组
     * @return
     */
    @PostMapping("/{projectId}/group")
    public JSONObject addMenu(
            @PathVariable(value = "projectId") String projectId,
            @RequestParam(value = "groupName") String groupName){
        JSONObject jsonObject = new JSONObject();
        try {
            Relation relation = new Relation();
            relation.setRelationName(groupName);
            relation.setProjectId(projectId);
            relationService.saveGroup(relation);
            jsonObject.put("groupId",relation.getRelationId());
            jsonObject.put("result",1);
        }catch (Exception e){
            log.error("添加关系异常:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 删除菜单
     * @param menuId 菜单id
     * @return
     */
    @DeleteMapping("/{menuId}/menu")
    public JSONObject deleteMenu(@PathVariable(value = "menuId") String menuId){
        JSONObject jsonObject = new JSONObject();
        try {
            relationService.removeById(menuId);
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常,菜单删除失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 删除分组
     * @param groupId 分组id
     * @return
     */
    @DeleteMapping("/{groupId}/group")
    public JSONObject deleteGroup(@PathVariable(value = "groupId") String groupId){
        JSONObject jsonObject = new JSONObject();
        try {
            relationService.deleteGroup(groupId);
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常,分组删除失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更新菜单的信息
     * @param menuId 菜单id
     * @param menuName 菜单名称
     * @return
     */
    @PutMapping("/{menuId}/menu")
    public JSONObject editMenu(@PathVariable(value = "menuId") String menuId, @RequestParam(value = "menuName") String menuName){
        JSONObject jsonObject = new JSONObject();
        try {
            Relation relation = new Relation();
            relation.setRelationId(menuId);
            relation.setRelationName(menuName);
            relation.setUpdateTime(System.currentTimeMillis());
            relationService.editMenu(relation);
            jsonObject.put("result","1");
        } catch (Exception e){
            log.error("系统异常,菜单编辑失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 将分组移动到回收站中 或者 恢复分组
     * @param relationId 分组id
     * @param groupDel 分组状态
     * @return
     */
    @PutMapping("/{groupId}/recyclebin")
    public JSONObject moveRecycleBin(@PathVariable(value = "groupId") String relationId, @RequestParam(value = "groupDel") String groupDel){
        JSONObject jsonObject = new JSONObject();
        try {
            relationService.moveRecycleBin(relationId,groupDel);
            jsonObject.put("result","1");
        } catch (Exception e){
            log.error("系统异常,移动失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 获取该项目下的所有分组信息
     * @param projectId 项目id
     * @return
     */
    @GetMapping("/{projectId}")
    public JSONObject projectAllGroup(@PathVariable(value = "projectId") String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            List<Relation> groupList = relationService.findAllGroupInfoByProjectId(projectId);
            jsonObject.put("data",groupList);
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常:",e);
            throw new SystemException(e);
        }
        return jsonObject;
    }

}
