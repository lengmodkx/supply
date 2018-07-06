package com.art1001.supply.entity.schedule;

import lombok.Data;

import java.util.List;
@Data
public class ScheduleVo {

    private String date;

    private List<Schedule> scheduleList;

}
