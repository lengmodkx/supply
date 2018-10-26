package com.art1001.supply.entity.task;

import com.art1001.supply.entity.quartz.QuartzInfo;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author heshaohua
 * @since 2018-10-25
 */
@Data
@TableName("prm_task_remind")
public class TaskRemindRule extends Model<TaskRemindRule> {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 关联的任务id
     */
    private String taskId;

    /**
     * 任务提醒规则
     */
    @TableField(strategy=FieldStrategy.IGNORED)
    private String remindType;

    /**
     * 显示的数量
     */
    @TableField(strategy=FieldStrategy.IGNORED)
    private Integer num;

    /**
     * 时间单位
     */
    @TableField(strategy=FieldStrategy.IGNORED)
    private String timeType;

    /**
     * 自定义时间
     */
    @TableField(strategy=FieldStrategy.IGNORED)
    private String customTime;

    /**
     * 对应的quartz 信息
     */
    @TableField(exist = false)
    private QuartzInfo quartzInfo;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
