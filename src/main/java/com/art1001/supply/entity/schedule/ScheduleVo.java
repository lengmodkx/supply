package com.art1001.supply.entity.schedule;

import lombok.Data;

import java.util.List;
@Data
public class ScheduleVo {

    private String date;

    private String userId;

    private String proejctId;

    private List<Schedule> scheduleList;

}
