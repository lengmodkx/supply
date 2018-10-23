package com.art1001.supply.quartz.mapper;

import com.art1001.supply.quartz.ScheduleTrigger;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author heshaohua
 * @Description:
 * @date 2018/10/22 18:07
 */
@Mapper
public interface ScheduleTriggerMapper {

    List<ScheduleTrigger> findAll();
}
