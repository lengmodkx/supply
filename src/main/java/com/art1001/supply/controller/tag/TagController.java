package com.art1001.supply.controller.tag;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.controller.base.BaseController;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.binding.Binding;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.schedule.ScheduleLogFunction;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskPushType;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.task.TaskService;
import jdk.jfr.events.ExceptionThrownEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tag")
@Slf4j
public class TagController extends BaseController {

    @Resource
    private TagService tagService;

    @Resource
    private TaskService taskService;

    @Resource
    private ScheduleService scheduleService;

    @Resource
    private FileService fileService;

    @Resource
    private ShareService shareService;

    @Resource
    private SimpMessagingTemplate messagingTemplate;


    @RequestMapping("/tag.html")
    public String tagPage(@RequestParam String projectId,
                          @RequestParam(required = false) String publicId, String publicType, Model model) {

        String tagId = "";
        //查询出项目的所有房间
        List<Tag> tagList = tagService.findByProjectId(projectId);

        //判断出要查询的 是 (任务,文件,日程,分享) 哪个的标签
        if(BindingConstants.BINDING_TASK_NAME.equals(publicType)){
            Task task = taskService.findTaskByTaskId(publicId);
            tagId = task.getTagId();
        }
        if(BindingConstants.BINDING_SCHEDULE_NAME.equals(publicType)){
            Schedule schedule = scheduleService.findScheduleById(publicId);
            tagId = schedule.getTagId();
        }
        if(BindingConstants.BINDING_FILE_NAME.equals(publicType)){
            File file = fileService.findFileById(publicId);
            tagId = file.getTagId();
        }
        if(BindingConstants.BINDING_SHARE_NAME.equals(publicType)){
            Share share = shareService.findById(publicId);
            tagId = share.getTagIds();
        }

        if(StringUtils.isNotEmpty(tagId)){
            List<String> tagIds = Arrays.asList(tagId.split(","));
            for(int i=0;i<tagList.size();i++) {
                for (int j=0;j<tagIds.size();j++) {
                    if(tagList.get(i).getTagId().equals(Long.valueOf(tagIds.get(j)))){
                        tagList.get(i).setFlag(true);
                    }
                }
            }
        }

        model.addAttribute("tagList", tagList);
        model.addAttribute("projectId", projectId);
        model.addAttribute("publicId", publicId);
        model.addAttribute("publicType",publicType);
        return "tk-add-tag";
    }


    /**
     * 查询标签列表，根据项目
     */
    @PostMapping("/findByProjectId")
    @ResponseBody
    public JSONObject findByProjectId(
            @RequestParam String projectId
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<Tag> tagList =  tagService.findByProjectId(projectId);
            if (tagList.size() > 0) {
                jsonObject.put("result", 1);
                jsonObject.put("data", JSON.toJSON(tagList));
                jsonObject.put("msg", "获取成功");
            } else {
                jsonObject.put("result", 0);
                jsonObject.put("data", null);
                jsonObject.put("msg", "没有数据");
            }

        } catch (Exception e) {
            log.error("获取标签异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("data", null);
            jsonObject.put("msg", "没有数据");
        }
        return jsonObject;
    }

    /**
     * 查询标签关联项
     */
    @GetMapping("/findByTag")
    @ResponseBody
    public JSONObject findByTag(Tag tag) {
        JSONObject jsonObject = new JSONObject();
        try {
            Map<String, Object> map = tagService.findByTag(tag);
            jsonObject.put("result", 1);
            jsonObject.put("data", JSON.toJSON(map));
            jsonObject.put("msg", "获取成功");
        } catch (Exception e) {
            log.error("查询标签关联项异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "没有数据");
        }
        return jsonObject;
    }

    @GetMapping("findByIds")
    @ResponseBody
    public JSONObject findByIds(Integer[] ids) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<Tag> tagList = tagService.findByIds(ids);
            if (tagList.size() > 0) {
                jsonObject.put("result", 1);
                jsonObject.put("data", JSON.toJSON(tagList));
                jsonObject.put("msg", "获取成功");
            } else {
                jsonObject.put("result", 0);
                jsonObject.put("data", null);
                jsonObject.put("msg", "无数据");
            }
        } catch (Exception e) {
            log.error("查询标签异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("data", null);
            jsonObject.put("msg", "无数据");
        }

        return jsonObject;
    }

    /**
     * 添加标签
     */
    @PostMapping("/add")
    @ResponseBody
    public JSONObject addTag(Tag tag) {
        JSONObject jsonObject = new JSONObject();
        try {
            // 如果标签已经存在，则直接使用
            int count = tagService.findCountByTagName(tag.getProjectId(), tag.getTagName());
            if (count > 0) {
                jsonObject.put("result", 0);
                jsonObject.put("data", null);
                jsonObject.put("msg", "标签已经存在");
                return jsonObject;
            }
            Tag tagId = tagService.saveTag(tag);
            jsonObject.put("result", 1);
            jsonObject.put("data", tagId);
            jsonObject.put("tag", tag);
            jsonObject.put("msg", "添加成功");
        } catch (Exception e) {
            log.error("添加标签异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("data", null);
            jsonObject.put("msg", "添加失败");
        }
        return jsonObject;
    }

    /**
     * 删除标签
     */
    @PostMapping("/delete")
    @ResponseBody
    public JSONObject deleteTag(@RequestParam Long tagId) {
        JSONObject jsonObject = new JSONObject();
        try {
            tagService.deleteTagByTagId(tagId);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "删除成功");
        } catch (Exception e) {
            log.error("删除标签异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "删除失败");
        }
        return jsonObject;
    }

    /**
     * 模糊查询标签
     * @return
     */
    @PostMapping("searchTag")
    @ResponseBody
    public JSONObject searchTag(String tagName){
        JSONObject jsonObject = new JSONObject();
        try {
            List<Tag> listTag = tagService.searchTag(tagName);
            jsonObject.put("data",listTag);
            jsonObject.put("result",1);
        } catch (Exception e){
            e.printStackTrace();
            log.error("系统异常,数据拉取失败!");
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常,数据拉取失败!");
        }
        return jsonObject;
    }

    /**
     * 移除掉 任务 文件 日程 分享 上的标签
     * @param publicId (任务,文件,日程,分享) 的id
     * @param publicType 要从 (任务,文件,日程,分享) 哪个类型上 移除掉标签
     * @param tagId 标签id
     * @return
     */
    @PostMapping("removeTag")
    @ResponseBody
    public JSONObject removeTag(String publicId, String publicType, String tagId){
        JSONObject jsonObject = new JSONObject();
        try {
            tagService.removeTag(publicId,publicType,tagId);
            //包装推送数据
            TaskPushType taskPushType = new TaskPushType();
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("tag",tagId);
            map.put("type",ScheduleLogFunction.M.getId());
            taskPushType.setObject(map);
            //推送至日程的详情界面
            messagingTemplate.convertAndSend("/topic/"+publicId,new ServerMessage(JSON.toJSONString(taskPushType)));
        } catch (Exception e){
            e.printStackTrace();
            log.error("系统异常,{}",e);
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常!");
        }
        return jsonObject;
    }

    /**
     * 添加 任务 文件 日程 分享 的标签
     * @param publicId (任务,文件,日程,分享) 的id
     * @param publicType 要从 (任务,文件,日程,分享) 哪个类型上 移除掉标签
     * @param tag 标签id 标签名称 标签所属项目
     * @return
     */
    @PostMapping("addItemTag")
    @ResponseBody
    public JSONObject addItemTag(Tag tag, String publicId, String publicType){
        JSONObject jsonObject = new JSONObject();
        try {
            if(StringUtils.isNotEmpty(tag.getTagName())){
                int countByTagName = tagService.findCountByTagName(tag.getProjectId(), tag.getTagName());
                if(countByTagName == 0){
                    tag = tagService.saveTag(tag);
                } else{
                    jsonObject.put("msg","标签已存在!");
                    jsonObject.put("result",0);
                    return jsonObject;
                }
            }
            tagService.addItemTag(String.valueOf(tag.getTagId()),publicId,publicType);
            //包装推送数据
            Tag byId = tagService.findById(tag.getTagId().intValue());
            TaskPushType taskPushType = new TaskPushType();
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("tag",byId);
            jsonObject.put("tag",byId);
            map.put("type",ScheduleLogFunction.L.getId());
            taskPushType.setObject(map);
            //推送至日程的详情界面
            messagingTemplate.convertAndSend("/topic/"+publicId,new ServerMessage(JSON.toJSONString(taskPushType)));
        } catch (Exception e){
            log.error("系统异常,{}",e);
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常!");
        }
        return jsonObject;
    }
}
