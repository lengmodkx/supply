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
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.List;

/**
 * fileEntity
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName("prm_file")
@Document(indexName = "file",type = "docs", shards = 1, replicas = 0)
public class File extends Model<File> {

    private static final long serialVersionUID = 1L;



    @Id
    @TableId(value = "file_id",type = IdType.UUID)
    private String fileId;


    /**
     * 文件名
     */
    @Field(type = FieldType.text, analyzer = "ik_smart", searchAnalyzer="ik_max_word" )
    private String fileName;


    /**
     * 以逗号分隔的标签名
     */
    @TableField(exist = false)
    @Field(type = FieldType.text, analyzer = "ik_smart", searchAnalyzer="ik_max_word" )
    private String tagsName;


    /**
     * 文件后缀名
     */
    @Field(type = FieldType.text)
    private String ext;


    /**
     * 文件路径
     */
    @Field(type = FieldType.text)
    private String fileUrl;


    /**
     * 关联的项目id
     */
    @Field(type = FieldType.text)
    private String projectId;

    /**
     * 创建者
     */
    @Field(type = FieldType.text)
    private String memberId;


    /**
     * 创建者姓名
     */
    @Field(type = FieldType.text)
    private String memberName;

    /**
     * 创建者头像
     */
    @Field(type = FieldType.text)
    private String memberImg;

    /**
     * 是否目录  1：目录  0：文件
     */
    private Integer catalog;

    /**
     * 文件大小
     */
    @Field(type = FieldType.text)
    private String size;


    /**
     * 父级id 0：顶级目录   1：项目的根目录（隐藏目录，在oss上分的文件夹）
     */
    @Field(type = FieldType.text)
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
     * 文件的下载量
     */
    private Integer fileDownloadCount;

    /**
     * 子文件
     */
    @TableField(exist = false)
    private List<File> files;

    /**
     * 从(文件,任务,分享,日程) 评论区上传的文件 或者 项目群聊上传的文件时候 的项目id 或者 文件,任务,分享,日程 的id
     */
    private String publicId;

    private String userId;

    /**
     * 标记是否是重要文件  0:否 1:是
     */
    private Integer important;

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

}