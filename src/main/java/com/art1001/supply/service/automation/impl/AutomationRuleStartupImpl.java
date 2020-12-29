package com.art1001.supply.service.automation.impl;

import com.art1001.supply.entity.task.Task;
import com.art1001.supply.mapper.task.TaskMapper;
import com.art1001.supply.service.automation.AutomationRuleStartup;
import com.art1001.supply.service.task.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @Description
 * @Date:2019/8/20 14:36
 * @Author heshaohua
 **/
@Configuration
public class AutomationRuleStartupImpl {

    @Resource
    private TaskMapper taskMapper;

    /**
     * 返回一个启动automationRule方式的实例
     * @return automationRuleStartup接口实例
     */
    @Bean
    public AutomationRuleStartup startupAutomationRule(){
        return (am,taskId) -> {
            String executor = am.getDefaultAssign();
            String menuId = am.getAutomaticJump();
            Task task = new Task();
            task.setTaskId(taskId);
            if(StringUtils.isNotEmpty(executor)){
                task.setExecutor(executor);
            }
            if(StringUtils.isNotEmpty(menuId)){
                task.setTaskMenuId(menuId);
            }
            task.setUpdateTime(System.currentTimeMillis());
            taskMapper.updateById(task);
        };
    }
}
