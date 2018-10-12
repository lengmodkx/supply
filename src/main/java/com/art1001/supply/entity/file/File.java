package com.art1001.supply.entity.file;

import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * fileEntity
 */
@Data
@ToString
@TableName("prm_file")
public class File extends Model<File> {

    private static final long serialVersionUID = 1L;


    /**
     * file_id
     */
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
    private String projectId;

    /**
     * 创建者
     */
    private String memberId;

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
    private String parentId = "0";

    /**
     * 是否删除
     */
    private Integer fileDel = 0;

    /**
     * 该文件的附属项目的实体信息
     */
    @TableField(exist = false)
    private Project project;

    /**
     * 标签id
     */
    private String tagId;

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
     * 所有文件参与者的Id
     */
    private String fileUids;

    /**
     * 文件的缩略图
     */
    private String fileThumbnail;

    /**
     * 文件全路径
     */
    private String fileUrlTemp;

    /**
     * 文件标识
     */
    private int fileLabel;

    /**
     * 是否是其他信息上传的文件(0.普通文件 1.其他文件)
     */
    private int publicLable;

    /**
     * 文件层级
     */
    @TableField("level")
    private int level;
    /** 创建时间
     *
     */
    private Long createTime;
    /** 修改时间*/
    private Long updateTime;
    /**
     * 从(文件,任务,分享,日程) 评论区上传的文件 或者 项目群聊上传的文件时候 的项目id 或者 文件,任务,分享,日程 的id
     */
    private String publicId;

    /**
     * 文件隐私模式 0所有成员可见，1参与者可见
     */
    private int filePrivacy;

    public String getFileUrlTemp(){
        try {
            return URLEncoder.encode("https://art1001-bim-5d.oss-cn-beijing.aliyuncs.com/"+fileUrl,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    protected Serializable pkVal() {
        return this.fileId;
    }
}