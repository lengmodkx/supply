package com.art1001.supply.entity.task;

import java.util.List;

/**
 * @author heshaohua
 * 任务收藏的实体类
 */
public class TaskCollect {
    private String id;

    private String memberId;

    private String taskId;

    private Long createTime;

    private Long updateTime;

    private String memberImg;

    private String memberName;

    public TaskCollect(String id, String memberId, String taskId, Long createTime, Long updateTime, String memberImg, String memberName) {
        this.id = id;
        this.memberId = memberId;
        this.taskId = taskId;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.memberImg = memberImg;
        this.memberName = memberName;
    }

    public TaskCollect() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId == null ? null : memberId.trim();
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId == null ? null : taskId.trim();
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getMemberImg() {
        return memberImg;
    }

    public void setMemberImg(String memberImg) {
        this.memberImg = memberImg == null ? null : memberImg.trim();
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName == null ? null : memberName.trim();
    }

}