package com.art1001.supply.entity.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ScheduleSimpleInfoVO
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/5/6 15:39
 * @Discription 日程简单信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleSimpleInfoVO {
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 日程id
     */
    private String scheduleId;
    /**
     * 日程名称
     */
    private String scheduleName;
    /**
     * 项目id
     */
    private String projectId;
    /**
     * 项目名称
     */
    private String projectName;
}
