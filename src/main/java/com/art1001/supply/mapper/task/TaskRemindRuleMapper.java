package com.art1001.supply.mapper.task;

import com.art1001.supply.entity.task.TaskRemindRule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author heshaohua
 * @since 2018-10-23
 */
public interface TaskRemindRuleMapper extends BaseMapper<TaskRemindRule> {

    /**
     * 查询出某个任务的所有规则 以及quartz定时信息
     * @param taskId 任务id
     */
    List<TaskRemindRule> listRuleAndQuartz(String taskId);
}
