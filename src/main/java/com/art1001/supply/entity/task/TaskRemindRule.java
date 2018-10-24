package com.art1001.supply.entity.task;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author heshaohua
 * @since 2018-10-24
 */
@Data
public class TaskRemindRule extends Model<TaskRemindRule> {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 关联的任务id
     */
    private String taskId;

    /**
     * quartz任务的名称
     */
    private String jobName;

    /**
     * 任务提醒规则
     */
    private String remindType;

    /**
     * 显示的数量
     */
    private Integer num;

    /**
     * 时间单位
     */
    private String timeType;

    /**
     * 该规则的 cron时间表达式
     */
    private String timeCron;

    /**
     * 自定义时间
     */
    private String customTime;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
