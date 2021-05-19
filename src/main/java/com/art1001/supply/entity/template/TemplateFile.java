package com.art1001.supply.entity.template;

import com.art1001.supply.entity.file.FileApiBean;
import com.art1001.supply.entity.file.FileVersion;
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

import java.io.Serializable;
import java.util.List;

/**
 * fileEntity
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName("template_file")
public class TemplateFile extends Model<TemplateFile> {

    @Id
    @TableId(value = "file_id",type = IdType.ASSIGN_UUID)
    private String fileId;
    /**
     * 文件名
     */
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
    private String templateId;

    /**
     * 创建者
     */
    private String memberId;

    /**
     * 创建者姓名
     */
    @TableField(exist = false)
    private String memberName;

    /**
     * 创建者头像
     */
    @TableField(exist = false)
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
//    @Field(type = FieldType.text)
    private String parentId;

    /**
     * 是否删除
     */
    private Integer fileDel;

    /**
     * 文件创建者信息
     */
    @TableField(exist = false)
    private UserEntity user;

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
    private List<TemplateFile> files;


    @TableField(exist = false)
    private boolean show;

    /**
     * 从(文件,任务,分享,日程) 评论区上传的文件 或者 项目群聊上传的文件时候 的项目id 或者 文件,任务,分享,日程 的id
     */
    private String publicId;

    /**
     * 文件隐私模式 0所有成员可见，1参与者可见
     */
    private Integer filePrivacy;

    @Override
    protected Serializable pkVal() {
        return this.fileId;
    }

}