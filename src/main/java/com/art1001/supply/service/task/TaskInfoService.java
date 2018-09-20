package com.art1001.supply.service.task;

import com.art1001.supply.entity.statistics.StatisticsDTO;
import com.baomidou.mybatisplus.extension.service.IService;

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
     * @param statisticsDTO 前台传送过来的数据
     * @param chartId   用于判断是哪一个chart数据
     * @return Map
     */
    Map selectTask(StatisticsDTO statisticsDTO, String chartId);
}
