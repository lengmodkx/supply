package com.art1001.supply.mapper.task;

import com.art1001.supply.entity.statistics.StatisticsDTO;
import com.art1001.supply.entity.statistics.StatisticsResultVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.elasticsearch.tasks.TaskInfo;

import java.util.List;

/**
 * 模块
 *
 * @author lujing
 * @date 2018-09-08 14:02
 */
public interface TaskInfoMapper extends BaseMapper<TaskInfo> {

    /**
     * 查询总任务数
     * @return int
     */
    int selectTaskCount();

    /**
     *查询未完成
     * @param statisticsDTO 条件查询数据
     * @param unfinishTaskCase 查询任务状态为未完成
     * @return  int
     */
    int selectUnfinishCount(@Param("stDTO") StatisticsDTO statisticsDTO, @Param("unfinishTaskCase") String unfinishTaskCase);

    int selectFinishCount(@Param("stDTO") StatisticsDTO statisticsDTO,  @Param("finishTaskCase") String finishTaskCase);

    List<StatisticsResultVO> selectExpireTask(@Param("stDTO") StatisticsDTO statisticsDTO);

    List<StatisticsResultVO> selectOverdueTask(@Param("stDTO") StatisticsDTO statisticsDTO);

    List<StatisticsResultVO> selectWaitClaimTask(@Param("stDTO") StatisticsDTO statisticsDTO);

    List<StatisticsResultVO> selectPunctualityTask(@Param("stDTO") StatisticsDTO statisticsDTO);

    List<StatisticsResultVO> selectExpiredToCompleteTask(@Param("stDTO") StatisticsDTO statisticsDTO);

    List<StatisticsResultVO> selectUnfinishTask(@Param("stDTO") StatisticsDTO statisticsDTO, @Param("unfinishTaskCase") String unfinishTaskCase);

    List<StatisticsResultVO> selectFinishTask(@Param("stDTO") StatisticsDTO statisticsDTO,  @Param("finishTaskCase") String finishTaskCase);

    List<StatisticsResultVO> selectGroupByTask(@Param("stDTO") StatisticsDTO statisticsDTO);
}
