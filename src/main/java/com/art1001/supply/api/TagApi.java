package com.art1001.supply.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Log;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.util.CommonUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
            jsonObject.put("result",1);
            jsonObject.put("data", tagList);
        } catch (Exception e){
            log.error("系统异常,标签初始化失败:",e);
            throw new SystemException(e);
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
        } catch (Exception e) {
            log.error("获取标签异常:", e);
            throw new SystemException(e);
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
            log.error("查询标签关联项异常:", e);
            throw new SystemException(e);
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
            if (!CommonUtils.listIsEmpty(tagList)) {
                jsonObject.put("result", 1);
                jsonObject.put("data", JSON.toJSON(tagList));
            } else {
                jsonObject.put("result", 0);
                jsonObject.put("data", null);
                jsonObject.put("msg", "无数据");
            }
        } catch (Exception e) {
            log.error("查询标签异常:", e);
            throw new SystemException(e);
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
            jsonObject.put("msg",e.getMessage());
        } catch (Exception e) {
            log.error("添加标签异常:", e);
            throw new AjaxException(e);
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
            log.error("删除标签异常:", e);
            throw new AjaxException(e);
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
    @Push(value = PushType.E2,type = 1)
    @DeleteMapping("/{tagId}/remove/tag")
    public JSONObject removeTag(@RequestParam(value = "publicId") String publicId, @RequestParam(value = "publicType") String publicType, @PathVariable long tagId){
        JSONObject jsonObject = new JSONObject();
        try {
            tagService.removeTag(publicId,publicType,tagId);
            jsonObject.put("data",new JSONObject().fluentPut("tagId",tagId).fluentPut("publicId",publicId).fluentPut("publicType",publicType));
            jsonObject.put("msgId",this.getProjectId(tagId));
            jsonObject.put("result",1);
            jsonObject.put("msg","移除成功");
        } catch (Exception e){
            log.error("系统异常,标签移除失败:",e);
            throw new AjaxException(e);
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
    @PostMapping("/add/tag")
    public JSONObject addItemTag(@RequestParam(value = "tagId",required = false) Long tagId,
                                 @RequestParam(value = "tagName",required = false) String tagName,
                                 @RequestParam(value = "bgColor",required = false) String bgColor,
                                 @RequestParam(value = "publicId") String publicId,
                                 @RequestParam(value = "publicType") String publicType,
                                 @RequestParam(value = "projectId",required = false) String projectId
    ){
        JSONObject jsonObject = new JSONObject();
        try {
            Tag tag = new Tag();
            tag.setTagId(tagId);
            tag.setTagName(tagName);
            tag.setProjectId(projectId);
            tag.setBgColor(bgColor);
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
            jsonObject.put("msg","添加成功!");
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常,添加标签失败:",e);
        }
        return jsonObject;
    }

    /**
     * 给某个信息绑定标签
     * @param tagId 标签id
     * @param publicId 信息id
     * @param publicType 信息类型
     * @return 是否成功
     */
    @Push(value = PushType.E1,type = 1)
    @PostMapping("/binding")
    public JSONObject bindingInfo(@RequestParam("tagId")Long tagId, @RequestParam("publicId") String publicId, @RequestParam("publicType")String publicType){
        JSONObject jsonObject = new JSONObject();
        try {
            if(tagService.addItemTag(tagId,publicId,publicType)) {
                Tag byId = tagService.getById(tagId);
                jsonObject.put("result", 1);
                jsonObject.put("data", new JSONObject().fluentPut("tag",byId).fluentPut("publicId",publicId).fluentPut("publicType",publicType));
                jsonObject.put("name", byId.getTagName());
                jsonObject.put("msgId", this.getProjectId(tagId));
                jsonObject.put("msg", "绑定成功!");
                jsonObject.put("id", publicId);
            } else{
                jsonObject.put("result",0);
                jsonObject.put("msg","绑定失败!");
            }

            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,标签绑定失败!",e);
        }
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
                                @RequestParam(value = "projectId") String projectId,
                                @RequestParam(value = "tagName",required = false) String tagName,
                                @RequestParam(value = "bgColor",required = false) String bgColor
                                ){
        JSONObject jsonObject = new JSONObject();
        try{
            Tag tag = new Tag();
            tag.setProjectId(projectId);
            tag.setTagId(tagId);
            tag.setTagName(tagName);
            tag.setBgColor(bgColor);
            tagService.updateTag(tag);
            jsonObject.put("result",1);
            jsonObject.put("data",tag);
        } catch (ServiceException e){
            jsonObject.put("msg",e.getMessage());
            jsonObject.put("result",0);
            return jsonObject;
        } catch (Exception e){
            log.error("系统异常,更新失败:",e);
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
            log.error("系统异常,移入回收站失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 用于获取当前标签的所属项目id
     * @param tagId 标签id
     * @return 项目id
     */
    private String getProjectId(Long tagId){
        return tagService.getOne(new QueryWrapper<Tag>().select("project_id").eq("tag_id",tagId)).getProjectId();
    }
}
