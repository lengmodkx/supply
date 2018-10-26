package com.art1001.supply.entity.quartz;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import org.quartz.JobDataMap;

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
@TableName("prm_quartz")
public class QuartzInfo extends Model<QuartzInfo> {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * job的名字
     */
    private String jobName;

    /**
     * job组名
     */
    private String jobGroup;

    /**
     * 触发器组名
     */
    private String triggerGroup;

    /**
     * 任务提醒规则id
     */
    private String remindId;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 额外参数
     */
    @TableField(exist = false)
    private JobDataMap jobDataMap;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
