package com.art1001.supply.entity.partment;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author DindDangMao
 * @since 2019-04-29
 */
public class PartmentMember implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * id
     */
    private String id;

    /**
     * 部门id
     */
    private String partmentId;

    /**
     * 用户id
     */
    private String memberId;

    /**
     * 是否是负责人  0:否   1:是 
     */
    private Boolean isMaster;

    /**
     * 成员身份 1:成员 2:拥有者 3:管理员
     */
    private Boolean memberLabel;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 最后更新时间
     */
    private Long updateTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPartmentId() {
        return partmentId;
    }

    public void setPartmentId(String partmentId) {
        this.partmentId = partmentId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public Boolean getMaster() {
        return isMaster;
    }

    public void setMaster(Boolean isMaster) {
        this.isMaster = isMaster;
    }

    public Boolean getMemberLabel() {
        return memberLabel;
    }

    public void setMemberLabel(Boolean memberLabel) {
        this.memberLabel = memberLabel;
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

    @Override
    public String toString() {
        return "PrmPartmentMember{" +
        "id=" + id +
        ", partmentId=" + partmentId +
        ", memberId=" + memberId +
        ", isMaster=" + isMaster +
        ", memberLabel=" + memberLabel +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
