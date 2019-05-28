package com.art1001.supply.entity.file;

import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.schedule.ScheduleApiBean;
import com.art1001.supply.entity.share.ShareApiBean;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.TaskApiBean;
import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.List;

/**
 * fileEntity
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName("prm_file")
//@Document(indexName = "file",type = "docs", shards = 1, replicas = 0)
public class File extends Model<File> {

    private static final long serialVersionUID = 1L;

   /* @Id
    private Long id= 111L;
*/



    @TableId(value = "file_id",type = IdType.UUID)
    private String fileId;


    /**
     * 文件名
     */
    //@Field(type = FieldType.text, analyzer = "ik_max_word")
    private String fileName;

    /**
     * 文件后缀名
     */
    private String ext;


    /**
     * 文件路径
     */
    private String fileUrl;


    /**
     * 关联的项目id
     */
    private String projectId;

    /**
     * 创建者
     */
    private String memberId;


    /**
     * 创建者姓名
     */
    private String memberName;

    /**
     * 创建者头像
     */
    private String memberImg;

    /**
     * 是否目录  1：目录  0：文件
     */
    private Integer catalog;

    /**
     * 文件大小
     */
    private String size;


    /**
     * 父级id 0：顶级目录   1：项目的根目录（隐藏目录，在oss上分的文件夹）
     */
    private String parentId;

    /**
     * 是否删除
     */
    private Integer fileDel;

    /**
     * 该文件的附属项目的实体信息
     */
    @TableField(exist = false)
    private Project project;

    /**
     * 文件创建者信息
     */
    @TableField(exist = false)
    private UserEntity userEntity;

    /**
     * 标签的集合
     */
    @TableField(exist = false)
    private List<Tag> tagList;

    /**
     * 文件参与者信息
     */
    @TableField(exist = false)
    private List<UserEntity> joinInfo;

    /**
     * 文件操作日志
     */
    @TableField(exist = false)
    private List<Log> logList;

    /**
     * 所有文件参与者的Id
     */
    private String fileUids;

    /**
     * 是否是模型文件(0:普通文件 1:模型文件)
     */
    private Integer isModel;

    /**
     * 文件的缩略图
     */
    private String fileThumbnail;

    /**
     * 文件标识
     */
    private Integer fileLabel;

    /**
     * 是否是其他信息上传的文件(0.普通文件 1.其他文件)
     */
    private Integer publicLable;

    /**
     * 文件层级
     */
    private Integer level;

    /** 创建时间
     *
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    /**
     *  修改时间
     */
    private Long updateTime;

    /**
     * 子文件
     */
    @TableField(exist = false)
    private List<File> files;

    /**
     * 从(文件,任务,分享,日程) 评论区上传的文件 或者 项目群聊上传的文件时候 的项目id 或者 文件,任务,分享,日程 的id
     */
    private String publicId;

    @TableField(exist = false)
    private List<TaskApiBean> bindTasks;

    @TableField(exist = false)
    private List<FileApiBean> bindFiles;

    @TableField(exist = false)
    private List<ScheduleApiBean> bindSchedules;

    @TableField(exist = false)
    private List<ShareApiBean> bindShares;

    /**
     * 文件隐私模式 0所有成员可见，1参与者可见
     */
    private Integer filePrivacy;
    @Override
    protected Serializable pkVal() {
        return this.fileId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberImg() {
        return memberImg;
    }

    public void setMemberImg(String memberImg) {
        this.memberImg = memberImg;
    }

    public Integer getCatalog() {
        return catalog;
    }

    public void setCatalog(Integer catalog) {
        this.catalog = catalog;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getFileDel() {
        return fileDel;
    }

    public void setFileDel(Integer fileDel) {
        this.fileDel = fileDel;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public List<UserEntity> getJoinInfo() {
        return joinInfo;
    }

    public void setJoinInfo(List<UserEntity> joinInfo) {
        this.joinInfo = joinInfo;
    }

    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }

    public String getFileUids() {
        return fileUids;
    }

    public void setFileUids(String fileUids) {
        this.fileUids = fileUids;
    }

    public Integer getIsModel() {
        return isModel;
    }

    public void setIsModel(Integer isModel) {
        this.isModel = isModel;
    }

    public String getFileThumbnail() {
        return fileThumbnail;
    }

    public void setFileThumbnail(String fileThumbnail) {
        this.fileThumbnail = fileThumbnail;
    }

    public Integer getFileLabel() {
        return fileLabel;
    }

    public void setFileLabel(Integer fileLabel) {
        this.fileLabel = fileLabel;
    }

    public Integer getPublicLable() {
        return publicLable;
    }

    public void setPublicLable(Integer publicLable) {
        this.publicLable = publicLable;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
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

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public List<TaskApiBean> getBindTasks() {
        return bindTasks;
    }

    public void setBindTasks(List<TaskApiBean> bindTasks) {
        this.bindTasks = bindTasks;
    }

    public List<FileApiBean> getBindFiles() {
        return bindFiles;
    }

    public void setBindFiles(List<FileApiBean> bindFiles) {
        this.bindFiles = bindFiles;
    }

    public List<ScheduleApiBean> getBindSchedules() {
        return bindSchedules;
    }

    public void setBindSchedules(List<ScheduleApiBean> bindSchedules) {
        this.bindSchedules = bindSchedules;
    }

    public List<ShareApiBean> getBindShares() {
        return bindShares;
    }

    public void setBindShares(List<ShareApiBean> bindShares) {
        this.bindShares = bindShares;
    }

    public Integer getFilePrivacy() {
        return filePrivacy;
    }

    public void setFilePrivacy(Integer filePrivacy) {
        this.filePrivacy = filePrivacy;
    }
}