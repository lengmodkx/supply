package com.art1001.supply.service.task.impl;

import com.art1001.supply.entity.task.TaskRemindRule;
import com.art1001.supply.mapper.task.TaskRemindRuleMapper;
import com.art1001.supply.service.task.TaskRemindRuleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author heshaohua
 * @since 2018-10-23
 */
@Service
public class TaskRemindRuleServiceImpl extends ServiceImpl<TaskRemindRuleMapper, TaskRemindRule> implements TaskRemindRuleService {

    @Resource
    private TaskRemindRuleMapper taskRemindRuleMapper;

    /**
     * 查询出某个任务的所有规则 以及quartz定时信息
     * @param taskId 任务id
     */
    @Override
    public List<TaskRemindRule> listRuleAndQuartz(String taskId) {
        return taskRemindRuleMapper.listRuleAndQuartz(taskId);
    }
}
