package com.art1001.supply.entity.task;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author heshaohua
 * @since 2020-03-23
 */
@Data
@TableName("prm_task_working_hours")
public class TaskWorkingHours implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 小时数
     */
    private Double hours;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 创建人
     */
    private String createName;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 工时预计时间
     */
    private Long hoursDate;



    @Override
    public String toString() {
        return "TaskWorkingHours{" +
        "id=" + id +
        ", hours=" + hours +
        ", createTime=" + createTime +
        ", createName=" + createName +
        "}";
    }
}
