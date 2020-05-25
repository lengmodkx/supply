package com.art1001.supply.api;

import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.template.Template;
import com.art1001.supply.entity.template.TemplateRelation;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.template.TemplateDataService;
import com.art1001.supply.service.template.TemplateRelationService;
import com.art1001.supply.service.template.TemplateService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
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

    /**
     * 获取模板信息列表
     * @return
     */
    @GetMapping
    public Result<List<Template>> getTemplates(@RequestParam String orgId){
        try {
            List<Template> templates = templateService.list(new QueryWrapper<Template>().eq("org_Id", orgId));
            return Result.success(templates);
        }catch (Exception e){
            throw new AjaxException(e.getMessage());
        }
    }


    /**
     * 保存模板信息
     * @param templateName 模板名称
     * @param templateDes 模板描述
     * @param templateCover 模板封面
     * @return
     */
    @PostMapping
    public Result<Template> saveTemplate(@RequestParam String templateName,
                               @RequestParam String templateDes,
                               @RequestParam String templateCover,
                                         @RequestParam String orgId){
        try {
            Template template = new Template();
            template.setTemplateName(templateName);
            template.setTemplateDes(templateDes);
            template.setTemplateCover(templateCover);
            template.setOrgId(orgId);
            templateService.save(template);
            String[] relations = new String[]{"待认领","进行中","已完成"};
            for (int i = 0; i < relations.length; i++) {
                TemplateRelation templateRelation = new TemplateRelation();
                templateRelation.setTemplateId(template.getTemplateId());
                templateRelation.setRelationName(relations[i]);
                templateRelation.setRelationOrder(i);
                templateRelation.setCreateTime(System.currentTimeMillis());
                templateRelationService.save(templateRelation);
            }
        }catch (Exception e){
            throw new AjaxException(e.getMessage());
        }
        return Result.success();
    }

    /**
     * 更新模板信息
     * @param templateName 模板名称
     * @param templateDes 模板描述
     * @param templateCover 模板封面
     * @return
     */
    @PutMapping
    public Result<Template> upadteTemplate(@RequestParam String templateId,
                                           @RequestParam String templateName,
                                           @RequestParam String templateDes,
                                           @RequestParam String templateCover){
        try {
            Template template = new Template();
            template.setTemplateId(templateId);
            template.setTemplateName(templateName);
            template.setTemplateDes(templateDes);
            template.setTemplateCover(templateCover);
            templateService.updateById(template);
        }catch (Exception e){
            throw new AjaxException(e.getMessage());
        }
        return Result.success();
    }

    /**
     * 删除模板信息
     * @param templateId 模板id
     * @return
     */
    @DeleteMapping
    public Result deleteTemplate(@RequestParam String templateId){
        try {
            Template template = new Template();
            template.setTemplateId(templateId);
            templateService.removeById(template);
        }catch (Exception e){
            throw new AjaxException(e.getMessage());
        }
        return Result.success();
    }


    @GetMapping("/relations")
    public Result<List<TemplateRelation>> getRelations(@RequestParam String templateId){
        try {
            List<TemplateRelation> relations = templateRelationService.list(new QueryWrapper<TemplateRelation>().eq("template_id", templateId).orderByAsc("relation_order"));
            return Result.success(relations);
        }catch (Exception e){
            throw new AjaxException(e.getMessage());
        }
    }

    @DeleteMapping("/relations")
    public Result deleteRelation(@RequestParam String relationId){
        try {
            TemplateRelation relation = new TemplateRelation();
            relation.setRelationId(relationId);
            templateRelationService.removeById(relation);
            return Result.success();
        }catch (Exception e){
            throw new AjaxException(e.getMessage());
        }
    }


    @PutMapping("/relations")
    public Result upadteRelation(@RequestParam String relationId,@RequestParam String relationName,@RequestParam(defaultValue = "0") Integer relationOrder){
        try {
            TemplateRelation relation = new TemplateRelation();
            relation.setRelationId(relationId);
            relation.setRelationName(relationName);
            relation.setRelationOrder(relationOrder);
            relation.setUpdateTime(System.currentTimeMillis());
            templateRelationService.updateById(relation);
            return Result.success();
        }catch (Exception e){
            throw new AjaxException(e.getMessage());
        }
    }

    @PostMapping("/relations")
    public Result createRelation(@RequestParam String templateId,@RequestParam String relationName,@RequestParam(defaultValue = "0") Integer relationOrder){
        try {
            TemplateRelation relation = new TemplateRelation();
            relation.setTemplateId(templateId);
            relation.setRelationName(relationName);
            relation.setRelationOrder(relationOrder);
            relation.setUpdateTime(System.currentTimeMillis());
            templateRelationService.save(relation);
            return Result.success();
        }catch (Exception e){
            throw new AjaxException(e.getMessage());
        }
    }
}
