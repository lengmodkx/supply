package com.art1001.supply.controller.tag;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.controller.base.BaseController;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.schedule.ScheduleLogFunction;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.tagrelation.TagRelation;
import com.art1001.supply.entity.task.PushType;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.tagrelation.TagRelationService;
import com.art1001.supply.service.task.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@Controller
@RequestMapping("/tag")
@Slf4j
public class TagController extends BaseController {

    @Resource
    private TagService tagService;
    @Resource
    private SimpMessagingTemplate messagingTemplate;

    @Resource
    private TagRelationService tagRelationService;

    @Resource
    private TaskService taskService;

    @Resource
    private FileService fileService;
    @Resource
    private ScheduleService scheduleService;
    @Resource
    private ShareService shareService;
    /**
     * 标签初始化
     * @param projectId 项目id
     * @param publicId 任务，文件，日程，分享id
     * @param publicType 任务，文件，日程，分享
     */
    @RequestMapping("/tag.html")
    public String tagPage(@RequestParam String projectId, @RequestParam(required = false) String publicId, @RequestParam(required = false)String publicType, Model model) {

        String tagId = "";
        //查询出项目的所有标签
        List<Tag> tagList = tagService.findByProjectId(projectId);
        //根据publicId 和 publicType查询出tag
        if(StringUtils.isNotEmpty(publicId)&&StringUtils.isNotEmpty(publicType)){
            List<Tag> tagListTemp = tagService.findByPublicId(publicId,publicType);
            for (Tag aTagList : tagList) {
                for (Tag aTagListTemp : tagListTemp) {
                    if (aTagList.getTagId().equals(aTagListTemp.getTagId())) {
                        aTagList.setFlag(true);
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
    @GetMapping("/tags/{projectId}")
    @ResponseBody
    public JSONObject tags(@PathVariable String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<Tag> tagList = tagService.findByProjectId(projectId);
            for (Tag tag:tagList) {
                List<TagRelation> tagRelationList = tagRelationService.findTagRelationByTagId(tag.getTagId());
                for (TagRelation tagRelation:tagRelationList) {
                    if(StringUtils.isNotEmpty(tagRelation.getTaskId())){
                        tag.getTaskList().add(taskService.findTaskByTaskId(tagRelation.getTaskId()));
                    }

                    if(StringUtils.isNotEmpty(tagRelation.getFileId())){
                        tag.getFileList().add(fileService.findFileById(tagRelation.getFileId()));
                    }

                    if(StringUtils.isNotEmpty(tagRelation.getScheduleId())){
                        tag.getScheduleList().add(scheduleService.findScheduleById(tagRelation.getScheduleId()));
                    }

                    if(StringUtils.isNotEmpty(tagRelation.getShareId())){
                        tag.getShareList().add(shareService.findById(tagRelation.getShareId()));
                    }
                }
            }
            jsonObject.put("result", 1);
            jsonObject.put("tagList", tagList);
            jsonObject.put("projectId", projectId);
        } catch (Exception e) {
            log.error("获取标签异常, {}", e);
            jsonObject.put("result", 0);
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
                jsonObject.put("msg", "标签已经存在");
                return jsonObject;
            }

            tag = tagService.saveTag(tag);
            jsonObject.put("result", 1);
            jsonObject.put("data", tag);
            jsonObject.put("msg", "添加成功");
            //包装推送数据
            PushType pushType = new PushType(TaskLogFunction.A10.getName());
            pushType.setObject(jsonObject);
            messagingTemplate.convertAndSend("/topic/tag/"+ tag.getProjectId(),new ServerMessage(JSON.toJSONString(pushType)));
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
     * @param projectId 项目id
     * @return
     */
    @PostMapping("removeTag")
    @ResponseBody
    public JSONObject removeTag(String publicId, String publicType, long tagId,String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            tagService.removeTag(publicId,publicType,tagId);
            //包装推送数据
            PushType taskPushType = new PushType(TaskLogFunction.A24.getName());
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("tag",String.valueOf(tagId));
            map.put("type",ScheduleLogFunction.M.getId());
            map.put("publicId",publicId);
            taskPushType.setObject(map);
            if(BindingConstants.BINDING_SHARE_NAME.equals(publicType)){
                messagingTemplate.convertAndSend("/topic/"+ projectId,new ServerMessage(JSON.toJSONString(taskPushType)));
                messagingTemplate.convertAndSend("/topic/"+ publicId);
            } else if(BindingConstants.BINDING_TASK_NAME.equals(publicType)){
                messagingTemplate.convertAndSend("/topic/"+publicId,new ServerMessage(JSON.toJSONString(taskPushType)));
                messagingTemplate.convertAndSend("/topic/"+projectId,new ServerMessage(JSON.toJSONString(taskPushType)));
            } else{
                messagingTemplate.convertAndSend("/topic/"+publicId,new ServerMessage(JSON.toJSONString(taskPushType)));
            }
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
     * @param projectId 项目的id
     * @return
     */
    @PostMapping("addItemTag")
    @ResponseBody
    public JSONObject addItemTag(Tag tag, String publicId, String publicType,String projectId){
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
            tagService.addItemTag(tag.getTagId(),publicId,publicType);
            //包装推送数据
            Tag byId = tagService.findById(tag.getTagId().intValue());

            PushType taskPushType = new PushType(TaskLogFunction.A20.getName());
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("tag",byId);
            jsonObject.put("tag",byId);
            map.put("type",ScheduleLogFunction.L.getId());
            map.put("publicId",publicId);
            taskPushType.setObject(map);

            if(BindingConstants.BINDING_SHARE_NAME.equals(publicType)){
                messagingTemplate.convertAndSend("/topic/"+ projectId,new ServerMessage(JSON.toJSONString(taskPushType)));
            } else if(BindingConstants.BINDING_TASK_NAME.equals(publicType)){
                messagingTemplate.convertAndSend("/topic/"+publicId,new ServerMessage(JSON.toJSONString(taskPushType)));
                messagingTemplate.convertAndSend("/topic/"+projectId,new ServerMessage(JSON.toJSONString(taskPushType)));
            } else{
                messagingTemplate.convertAndSend("/topic/"+publicId,new ServerMessage(JSON.toJSONString(taskPushType)));
            }
        } catch (Exception e){
            log.error("系统异常,{}",e);
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常!");
        }
        return jsonObject;
    }

    @PostMapping("updateTag")
    @ResponseBody
    public JSONObject updateTag(Tag tag){
        JSONObject jsonObject = new JSONObject();
        try{
            tagService.updateTag(tag);
            jsonObject.put("result",1);
            jsonObject.put("data",tag);
            //包装推送数据
            PushType pushType = new PushType(TaskLogFunction.A30.getName());
            pushType.setObject(jsonObject);
            messagingTemplate.convertAndSend("/topic/tag/"+ tag.getProjectId(),new ServerMessage(JSON.toJSONString(pushType)));
        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    @PostMapping("dropTag")
    @ResponseBody
    public JSONObject dropTag(Tag tag){
        JSONObject jsonObject = new JSONObject();
        try{
            tagService.updateTag(tag);
            jsonObject.put("result",1);
            //包装推送数据
            PushType pushType = new PushType(TaskLogFunction.A11.getName());
            messagingTemplate.convertAndSend("/topic/tag/"+ tag.getProjectId(),new ServerMessage(JSON.toJSONString(pushType)));
        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }


}
