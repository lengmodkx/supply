package com.art1001.supply.entity.base;

import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.task.Task;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author heshaohua
 * @Title: PublicVO
 * @Description: TODO
 * @date 2018/8/9 17:30
 **/
@Data
public class PublicVO {
    private String name;
    private List<Task> taskList = new ArrayList<Task>();
    private List<Schedule> scheduleList = new ArrayList<Schedule>();
}
