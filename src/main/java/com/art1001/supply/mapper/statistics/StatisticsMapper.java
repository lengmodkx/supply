package com.art1001.supply.mapper.statistics;

import com.art1001.supply.entity.statistics.StatisticsPie;
import com.art1001.supply.entity.task.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Auther: yanglujing
 * @Date: 2019/4/13 19:07
 * @Description:
 */

@Mapper
public interface StatisticsMapper  extends BaseMapper<Task> {

    /**
     * 查询任务总数
     * @return integer
     * @param projectId
     */
    @Select("SELECT COUNT(1) FROM prm_task pt  WHERE  pt.project_id=#{projectId}")
    Integer getCountTask(@Param("projectId") String projectId) ;



    /**
     * 查询每个用户任务数
     * @return list
     * @param projectId
     */
     List<StatisticsPie> getPieDate(String projectId);
}
