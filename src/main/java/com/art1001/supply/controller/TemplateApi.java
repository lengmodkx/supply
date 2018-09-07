package com.art1001.supply.controller;

import com.art1001.supply.service.template.TemplateDataService;
import com.art1001.supply.service.template.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * [POST]   // 新增
 * [GET]    // 查询
 * [PATCH]  // 更新
 * [PUT]    // 覆盖，全部更新
 * [DELETE] // 删除
 */
@Slf4j
@RestController
@RequestMapping("template")
public class TemplateApi {
    @Resource
    private TemplateDataService templateDataService;

    @Resource
    private TemplateService templateService;
}
