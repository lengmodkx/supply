package com.art1001.supply.entity.task.vo;

import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.task.Task;
import lombok.Data;

import java.util.List;

/**
 * @Description 任务首页显示的vo类
 * @Date:2019/3/19 17:23
 * @Author heshaohua
 **/
@Data
public class TaskShowVo {

    /**
     * 任务的实体信息
     */
    private Task task;

    /**
     * 当前用户是否对任务点赞
     */
    private boolean isFabulous;

    /**
     * 当前用户是否收藏了该任务
     */
    private boolean isCollect;

    /**
     * 当前任务的得赞数
     */
    private Integer fabulousCount;

    /**
     * 关联的文件
     */
    private List<File> bindingFiles;

    /**
     * 关联的任务
     */
    private List<Task> bindingTasks;

    /**
     * 关联的日程
     */
    private List<Schedule> bindingSchedules;

    /**
     * 关联的分享
     */
    private List<Share> bindingShares;

    /**
     * 任务的操作日志信息
     */
    private List<Log> logs;

}
