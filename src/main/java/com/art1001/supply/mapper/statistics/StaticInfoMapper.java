package com.art1001.supply.mapper.statistics;

import com.art1001.supply.entity.statistics.*;
import com.art1001.supply.entity.task.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

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
    Integer getCountTask(@Param("projectId") String projectId) ;



    /**
     * 查询每个用户任务数
     * @return list
     * @param projectId
     * @param sto
     */
     List<StatisticsPie> getPieDate(@Param("projectId") String projectId, @Param("stDTO")StaticDto sto);

    /**
     * 查询柱状图数据
     * @return list
     * @param projectId 项目id
     * @param sto
     */
    List<StatisticsHistogram> getHistogramsDate(@Param("projectId") String projectId, @Param("currentDate") Long currentDate, @Param("stDTO")StaticDto sto);



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
     * @param sto
     * @param dayNumMap
     * @return list
     */
    List<StatisticsResultVO> taskOfProgress(@Param("projectId") String projectId, @Param("currDate") Long currentDate, @Param("stDTO") StaticDto sto, @Param("dayNum")Map<String, Integer> dayNumMap);

    /**
     * 查询已完成项目进展走势
     * @param projectId 项目id
     * @param currentDate  当前日期
     * @param sto
     * @param dayNumMap
     * @return list
     */
    List<StatisticsResultVO> taskOfFinishProgress(@Param("projectId") String projectId, @Param("currDate") Long currentDate, @Param("stDTO") StaticDto sto, @Param("dayNum")Map<String, Integer> dayNumMap);

    /**
     * 查询七天前完成的任务数据
     *
     * @param projectId 项目id
     * @param currentDate  当前日期
     * @param sto
     * @return list
     */
    @Select("SELECT COUNT(1) FROM prm_task pt WHERE  DATEDIFF(FROM_UNIXTIME(#{currDate},'%Y-%m-%d %T'),FROM_UNIXTIME(pt.create_time/1000,'%Y-%m-%d %T'))>=7 AND pt.task_status=1 AND project_id = #{projectId}")
    int taskFinishOfSevenDayAgo(@Param("projectId") String projectId, @Param("currDate") Long currentDate, @Param("stDTO")StaticDto sto);

    /**
     * 查询任务燃尽图表数据
     * @param projectId 项目id
     * @param sto
     * @return list
     */
    List<StatisticsResultVO> selectTaskBurnOut(@Param("projectId")String projectId, @Param("stDTO")StaticDto sto);

    /**
     * 查询项目进展图表数据
     * @param projectId 项目id
     * @param sto
     * @return list
     */
    List<StatisticsResultVO> selectProjectProgress(@Param("projectId")String projectId, @Param("stDTO")StaticDto sto);

    /**
     * 查询执行者分组数据
     * @param projectId 项目id
     * @return obj
     */
    List<QueryVO> getExecutorGroup(String projectId);

    /**
     * 查询任务分组数据
     * @param projectId 项目id
     * @return obj
     */
    List<QueryVO> getTaskGroup(String projectId);

    /**
     *  获取饼图数据
     * @param projectId 项目id
     * @return obj
     */
    List<StatisticsPie> selectExcutorTask(@Param("projectId")String projectId,  @Param("stDTO")StaticDto resultStatic);


    /**
     * 查询某个项目下未完成的任务数量
     * @param projectId 项目id
     * @return
     */
    @Select("select count(0) from prm_task where project_id = #{projectId} and task_status = 0")
    int findHangInTheAirTaskCount(String projectId);

    /**
     * 查询某个项目下完成的任务数量
     * @param projectId 项目id
     * @return
     */
        @Select("select count(0) from prm_task where project_id = #{projectId} and task_status = 1")
    int findCompletedTaskCount(String projectId);

    /**
     * 查询某个项目下 今日到期的任务
     * @param projectId 项目id
     * @param currDate  当前日期 格式为 yyyy-MM-dd
     * @return
     */
    @Select("select count(0) from prm_task where project_id = #{projectId} and FROM_UNIXTIME(end_time/1000, '%Y-%m-%d') = FROM_UNIXTIME(#{currDate}, '%Y-%m-%d'	)")
    int currDayTaskCount(@Param("projectId") String projectId, @Param("currDate") Long currDate);

    /**
     * 查询某个项目下 已逾期的任务
     * @param projectId 项目id
     * @param currDate  当前日期 格式为 yyyy-MM-dd
     * @return
     */
    @Select("select count(0) from prm_task where project_id = #{projectId} and FROM_UNIXTIME(end_time/1000, '%Y-%m-%d %T') < FROM_UNIXTIME(#{currDate}, '%Y-%m-%d %T') and task_status = 0 ")
    int findBeoberdueTaskCount(@Param("projectId") String projectId, @Param("currDate") Long currDate);

    /**
     * 查询出该项目下 的所有待认领的任务
     * @param projectId 项目id
     * @return
     */
    @Select("select count(0) from prm_task where project_id = #{projectId} and (executor = '' or executor is null)")
    int findTobeclaimedTaskCount(String projectId);

    /**
     * 查询出该项目下 按时完成的所有任务
     * @param projectId 项目id
     * @param currDate  当前日期 格式为 yyyy-MM-dd
     * @return
     */
    @Select("select count(0) from prm_task where project_id = #{projectId} and FROM_UNIXTIME(end_time/1000, '%Y-%m-%d') >= FROM_UNIXTIME(#{currDate}, '%Y-%m-%d') and task_status = 1")
    int findFinishontTimeTaskCount(@Param("projectId") String projectId, @Param("currDate") Long currDate);

    /**
     * 查询出该项目下 按时完成的所有任务
     * @param projectId 项目id
     * @param currDate  当前日期 格式为 yyyy-MM-dd
     * @return
     */
    @Select("select count(0) from prm_task where project_id = #{projectId} and FROM_UNIXTIME(end_time/1000, '%Y-%m-%d') < FROM_UNIXTIME(#{currDate}, '%Y-%m-%d') and task_status = 1")
    int findOverdueCompletion(@Param("projectId") String projectId, @Param("currDate") Long currDate);


    /**
     * 未完成数据
     */
    List<StatisticsResultVO> selectUnfinishTask(@Param("stDTO") StatisticsDTO statisticsDTO, @Param("unfinishTaskCase") String unfinishTaskCase, @Param("projectId") String projectId);

    /**
     * 已完成数据
     */
    List<StatisticsResultVO> selectFinishTask(@Param("stDTO") StatisticsDTO statisticsDTO, @Param("finishTaskCase") String finishTaskCase, @Param("projectId") String projectId);

    /**
     * 今日到期
     */
    List<StatisticsResultVO> selectExpireTask(@Param("stDTO") StatisticsDTO statisticsDTO, @Param("projectId") String projectId);

    /**
     * 已逾期
     */
    List<StatisticsResultVO> selectOverdueTask(@Param("stDTO") StatisticsDTO statisticsDTO, @Param("projectId") String projectId);

    /**
     * 待认领
     */
    List<StatisticsResultVO> selectWaitClaimTask(@Param("stDTO") StatisticsDTO statisticsDTO, @Param("projectId") String projectId);

    /**
     * 按时完成数据
     */
    List<StatisticsResultVO> selectPunctualityTask(@Param("stDTO") StatisticsDTO statisticsDTO, @Param("projectId") String projectId);

    /**
     * 逾期完成数据
     */
    List<StatisticsResultVO> selectExpiredToCompleteTask(@Param("stDTO") StatisticsDTO statisticsDTO, @Param("projectId") String projectId);
}
