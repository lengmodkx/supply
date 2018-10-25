package com.art1001.supply.entity.project;

import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.task.Task;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;


@Data
@ToString
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
    @TableField("project_name")
    private String projectName;

    /**
     * 用户id，项目创建人
     */
    @TableField("member_id")
    private String memberId;

    /**
     * 项目开始时间
     */
    @TableField("start_time")
    private Long startTime;

    /**
     * 项目结束时间 0：没有 1：
     */
    @TableField("end_time")
    private Long endTime;

    /**
     * 项目封面
     */
    @TableField("project_name")
    private String projectCover;

    /**
     * 描述
     */
    @TableField("project_des")
    private String projectDes;

    /**
     * 是否删除  0：未删除  1：已删除
     */
    @TableField("project_del")
    private Integer projectDel;

    /**
     * 状态
     */
    @TableField("project_status")
    private Integer projectStatus;
    /**
     * 项目是否公开，0私有，1公开
     */
    @TableField("is_public")
    private Integer isPublic;

    /**
     * 项目是否开启推送提醒
     */
    @TableField("project_remind")
    private Integer projectRemind;

    /**
     * 项目插件
     */
    @TableField("func")
    private String func;

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
    @TableField("create_time")
    private Long createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Long updateTime;

    /**
     * 企业id
     */
    @TableField("organization_id")
    private String organizationId;

    /**
     * 默认组
     * @return
     */
    private String groupId;

    @Override
    protected Serializable pkVal() {
        return this.projectId;
    }
}