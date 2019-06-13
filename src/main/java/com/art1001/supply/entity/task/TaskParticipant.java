package com.art1001.supply.entity.task;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 任务参与者实体类
 */

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName("prm_task_member")
public class TaskParticipant extends Model<TaskParticipant> {

    @TableId(value = "id",type = IdType.UUID)
    private String Id;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 用户id
     */
    private String memberId;

    private String memberName;

    private String memberImg;

    private String type;

    private Long createTime;

    private Long updateTime;
    @Override
    protected Serializable pkVal() {
        return this.Id;
    }
}
