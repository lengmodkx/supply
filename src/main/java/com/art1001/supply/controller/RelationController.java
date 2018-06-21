package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.dtgrid.model.Column;
import com.art1001.supply.dtgrid.model.Pager;
import com.art1001.supply.dtgrid.util.ExportUtils;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.task.TaskService;
import io.netty.handler.codec.json.JsonObjectDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.print.attribute.standard.JobSheets;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.beans.ExceptionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 排序分组中的菜单
     * @param relationId 分组id
     * @return
     */
    @PostMapping("menuSort")
    @ResponseBody
    public JSONObject menuSort(String relationId){
        JSONObject jsonObject = new JSONObject();
        try {
            List<Relation> relationList = relationService.menuSort(relationId);
            jsonObject.put("data",relationList);
            jsonObject.put("result","1");
        } catch (Exception e){
            log.error("系统异常,菜单排序失败!");
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 添加菜单
     * @param parentId 父级分组id
     * @param relation 菜单信息
     * @return
     */
    @PostMapping("addMenu")
    @ResponseBody
    public JSONObject addMenu(String parentId, Relation relation){
        JSONObject jsonObject = new JSONObject();
        try {
            relationService.addMenu(parentId,relation);
            jsonObject.put("msg","添加成功!");
            jsonObject.put("result","1");
        } catch (Exception e){
            log.error("系统异常,创建列表失败!",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更新菜单的信息
     * @param relation
     * @return
     */
    @PostMapping("editMenu")
    @ResponseBody
    public JSONObject editMenu(Relation relation){
        JSONObject jsonObject = new JSONObject();
        try {
            relationService.editMenu(relation);
            jsonObject.put("msg","信息更新成功!");
            jsonObject.put("result","1");
        } catch (Exception e){
            log.error("系统异常,菜单编辑失败!",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 菜单排序任务
     * @param relationId 菜单id
     */
    @PostMapping("taskSort")
    @ResponseBody
    public JSONObject taskSort(String relationId){
        JSONObject jsonObject = new JSONObject();
        try {
            Relation relation = relationService.taskSort(relationId);
            jsonObject.put("data",relation.getTaskList());
            jsonObject.put("result","1");
        } catch (Exception e){
            log.error("系统异常,排序失败!",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 将分组移动到回收站中
     * @param relationId
     * @param relationDel
     * @return
     */
    @PostMapping("moveRecycleBin")
    @ResponseBody
    public JSONObject moveRecycleBin(String relationId,String relationDel){
        JSONObject jsonObject = new JSONObject();
        try {
            relationService.moveRecycleBin(relationId,relationDel);
            jsonObject.put("msg","成功将任务移至回收站!");
            jsonObject.put("result","1");
        } catch (Exception e){
            log.error("系统异常,移动失败",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 恢复任务分组
     * @param relationId 任务分组的id
     * @param relationDel 任务分组的状态
     */
    @PostMapping("recoveryRelation")
    @ResponseBody
    public JSONObject recoveryRelation(String relationId,String relationDel){
        JSONObject jsonObject = new JSONObject();
        try {
            relationService.moveRecycleBin(relationId,relationDel);
            jsonObject.put("msg","恢复成功!");
            jsonObject.put("result","1");
        } catch (Exception e){
            log.error("系统异常,恢复分组失败!",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    @GetMapping("exportTaskInfo")
    public void exportTaskInfo(HttpServletResponse response){
        Pager pager = new Pager();
        List<Task> taskAllList = taskService.findTaskAllList();
        List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
        List<Column> column = new ArrayList<Column>();
        for (Task task: taskAllList) {

        }
        pager.setExportColumns(column);
        pager.setExportDatas(list);
        pager.setExportFileName("任务数据");
        pager.setExportType("EXCEL");
        try {
            ExportUtils.export(response,pager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
