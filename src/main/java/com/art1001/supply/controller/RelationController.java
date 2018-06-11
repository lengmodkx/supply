package com.art1001.supply.controller;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.task.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

/**
 * 项目，任务分组，菜单之间的关系处理
 */
@Controller
@Slf4j
@RequestMapping("/relation")
public class RelationController {

    @Resource
    private RelationService relationService;

    @Resource
    private TaskService taskService;
    /**
     * 添加分组/分组下的菜单
     * @param relation
     * @return
     */
    @RequestMapping("/addRelation")
    public JSONObject addRelation(Relation relation){
        JSONObject jsonObject = new JSONObject();
        try {
            relation.setCreateTime(System.currentTimeMillis());
            relationService.saveRelation(relation);
            jsonObject.put("result",1);
            jsonObject.put("msg","添加成功");
        }catch (Exception e){
            log.error("添加关系异常",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    /**
     * 更新分组/分组下的菜单
     * @param relation
     * @return
     */
    @RequestMapping("/updateRelation")
    public JSONObject updateRelation(Relation relation){
        JSONObject jsonObject = new JSONObject();
        try {
            relation.setUpdateTime(System.currentTimeMillis());
            relationService.updateRelation(relation);
            jsonObject.put("result",1);
            jsonObject.put("msg","更新成功");
        }catch (Exception e){
            log.error("更新关系异常",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    /**
     * 删除分组/分组下的菜单
     * @param relationId
     * @return
     */
    @RequestMapping("/delRelation")
    public JSONObject delRelation(@RequestParam String relationId,
                                  @RequestParam String parentId){
        JSONObject jsonObject = new JSONObject();
        try {
            //删除分组
            if(StringUtils.isEmpty(parentId)){
                relationService.deleteRelationByRelationId(relationId);
                jsonObject.put("result",1);
                jsonObject.put("msg","删除成功");
            }else {
                //删除分组的菜单
                int task = taskService.findTaskByMenuId(relationId);
                //判断菜单下面是否有任务，如果有的话提示用户不可以删除菜单，否则可以直接删除
                if(task==0){
                    relationService.deletenMenuByRelationId(relationId);
                    jsonObject.put("result",1);
                    jsonObject.put("msg","删除成功");
                }else{
                    jsonObject.put("result",0);
                    jsonObject.put("msg","请先清空此列表上的任务，然后再删除这个列表");
                }
            }
        }catch (Exception e){
            log.error("删除关系异常",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

}
