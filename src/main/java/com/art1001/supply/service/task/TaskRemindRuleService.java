package com.art1001.supply.service.task;

import com.art1001.supply.entity.task.TaskRemindRule;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author heshaohua
 * @since 2018-10-23
 */
public interface TaskRemindRuleService extends IService<TaskRemindRule> {

    /**
     * 查询出某个任务的所有规则 以及quartz定时信息
     * @param taskId 任务id
     */
    List<TaskRemindRule> listRuleAndQuartz(String taskId);
}
