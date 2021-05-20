package com.art1001.supply.service.template.impl;

import com.art1001.supply.entity.template.TemplateTask;
import com.art1001.supply.mapper.template.TemplateTaskMapper;
import com.art1001.supply.service.template.TemplateTaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName TemplateTaskServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/5/20 10:29
 * @Discription 模板任务
 */
@Service
public class TemplateTaskServiceImpl extends ServiceImpl<TemplateTaskMapper, TemplateTask> implements TemplateTaskService {

    @Resource
    private TemplateTaskMapper templateTaskMapper;

    @Override
    public void createTemplateTask(String taskName, String relationId) {
        TemplateTask templateTask = new TemplateTask();
        templateTask.setTaskName(taskName);
        templateTask.setTaskMenuId(relationId);
        templateTask.setTaskGroupId(relationId);
        templateTask.setCreateTime(System.currentTimeMillis());
        templateTask.setUpdateTime(System.currentTimeMillis());
        templateTask.setMemberId(ShiroAuthenticationManager.getUserId());
        save(templateTask);
    }
}
