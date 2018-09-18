package com.art1001.supply.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.schedule.ScheduleLogFunction;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.tagrelation.TagRelation;
import com.art1001.supply.entity.task.PushType;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.service.tag.TagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author heshaohua
 * @Title: TagApi
 * @Description: TODO 标签api
 * @date 2018/9/12 13:42
 * [POST]   // 新增
 * [GET]    // 查询
 * [PATCH]  // 更新
 * [PUT]    // 覆盖，全部更新
 * [DELETE] // 删除
 **/
@Slf4j
@RequestMapping("tags")
@RestController
public class TagApi {

    /**
     * 注入标签逻辑层实例
     */
    @Resource
    private TagService tagService;

    /**
     * 标签初始化
     * @param projectId 项目id
     * @param publicId 任务，文件，日程，分享id
     * @param publicType 任务，文件，日程，分享
     */
    @GetMapping("/{projectId}")
    public JSONObject tagPage(@PathVariable(value = "projectId") String projectId,
                              @RequestParam(value = "publicId", required = false) String publicId,
                              @RequestParam(value="publicType", required = false)String publicType) {
        JSONObject jsonObject = new JSONObject();
        try {
            String tagId = "";
            //查询出项目的所有标签
            List<Tag> tagList = tagService.findByProjectId(projectId);
            //根据publicId 和 publicType查询出tag
            if(StringUtils.isNotEmpty(publicId)&&StringUtils.isNotEmpty(publicType)){
                List<Tag> tagListTemp = tagService.findByPublicId(publicId,publicType);
                tagList.forEach(item-> tagListTemp.forEach(item2 -> {
                    if (item.getTagId().equals(item2.getTagId())) {
                        item.setFlag(true);
                    }
                }));
            }
            jsonObject.put("tagList", tagList);
            jsonObject.put("projectId", projectId);
            jsonObject.put("publicId", publicId);
            jsonObject.put("publicType",publicType);
        } catch (Exception e){
            log.error("系统异常",e.getMessage());
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常!");
        }
        return jsonObject;
    }

    /**
     * 查询标签列表，根据项目
     */
    @GetMapping("/{projectId}/checkProjectTag")
    public JSONObject tags(@PathVariable String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<Tag> tagList = tagService.findTagByProjectIdWithAllInfo(projectId);
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

    /**
     * 查询多个标签
     * @param ids 标签数组
     * @return
     */
    @GetMapping("findByIds")
    public JSONObject findByIds(Integer[] ids) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<Tag> tagList = tagService.findByIds(ids);
            if (tagList.size() > 0) {
                jsonObject.put("result", 1);
                jsonObject.put("data", JSON.toJSON(tagList));
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
    @PostMapping
    public JSONObject addTag(@RequestParam(value = "projectId") String projectId, @RequestParam("tagName") String tagName, @RequestParam String bgColor) {
        JSONObject jsonObject = new JSONObject();
        try {
            Tag tag = new Tag();
            tag.setProjectId(projectId);
            tag.setBgColor(bgColor);
            tag.setTagName(tagName);
            tag = tagService.saveTag(tag);
            jsonObject.put("result", 1);
            jsonObject.put("data", tag);
            jsonObject.put("msg", "添加成功");
        } catch (ServiceException e){
            jsonObject.put("result",0);
            jsonObject.put("msg","该标签已存在!");
        } catch (Exception e) {
            log.error("添加标签异常", e.getMessage());
            jsonObject.put("result", 0);
            jsonObject.put("data", null);
            jsonObject.put("msg", "添加失败");
        }
        return jsonObject;
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{tagId}")
    public JSONObject deleteTag(@PathVariable Long tagId) {
        JSONObject jsonObject = new JSONObject();
        try {
            tagService.deleteTagByTagId(tagId);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("删除标签异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "删除失败");
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
    @DeleteMapping("/{tagId}/remove/tag")
    public JSONObject removeTag(@RequestParam(value = "publicId") String publicId, @RequestParam(value = "publicType") String publicType, @PathVariable long tagId){
        JSONObject jsonObject = new JSONObject();
        try {
            tagService.removeTag(publicId,publicType,tagId);
            jsonObject.put("result",1);
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
     * @param publicType 要从 (任务,文件,日程,分享) 哪个类型上 添加标签
     * @param tagName 标签名称 标签名称 标签所属项目
     * @param projectId 项目的id
     * @return
     */
    @PostMapping("addItemTag")
    public JSONObject addItemTag(@RequestParam(value = "tagId",required = false) Long tagId,
                                 @RequestParam(value = "tagName",required = false) String tagName,
                                 @RequestParam(value = "publicId") String publicId,
                                 @RequestParam(value = "publicType") String publicType,
                                 @RequestParam(value = "projectId") String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            Tag tag = new Tag();
            tag.setTagId(tagId);
            tag.setTagName(tagName);
            tag.setProjectId(projectId);
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
        } catch (Exception e){
            log.error("系统异常,{}",e);
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常!");
        }
        return jsonObject;
    }

    /**
     * 更新标签
     * @param tagId 标签id
     * @param tagName 标签名称
     * @param bgColor 标签颜色
     * @return
     */
    @PutMapping("/{tagId}")
    public JSONObject updateTag(@PathVariable Long tagId,
                                @RequestParam(value = "tagName",required = false) String tagName,
                                @RequestParam(value = "bgColor",required = false) String bgColor){
        JSONObject jsonObject = new JSONObject();
        try{
            Tag tag = new Tag();
            tag.setTagId(tagId);
            tag.setTagName(tagName);
            tag.setBgColor(bgColor);
            tagService.updateTag(tag);
            jsonObject.put("result",1);
            jsonObject.put("data",tag);
        }catch (Exception e){
            log.error("系统异常,{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 移入回收站
     * @param
     * @return
     */
    @PutMapping("/{tagId}/dropTag")
    public JSONObject dropTag(@PathVariable Long tagId){
        JSONObject jsonObject = new JSONObject();
        try{
            Tag tag = new Tag();
            tag.setTagId(tagId);
            tag.setIsDel(1);
            tagService.updateTag(tag);
            jsonObject.put("result",1);
        }catch (Exception e){
            log.error("系统异常,{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }
}
