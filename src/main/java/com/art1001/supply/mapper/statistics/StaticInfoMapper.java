package com.art1001.supply.mapper.statistics;

import com.art1001.supply.entity.statistics.StatisticsHistogram;
import com.art1001.supply.entity.statistics.StatisticsPie;
import com.art1001.supply.entity.statistics.StatisticsResultVO;
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
public interface StaticInfoMapper extends BaseMapper<Task> {

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

    /**
     * 查询柱状图数据
     * @return list
     * @param projectId 项目id
     */
    List<StatisticsHistogram> getHistogramsDate(@Param("projectId")String projectId, @Param("currentDate")Long currentDate);



    /**
     * 查询七天前的任务总量
     * @return int
     * @param projectId 项目id
     */
    @Select("SELECT COUNT(1) FROM prm_task pt WHERE  DATEDIFF(FROM_UNIXTIME(#{currDate},'%Y-%m-%d %T'),FROM_UNIXTIME(pt.create_time/1000,'%Y-%m-%d %T'))>=#{dayNum} AND project_id = #{projectId} ")
    Integer taskSevenDayAgo(@Param("projectId")String projectId, @Param("currDate")Long currentDate,  @Param("dayNum")int dayNum);

    /**
     * 查询项目进展走势
     * @param projectId 项目id
     * @param currentDate  当前日期
     * @return list
     */
    List<StatisticsResultVO> taskOfProgress(@Param("projectId")String projectId, @Param("currDate")Long currentDate);

    /**
     * 查询已完成项目进展走势
     * @param projectId 项目id
     * @param currentDate  当前日期
     * @return list
     */
    List<StatisticsResultVO> taskOfFinishProgress(@Param("projectId")String projectId, @Param("currDate")Long currentDate);

    /**
     * 查询七天前完成的任务数据
     *
     * @param projectId 项目id
     * @param currentDate  当前日期
     * @return list
     */
    @Select("SELECT COUNT(1) FROM prm_task pt WHERE  DATEDIFF(FROM_UNIXTIME(#{currDate},'%Y-%m-%d %T'),FROM_UNIXTIME(pt.create_time/1000,'%Y-%m-%d %T'))>=7 AND pt.task_status='完成' AND project_id = #{projectId}")
    int taskFinishOfSevenDayAgo(@Param("projectId")String projectId, @Param("currDate")Long currentDate);
}
