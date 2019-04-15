package com.art1001.supply.mapper.statistics;

import com.art1001.supply.entity.statistics.StatisticsPie;
import com.art1001.supply.entity.task.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Auther: Administrator
 * @Date: 2019/4/13 19:07
 * @Description:
 */
public interface StatisticsMapper  extends BaseMapper<Task> {

    /**
     * 查询任务总数
     * @return integer
     * @param projectId
     */
    Integer getCountTask(String projectId) ;

    /**
     * 查询每个用户任务数
     * @return list
     * @param projectId
     */
    List<StatisticsPie> getPieDate(String projectId);
}
