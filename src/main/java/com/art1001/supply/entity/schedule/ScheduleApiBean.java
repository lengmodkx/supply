package com.art1001.supply.entity.schedule;

import lombok.Data;

/**
 * @author heshaohua
 * @Title: ScheduleApiBean
 * @Description: TODO
 * @date 2018/9/25 11:44
 **/
@Data
public class ScheduleApiBean {

    /**
     * 日程id
     */
    private String scheduleId;

    /**
     * 日程名称
     */
    private String scheduleName;

    /**
     * 日程所在项目名称
     */
    private String projectName;

    /**
     * 开始时间
     */
    private long startTime;

    /**
     * 结束时间
     */
    private long endTime;

}
