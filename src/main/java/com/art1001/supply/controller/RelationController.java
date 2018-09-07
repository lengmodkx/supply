package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.dtgrid.model.Column;
import com.art1001.supply.dtgrid.model.Pager;
import com.art1001.supply.dtgrid.util.ExportUtils;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.relation.GroupVO;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskMenuVO;
import com.art1001.supply.entity.task.PushType;
import com.art1001.supply.entity.user.UserInfoEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    @Resource
    private ProjectService projectService;


    /**
     * 添加分组/分组下的菜单
     * @param relation
     * @return
     */
    @RequestMapping("/addRelation")
    @ResponseBody
    public JSONObject addRelation(Relation relation,String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            relation.setCreator(ShiroAuthenticationManager.getUserId());
            relation.setCreateTime(System.currentTimeMillis());
            relationService.saveRelation(relation);
            //初始化菜单
            String[] menus  = new String[]{"待处理","进行中","已完成"};
            relationService.saveRelationBatch(Arrays.asList(menus),projectId,relation.getRelationId());

            jsonObject.put("groupId",relation.getRelationId());
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
    @ResponseBody
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
    @ResponseBody
    public JSONObject delRelation(@RequestParam String relationId,
                                  String parentId){
        JSONObject jsonObject = new JSONObject();
        try {
            //删除分组
            if(StringUtils.isEmpty(parentId)){
                relationService.deleteGroup(relationId);
                jsonObject.put("result",1);
                jsonObject.put("msg","删除成功");
            }else {
                relationService.deleteRelationById(relationId);
                jsonObject.put("result",1);
                jsonObject.put("msg","删除成功");
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
     * @param projectId 项目id
     * @param parentId 父级分组id
     * @param relation 菜单信息
     * @return
     */
    @PostMapping("addMenu")
    @ResponseBody
    public JSONObject addMenu(String projectId,String parentId, Relation relation){
        JSONObject jsonObject = new JSONObject();
        try {
            relation.setRelationId(IdGen.uuid());
            relation.setProjectId(projectId);
            relationService.addMenu(parentId,relation);
            PushType taskPushType = new PushType("添加菜单");
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("menu",relation);
            taskPushType.setObject(map);
            simpMessagingTemplate.convertAndSend("/topic/"+projectId,new ServerMessage(JSON.toJSONString(taskPushType)));
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
     * @param relationId 分组id
     * @param relationDel 分组状态
     * @return
     */
    @PostMapping("moveRecycleBin")
    @ResponseBody
    public JSONObject moveRecycleBin(String relationId,String relationDel){
        JSONObject jsonObject = new JSONObject();
        try {
            relationService.moveRecycleBin(relationId,relationDel);
            jsonObject.put("msg","成功将分组移至回收站!");
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

    /**
     * 导出任务数据
     * @param response
     * @return
     */
    @GetMapping("exportTaskInfo")
    public JSONObject exportTaskInfo(HttpServletResponse response){
        JSONObject jsonObject = new JSONObject();
        Pager pager = new Pager();
        List<Column> columnList = new ArrayList<Column>();
        String[] titles = new String[]{"任务名称","开始时间","结束时间","优先级","备注","层级","创建者","创建时间"};
        int i = 0;
        //设置表头对象
        for (String title: titles) {
            i++;
            Column column = new Column();
            column.setId(String.valueOf(i));
            column.setTitle(title);
            column.setWidth("500");
            columnList.add(column);
        }
        //查询出要导出的任务信息
        List<Task> taskAllList = taskService.findTaskAllList();
        List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
        for (Task task : taskAllList) {
            i = 0;
            Map<String,Object> map = new HashMap<String,Object>();
            map.put(String.valueOf(i+=1),task.getTaskName());
            map.put(String.valueOf(i+=1),task.getStartTime());
            map.put(String.valueOf(i+=1),task.getEndTime());
            map.put(String.valueOf(i+=1),task.getPriority());
            map.put(String.valueOf(i+=1),task.getRemarks());
            map.put(String.valueOf(i+=1),task.getLevel());
            map.put(String.valueOf(i+=1),task.getMemberId());
            map.put(String.valueOf(i+=1),task.getCreateTime());
            list.add(map);
        }
        pager.setExportColumns(columnList);
        pager.setExportDatas(list);
        pager.setExportFileName("任务数据");
        pager.setExportType("EXCEL");
        try {
            ExportUtils.export(response,pager);
            jsonObject.put("msg","数据导出成功!");
            jsonObject.put("result","1");
        } catch (Exception e) {
            log.error("系统异常,数据导出失败!");
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    /**
     * 更新一个菜单下所有的任务执行者
     * @param relationId 菜单id
     * @param userInfoEntity 用户信息
     * @param uName 用户名字
     */
    @PostMapping("setMenuAllTaskExecutor")
    @ResponseBody
    public void setMenuAllTaskExecutor(String relationId,UserInfoEntity userInfoEntity,String uName){
        JSONObject jsonObject = new JSONObject();
        try {
            relationService.setMenuAllTaskExecutor(relationId,userInfoEntity,uName);
            jsonObject.put("msg","更新成功!");
            jsonObject.put("result","1");
        } catch (Exception e){
            log.error("系统异常,更新失败!",e);
            throw new AjaxException(e);
        }
    }

    /**
     * 设置此菜单下所有的任务的截止时间
     * @param relationId
     * @param endTime
     * @return
     */
    @PostMapping("setMenuAllTaskEndTime")
    @ResponseBody
    public JSONObject setMenuAllTaskEndTime(@RequestParam("relationId") String relationId,@RequestParam("endTime") Long endTime){
        JSONObject jsonObject = new JSONObject();
        try {
            relationService.setMenuAllTaskEndTime(relationId,endTime);
            jsonObject.put("msg","设置成功!");
            jsonObject.put("result","1");
        } catch (Exception e){
            log.error("系统异常,设置失败!");
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 移动列表下的所有任务
     * @param oldTaskMenuVO 原来的位置信息
     * @param newTaskMenuVO 新的位置信息
     */
    @PostMapping("moveMenuAllTask")
    @ResponseBody
    public JSONObject moveMenuAllTask(TaskMenuVO oldTaskMenuVO,TaskMenuVO newTaskMenuVO){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg","移动成功!");
        jsonObject.put("result","1");
        try {
            relationService.moveMenuAllTask(oldTaskMenuVO,newTaskMenuVO);

        } catch (Exception e){
            log.error("系统异常,移动任务失败!");
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 复制列表下所有的任务
     * @param oldTaskMenuVO 旧的任务位置
     * @param newTaskMenuVO 新的任务位置
     * @return
     */
    @PostMapping("copyMenuAllTask")
    @ResponseBody
    public JSONObject copyMenuAllTask(@RequestParam TaskMenuVO oldTaskMenuVO,@RequestParam TaskMenuVO newTaskMenuVO){
        JSONObject jsonObject = new JSONObject();
        try {
            relationService.copyMenuAllTask(oldTaskMenuVO,newTaskMenuVO);
            jsonObject.put("msg","复制成功!");
            jsonObject.put("result","1");
        } catch (Exception e){
            log.error("系统异常,任务复制失败!");
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 把菜单下的所有任务移入回收站
     * @param relationId 菜单id
     * @return
     */
    @PostMapping("menuAllTaskToRecycleBin")
    @ResponseBody
    public JSONObject menuAllTaskToRecycleBin(String relationId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", "已经该列表下的任务全部移入回收站!");
        jsonObject.put("result", "1");
        try {
            relationService.menuAllTaskToRecycleBin(relationId);
        } catch (Exception e) {
            log.error("系统异常,移入回收站失败!", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 获取该项目下的所有分组信息
     * @param projectId 项目id
     * @return
     */
    @PostMapping("projectAllGroup")
    @ResponseBody
    public JSONObject projectAllGroup(String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            List<Relation> groupList = relationService.findAllGroupInfoByProjectId(projectId);
            jsonObject.put("data",groupList);
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 查询出某个分组下的所有菜单信息
     * @param groupId
     * @return
     */
    @PostMapping("groupAllMenu")
    @ResponseBody
    public JSONObject groupAllMenu(String groupId){
        JSONObject jsonObject = new JSONObject();
        try {
            List<Relation> menuList = relationService.findAllMenuInfoByGroupId(groupId);
            jsonObject.put("data",menuList);
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 加载项目下所有分组信息
     * @param projectId 项目id
     * @param currentGroupId 当前分组的id
     * @return
     */
    @PostMapping("/loadGroupInfo")
    @ResponseBody
    public JSONObject loadGroupInfo(@RequestParam String projectId, @RequestParam String currentGroupId, Model model){
        JSONObject jsonObject = new JSONObject();
        try {
            List<GroupVO> groupos = relationService.loadGroupInfo(projectId);
            jsonObject.put("groups",groupos);
            jsonObject.put("groupId",currentGroupId);
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常,数据拉取失败!");
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常,数据拉取失败!");
        }
        return jsonObject;
    }

    /**
     * 用户选择分组完成后 切换任务列表以及任务信息
     * @param groupId 分组的名称
     * @return
     */
    @RequestMapping("changeGroup")
    public String changeGroup(@RequestParam String groupId, @RequestParam String projectId, Model model){
        try {
            Relation groupInfo = relationService.findRelationByRelationId(groupId);

            Relation relation1 = new Relation();
            relation1.setParentId(groupInfo.getRelationId());
            relation1.setLable(1);
            //获取当前分组下的所有菜单 和 菜单下的任务信息
            List<Relation> menu = relationService.findRelationAllList(relation1);
            model.addAttribute("taskMenus",menu);

            Project project = projectService.findProjectByProjectId(groupInfo.getProjectId());
            model.addAttribute("project",project);

            //加载该项目下所有分组的信息
            List<GroupVO> groups = relationService.loadGroupInfo(projectId);
            model.addAttribute("groups",groups);

            model.addAttribute("currentGroup",groupId);

            model.addAttribute("user",ShiroAuthenticationManager.getUserEntity());
        } catch (Exception e){
            log.error("系统异常,切换失败!");
            throw new SystemException(e);
        }
        return "mainpage";
    }
}
