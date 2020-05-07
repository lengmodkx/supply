package com.art1001.supply.entity.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName ScheduleListVO
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/5/6 15:38
 * @Discription 日程安排VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleListVO {
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 日程简单信息列表
     */
    private List<ScheduleSimpleInfoVO> scheduleSimpleInfoVOS;
}
