package com.art1001.supply.entity.project;

import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.task.Task;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;


@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName("prm_project")
public class Project extends Model<Project> {

    private static final long serialVersionUID = 1L;

    /**
     * project_id
     */
    @TableId("project_id")
    private String projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 用户id，项目创建人
     */
    private String memberId;
    /**
     * 项目开始时间
     */
    private Long startTime;

    /**
     * 项目结束时间
     */
    private Long endTime;

    /**
     * 项目封面
     */
    private String projectCover;

    /**
     * 描述
     */
    private String projectDes;

    /**
     * 是否删除  0：未删除  1：已删除
     */
    private Integer projectDel;

    /**
     * 状态
     */
    private Integer projectStatus;
    /**
     * 项目是否公开，0私有，1公开
     */
    private Integer isPublic;

    /**
     * 项目是否开启推送提醒
     */
    private Integer projectRemind;

    /**
     * 项目插件
     */
    private String func;

    /**
     * 项目创建的名称
     */
    @TableField(exist = false)
    private String memberName;

    /**
     * 项目下的所有任务
     */
    @TableField(exist = false)
    private List<Task> taskList;

    /**
     * 项目成员
     */
    @TableField(exist = false)
    private List<ProjectMember> projectMemberList;

    /**
     * 项目日志
     */
    @TableField(exist = false)
    private List<Log> logList;

    /**项目是否被收藏 0否，1是*/
    @TableField(exist = false)
    private int collect;

    /**
     * 项目成员标识 1用于者，0成员
     */
    @TableField(exist = false)
    private int memberLabel;

    @TableField(exist = false)
    private int label=0;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private Long updateTime;

    /**
     * 企业id
     */
    private String organizationId;

    /**
     * 父级项目id
     */
    private String parentId;

    /**
     * 默认组
     * @return
     */
    @TableField(exist = false)
    private String groupId;

    /**
     * 是否添加 0未添加 1已添加
     */
    @TableField(exist = false)
    private Integer isAdd;

    /**
     * 项目进度
     */
    @TableField(exist = false)
    private Integer projectSchedule;

    @Override
    protected Serializable pkVal() {
        return this.projectId;
    }
}