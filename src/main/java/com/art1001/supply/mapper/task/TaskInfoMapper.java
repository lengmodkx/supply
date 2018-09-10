package com.art1001.supply.mapper.task;

import com.art1001.supply.entity.statistics.StatisticsDTO;
import com.art1001.supply.entity.statistics.StatisticsResultVO;

import java.util.List;

/**
 * 模块
 *
 * @author lujing
 * @date 2018-09-08 14:02
 */
public interface TaskInfoMapper {

    /**
     * 查询总任务数
     * @return int
     */
    int selectTaskCount();

    /**
     *查询未完成
     * @param statisticsDTO 条件查询数据
     * @return  int
     */
    int selectUnfinishCount(StatisticsDTO statisticsDTO);

    List<StatisticsResultVO> selectUnfinishTask(StatisticsDTO statisticsDTO);

    List<StatisticsResultVO> selectFinishTask(StatisticsDTO statisticsDTO);

    List<StatisticsResultVO> selectExpireTask(StatisticsDTO statisticsDTO);

    List<StatisticsResultVO> selectOverdueTask(StatisticsDTO statisticsDTO);

    List<StatisticsResultVO> selectWaitClaimTask(StatisticsDTO statisticsDTO);

    List<StatisticsResultVO> selectPunctualityTask(StatisticsDTO statisticsDTO);

    List<StatisticsResultVO> selectExpiredToCompleteTask(StatisticsDTO statisticsDTO);
}
