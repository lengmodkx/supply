package com.art1001.supply.service.template;

import com.art1001.supply.entity.template.TemplateTask;
import com.baomidou.mybatisplus.extension.service.IService;

public interface TemplateTaskService extends IService<TemplateTask> {

    /**
     * 创建模板任务
     * @param taskName
     * @param relationId
     */
    void createTemplateTask(String taskName, String relationId);



}
