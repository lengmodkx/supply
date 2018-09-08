package com.art1001.supply.service.task;

import com.art1001.supply.entity.statistics.TaskCondition;
import java.util.Map;

/**
 * 模块
 *
 * @author lujing
 * @date 2018-09-05 15:33
 */
public interface TaskInfoService {
    /**
     * 查询总量概览的详细数据
     * @param taskCondition 前台传送过来的数据
     * @param chartId   用于判断是哪一个chart数据
     * @return Map
     */
    Map selectTask(TaskCondition taskCondition, String chartId);
}
