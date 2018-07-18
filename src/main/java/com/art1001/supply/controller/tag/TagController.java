package com.art1001.supply.controller.tag;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.service.tag.TagService;
import jdk.jfr.events.ExceptionThrownEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tag")
@Slf4j
public class TagController {

    @Resource
    private TagService tagService;

    @RequestMapping("/tag.html")
    public String tagPage(@RequestParam String projectId, Model model) {
        List<Tag> tagList = tagService.findByProjectId(projectId);
        model.addAttribute("tagList", tagList);
        return "tk-add-tag";
    }

    /**
     * 查询标签列表，根据项目
     */
    @GetMapping("/findByProjectId")
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
}
