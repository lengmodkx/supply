package com.art1001.supply.api;

import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.template.*;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.template.*;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * [POST]   // 新增
 * [GET]    // 查询
 * [PATCH]  // 更新
 * [PUT]    // 覆盖，全部更新
 * [DELETE] // 删除
 */
@Slf4j
@RestController
@RequestMapping("templates")
public class TemplateApi {
    @Resource
    private TemplateRelationService templateRelationService;

    @Resource
    private TemplateService templateService;

    @Resource
    private TemplateTaskService templateTaskService;

    @Resource
    private TemplateTagService templateTagService;

    @Resource
    private TemplateTagRealtionService templateTagRealtionService;

    /**
     * 获取模板信息列表
     *
     * @return
     */
    @GetMapping
    public Result<List<Template>> getTemplates(@RequestParam String orgId) {
        try {
            List<Template> templates = templateService.list(new QueryWrapper<Template>().eq("org_Id", orgId));
            return Result.success(templates);
        } catch (Exception e) {
            throw new AjaxException(e.getMessage());
        }
    }

    /**
     * 获取模板信息列表
     *
     * @return
     */
    @GetMapping("{templateId}")
    public Result<Template> getTemplate(@PathVariable String templateId) {
        try {
            Template templates = templateService.getById(templateId);
            return Result.success(templates);
        } catch (Exception e) {
            throw new AjaxException(e.getMessage());
        }
    }

    /**
     * 保存模板信息
     *
     * @param templateName  模板名称
     * @param templateDes   模板描述
     * @param templateCover 模板封面
     * @return
     */
    @PostMapping
    public Result<Template> saveTemplate(@RequestParam(value = "templateName", required = false) String templateName,
                                         @RequestParam(value = "templateDes", required = false) String templateDes,
                                         @RequestParam(value = "templateCover", required = false) String templateCover,
                                         @RequestParam(value = "orgId") String orgId,
                                         @RequestParam(value = "templateId", required = false) String templateId) {
        try {
            if (StringUtils.isNotEmpty(templateId)) {
                //处理模板
                Template template = new Template();
                template.setTemplateName(templateName);
                template.setTemplateDes(templateDes);
                template.setTemplateCover(templateCover);
                template.setOrgId(orgId);
                template.setMemberId(ShiroAuthenticationManager.getUserId());
                templateService.saveTemplate(template);
            } else {
                Template template = new Template();
                template.setTemplateId(templateId);
                template.setTemplateName(templateName);
                template.setTemplateDes(templateDes);
                template.setTemplateCover(templateCover);
                templateService.updateById(template);
            }

        } catch (Exception e) {
            throw new AjaxException(e.getMessage());
        }
        return Result.success();
    }

    /**
     * 更新模板信息
     *
     * @param templateName  模板名称
     * @param templateDes   模板描述
     * @param templateCover 模板封面
     * @return
     */
//    @PutMapping
//    public Result<Template> upadteTemplate(@RequestParam String templateId,
//                                           @RequestParam String templateName,
//                                           @RequestParam String templateDes,
//                                           @RequestParam String templateCover) {
//        try {
//            Template template = new Template();
//            template.setTemplateId(templateId);
//            template.setTemplateName(templateName);
//            template.setTemplateDes(templateDes);
//            template.setTemplateCover(templateCover);
//            templateService.updateById(template);
//        } catch (Exception e) {
//            throw new AjaxException(e.getMessage());
//        }
//        return Result.success();
//    }

    /**
     * 删除模板信息
     *
     * @param templateId 模板id
     * @return
     */
    @DeleteMapping
    public Result deleteTemplate(@RequestParam String templateId) {
        try {
            Template template = new Template();
            template.setTemplateId(templateId);
            templateService.removeById(template);
        } catch (Exception e) {
            throw new AjaxException(e.getMessage());
        }
        return Result.success();
    }


    @GetMapping("/relations")
    public Result<List<TemplateRelation>> getRelations(@RequestParam String templateId) {
        try {
            List<TemplateRelation> relations = templateRelationService.getRelation(templateId);
            return Result.success(relations);
        } catch (Exception e) {
            throw new AjaxException(e.getMessage());
        }
    }


    @DeleteMapping("/relations")
    public Result deleteRelation(@RequestParam String relationId) {
        try {
            TemplateRelation relation = new TemplateRelation();
            relation.setRelationId(relationId);
            templateRelationService.removeById(relation);
            return Result.success();
        } catch (Exception e) {
            throw new AjaxException(e.getMessage());
        }
    }


    @PutMapping("/relations")
    public Result upadteRelation(@RequestParam String relationId, @RequestParam String relationName, @RequestParam(defaultValue = "0") Integer relationOrder) {
        try {
            TemplateRelation relation = new TemplateRelation();
            relation.setRelationId(relationId);
            relation.setRelationName(relationName);
            relation.setOrder(relationOrder);
            relation.setUpdateTime(System.currentTimeMillis());
            templateRelationService.updateById(relation);
            return Result.success();
        } catch (Exception e) {
            throw new AjaxException(e.getMessage());
        }
    }

    @PostMapping("/relations")
    public Result createRelation(@RequestParam String templateId, @RequestParam String relationName, @RequestParam(defaultValue = "0") Integer relationOrder) {
        try {
            TemplateRelation relation = new TemplateRelation();
            relation.setTemplateId(templateId);
            relation.setRelationName(relationName);
            relation.setOrder(relationOrder);
            relation.setUpdateTime(System.currentTimeMillis());
            templateRelationService.save(relation);
            return Result.success();
        } catch (Exception e) {
            throw new AjaxException(e.getMessage());
        }
    }

    /**
     * 更新模板中的任务
     *
     * @param taskId
     * @param taskName
     * @param remarks
     * @return
     */
    @PutMapping("{taskId}/update")
    public Result<String> updateTemplateTask(@PathVariable String taskId,
                                             @RequestParam(required = false) String taskName,
                                             @RequestParam(required = false) String relationId,
                                             @RequestParam(required = false) String remarks,
                                             @RequestParam(required = false)Long tagId) {

        try {
            TemplateTask task = new TemplateTask();
            task.setTaskId(taskId);
            task.setTaskName(taskName);
            task.setTaskMenuId(relationId);
            task.setRemarks(remarks);
            templateTaskService.updateById(task);
            TemplateTagRelation templateTagRelation = new TemplateTagRelation();
            templateTagRelation.setTagId(tagId);
            templateTagRelation.setTagId(tagId);
            templateTagRealtionService.save(templateTagRelation);
        } catch (Exception e) {
            throw new AjaxException(e.getMessage());
        }
        return Result.success();
    }

    /**
     * 根据模板创建项目
     *
     * @param templateId 模板id
     * @return
     */
    @PostMapping("/project/{templateId}")
    public Result<String> addProjectByTemplate(@PathVariable String templateId, @RequestParam String projectName) {
        try {
            templateService.addProject(templateId,projectName);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return Result.success("创建成功");
    }

    /**
     * 创建模板任务
     *
     * @param taskName
     * @param relationId
     * @return
     */
    @GetMapping("/createTemplateTask")
    public Result createTemplateTask(@RequestParam(value = "taskName") String taskName,
                                     @RequestParam(value = "relationId") String relationId) {
        try {
            templateTaskService.createTemplateTask(taskName, relationId);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return Result.success();

    }

    /**
     * 模板详情
     *
     * @return
     */
    @GetMapping("/templateInfo")
    public Result<Template> templateInfo(@RequestParam(value = "templateId") String templateId) {
        Template template = templateService.getById(templateId);

        return Result.success(template);
    }

    /**
     * 创建模板标签
     * @param templateTagName
     * @param templateId
     * @param bgColor
     * @return
     */
    @GetMapping("/createTemplateTag")
    public Result createTemplateTag(@RequestParam(value = "templateTagName") String templateTagName,
                                    @RequestParam(value = "templateId") String templateId,
                                    @RequestParam(value = "bgColor")String bgColor) {
        try {
            TemplateTag templateTag = new TemplateTag();
            templateTag.setTemplateId(templateId);
            templateTag.setBgColor(bgColor);
            templateTag.setTagName(templateTagName);
            templateTag.setCreateTime(System.currentTimeMillis());
            templateTag.setUpdateTime(System.currentTimeMillis());
            templateTagService.save(templateTag);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return Result.success("保存成功");
    }

    /**
     * 更新模板标签
     * @param templateTagName
     * @param bgColor
     * @param tagId
     * @return
     */
    @GetMapping("/updateTemplateTag")
    public Result updateTemplateTag(@RequestParam(value = "templateTagName",required = false) String templateTagName,
                                    @RequestParam(value = "bgColor",required = false)String bgColor,
                                    @RequestParam(value ="tagId" ) Long tagId){
        try {
            templateTagService.update(new UpdateWrapper<TemplateTag>().set("tag_name",templateTagName).set("bg_color",bgColor).eq("tag_id",tagId));
        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return Result.success("修改成功");
    }

    /**
     * 模板标签列表
     * @param templateId
     * @return
     */
    @GetMapping("/getTemplateTagList")
    public Result getTemplateTagList(@RequestParam(value = "templateId") String templateId){
        try {
            List<TemplateTag> List = templateTagService.list(new QueryWrapper<TemplateTag>().eq("template_id", templateId));
            return Result.success(List);
        } catch (Exception e) {
            throw new AjaxException(e);
        }

    }

    /**
     * 移除模板标签
     * @param tagId
     * @return
     */
    @GetMapping("/removeTemplateTag")
    public Result removeTemplateTag(@RequestParam(value = "tagId") String tagId){
        try {
            templateTagService.removeById(tagId);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return Result.success("移除成功");
    }

    /**
     * 移除模板列表任务
     * @param relationId
     * @return
     */
    @GetMapping("/deleteRealtionAllTask")
    public Result deleteRealtionAllTask(@RequestParam(value = "relationId") String relationId) {
        try {
            templateTaskService.remove(new QueryWrapper<TemplateTask>().eq("task_menu_id",relationId));
        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return Result.success("列表所有任务已经移除");
    }

    /**
     *  模板任务初始化
     * @param taskId
     * @return
     */
    @GetMapping("/getTemplateTaskList")
    public Result getTemplateTaskList(@RequestParam("taskId") String taskId){
        try {
            TemplateTask task=templateService.getTemplateTaskList(taskId);
            return Result.success(task);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 添加子任务
     * @param parentTaskId
     * @param taskName
     * @param relationId
     * @return
     */
    @GetMapping("/insertChildTask")
    public Result insertChildTask(@RequestParam("parentTaskId") String parentTaskId,
                                  @RequestParam("taskName")String taskName,
                                  @RequestParam(value = "relationId") String relationId){
        try {
            templateService.insertChildTask(parentTaskId,taskName,relationId);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return Result.success("保存子任务成功");
    }


}
