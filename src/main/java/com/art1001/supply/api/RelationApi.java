package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.relation.GroupVO;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.relation.RelationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author heshaohua
 * @Title: RelationApi
 * @Description: TODO 分组/菜单 api
 * @date 2018/9/13 13:44
 **/
@Slf4j
@RequestMapping("relations")
@RestController
public class  RelationApi {

    @Resource
    private RelationService relationService;

    /**
     * 添加菜单
     * @return
     */
    @Push(PushType.H2)
    @PostMapping("/{projectId}/menu")
    public JSONObject addMenu(@PathVariable(value = "projectId") String projectId,
                              @RequestParam(value = "groupId") String groupId,
                              @RequestParam(value = "menuName") String menuName,
                              @RequestParam(required = false) Integer order
    ){
        JSONObject jsonObject = new JSONObject();
        try {




            Relation relation = new Relation();
            relation.setRelationName(menuName);
            relation.setProjectId(projectId);
            relation.setParentId(groupId);
            relation.setOrder(order);
            relationService.saveMenu(relation);
            jsonObject.put("menuId",relation.getRelationId());
            jsonObject.put("result",1);
            jsonObject.put("msgId", projectId);
            jsonObject.put("data", projectId);
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
    public Result addGroup(
            @PathVariable(value = "projectId") String projectId,
            @RequestParam(value = "groupName") String groupName){
        try {
            Relation relation = new Relation();
            relation.setRelationName(groupName);
            relation.setProjectId(projectId);
            relationService.saveGroup(relation);
            GroupVO groupVO = new GroupVO();
            groupVO.setGroupId(relation.getRelationId());
            groupVO.setGroupName(relation.getRelationName());
            groupVO.setCompletePercentage("0");
            groupVO.setBeOverdue(0);
            groupVO.setCompleteCount(0);
            groupVO.setNotCompleteCount(0);

            return Result.success(relation);
        }catch (Exception e){
            log.error("添加关系异常:",e);
            throw new AjaxException(e);
        }
    }

    /**
     * 删除菜单
     * @param menuId 菜单id
     * @return 是否成功
     */
    @Push(value = PushType.H8,type = 1)
    @DeleteMapping("/{menuId}/menu")
        public JSONObject deleteMenu(@PathVariable(value = "menuId") String menuId){
        JSONObject jsonObject = new JSONObject();
        try {
            String relationProjectId = this.getRelationProjectId(menuId);
            if(relationService.removeMenu(menuId)){
                jsonObject.put("result", 1);
            } else {
                jsonObject.put("result", 0);
            }
            jsonObject.put("data", relationProjectId);
            jsonObject.put("msgId",relationProjectId);
        } catch (ServiceException e){
            throw new AjaxException(e.getMessage(),e);
        } catch (Exception e){
            throw new AjaxException("系统异常,删除失败!",e);
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
     * @return 是否成功
     */
    @Push(value = PushType.H1)
    @PutMapping("/{menuId}/menu")
        public JSONObject editMenu(@PathVariable(value = "menuId") String menuId, @RequestParam(value = "menuName") String menuName){
        JSONObject jsonObject = new JSONObject();
        try {
            Relation relation = new Relation();
            relation.setRelationId(menuId);
            relation.setRelationName(menuName);
            relation.setUpdateTime(System.currentTimeMillis());
            relationService.editMenu(relation);
            String relationProjectId = this.getRelationProjectId(menuId);
            jsonObject.put("msgId",relationProjectId);
            jsonObject.put("data",relationProjectId);
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

    /**
     * 获取分组下的所有菜单信息
     * @param groupId
     * @return
     */
    @GetMapping("/{groupId}/menus")
    public JSONObject getMenus(@PathVariable("groupId") String groupId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",relationService.findAllMenuInfoByGroupId(groupId));
            jsonObject.put("result",1);
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,获取菜单信息失败!",e);
        }
    }

    /**
     * 获取一个项目下的分组信息(分组id,分组名称,分组创建者) 根据order排序
     */
    @GetMapping("/{projectId}/bind/group_info")
    public JSONObject getGroupByProject(@PathVariable String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",relationService.loadGroupInfo(projectId));
            jsonObject.put("result",1);
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,获取分组信息失败!",e);
        }
    }

    /**
     * 获取一个分组下的任务和菜单信息
     * @return 信息
     */
    @GetMapping("{groupId}/bind/menu_info")
    public JSONObject getBindingMenuInfo(@PathVariable("groupId") String groupId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", relationService.bindMenuInfo(groupId));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常,信息获取失败!", e);
        }
    }

    /**
     * 获取一个项目下的分组信息
     * 分组信息内 包括改分组的任务完成情况 优先级分布等
     * @param projectId 项目id
     * @return 分组id
     */
    @GetMapping("/{projectId}/groups")
    public JSONObject getGroupsAndTaskCountInfo(@PathVariable String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",relationService.getGroupsInfo(projectId));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e){
            e.printStackTrace();
            throw new SystemException("系统异常,数据获取失败!",e);
        }
    }

    /**
     * 设置本列表所有的任务截止时间
     * @param menuId 列表id
     * @param endTime 截止时间
     * @return 是否成功
     */
    @Push(PushType.H3)
    @PutMapping("/{menuId}/task_end_time")
    public JSONObject setAllTaskEndTime(@PathVariable String menuId,Long endTime){
        JSONObject jsonObject = new JSONObject();
        try {
            if(relationService.setAllTaskEndTime(menuId, endTime) == 1){
                jsonObject.put("result", 1);
                String relationProjectId = this.getRelationProjectId(menuId);
                jsonObject.put("msgId",relationProjectId);
                jsonObject.put("data", relationProjectId);
            } else{
                jsonObject.put("result", 0);
            }
            return jsonObject;
        } catch (ServiceException e){
            throw new AjaxException(e.getMessage(),e);
        } catch (Exception e){
            throw new AjaxException("系统异常,设置截止时间失败!",e);
        }
    }

    /**
     * 移动列表下的所有任务
     * @param menuId 菜单id
     * @param projectId 移动到的项目id
     * @param groupId 移动到的分组id
     * @param toMenuId 移动到的菜单id
     * @return 结果
     */
    @Push(type = 2,value = PushType.H4)
    @PutMapping("/{menuId}/move_all_task")
    public JSONObject moveAllTask(@PathVariable String menuId, String projectId, String groupId, String toMenuId){
        JSONObject jsonObject = new JSONObject();
        try {
            if(relationService.moveAllTask(menuId,projectId,groupId,toMenuId)){
                jsonObject.put("result", 1);
                String relationProjectId = this.getRelationProjectId(menuId);
                Map<String,Object> maps = new HashMap<String,Object>(2);
                if(relationProjectId.equals(projectId)){
                    maps.put(projectId,projectId);
                } else{
                    maps.put(projectId,projectId);
                    maps.put(relationProjectId,relationProjectId);
                }
                jsonObject.put("data", maps);
            } else{
                jsonObject.put("result", 0);
            }
            return jsonObject;
        } catch (ServiceException e){
            throw new AjaxException(e.getMessage(),e);
        } catch (Exception e){
            throw new AjaxException("系统异常,移动事失败!",e);
        }
    }

    /**
     * 复制列表下的所有任务
     * @param menuId 列表id
     * @param projectId 项目id
     * @param groupId 分组id
     * @param toMenuId 复制到的列表id
     * @return 结果
     */
    @Push(type = 1,value = PushType.H5)
    @PostMapping("/{menuId}/copy_all_task")
    public JSONObject copyAllTask(@PathVariable String menuId, String projectId, String groupId, String toMenuId){
        JSONObject jsonObject = new JSONObject();
        try {
            if(relationService.copyAllTask(menuId,projectId,groupId,toMenuId)){
                String relationProjectId = this.getRelationProjectId(toMenuId);
                jsonObject.put("result", 1);
                jsonObject.put("msgId", relationProjectId);
                jsonObject.put("data", relationProjectId);
            } else{
                jsonObject.put("result", 0);
            }
            return jsonObject;
        } catch (ServiceException e){
            throw new AjaxException(e.getMessage(),e);
        } catch (Exception e){
            throw new AjaxException("系统异常,复制失败!",e);
        }
    }

    /**
     * 列表所有任务移动到回收站
     * @param menuId 列表id
     * @return 结果
     */
    @Push(PushType.H6)
    @PutMapping("/{menuId}/move_recycle_bin")
    public JSONObject allTaskCycToBin(@PathVariable String menuId){
        JSONObject jsonObject = new JSONObject();
        try {
            if(relationService.allTaskMoveRecycleBin(menuId)){
                String relationProjectId = this.getRelationProjectId(menuId);
                jsonObject.put("result", 1);
                jsonObject.put("data", relationProjectId);
                jsonObject.put("msgId",relationProjectId);
            } else {
                jsonObject.put("result", 0);
            }
            return jsonObject;
        } catch (ServiceException e){
            throw new AjaxException(e.getMessage(),e);
        } catch (Exception e){
            throw new AjaxException("系统异常,复制失败!",e);
        }
    }

    /**
     * 设置此列表的所有执行者
     * @param menuId 列表id
     * @param executor 执行者id
     * @return 是否成功
     */
    @Push(PushType.H7)
    @PutMapping("/{menuId}/all_task_executor")
    public JSONObject setAllTaskExecutor(@PathVariable String menuId,String executor){
        JSONObject jsonObject = new JSONObject();
        try {
            if(relationService.setAllTaskExecutor(menuId,executor)){
                String relationProjectId = this.getRelationProjectId(menuId);
                jsonObject.put("result", 1);
                jsonObject.put("data", relationProjectId);
                jsonObject.put("msgId",relationProjectId);
            } else {
                jsonObject.put("result", 0);
            }
            return jsonObject;
        } catch (ServiceException e){
            throw new AjaxException(e.getMessage(),e);
        } catch (Exception e){
            throw new AjaxException("系统异常,复制失败!",e);
        }
    }

    /**
     * 检查是否有访问分组的权限
     * @return 结果
     */
    @GetMapping("/check_access/permissions")
    public JSONObject checkUserIsExistGroup(@RequestParam
                                                @Validated
                                                @NotBlank(message = "分组id不能为空！") String groupId){
        log.info("Check user is exist permissions.");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", relationService.checkUserIsExistGroup(groupId));
        jsonObject.put("result", 0);
        return jsonObject;
    }

    /**
     * 获取菜单或者分组的项目id
     * @param relationId 菜单/分组id
     * @return 项目id
     */
    private String getRelationProjectId(String relationId){
        return relationService.getOne(new QueryWrapper<Relation>().lambda().eq(Relation::getRelationId,relationId ).select(Relation::getProjectId)).getProjectId();
    }



}
