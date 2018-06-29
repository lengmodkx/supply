package com.art1001.supply.entity.file;

import com.art1001.supply.entity.base.BaseEntity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * fileEntity
 */
@Data
public class File extends BaseEntity implements Serializable {

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
     * 用户名
     */
    private String memberName;


    /**
     * member_img
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
    private String parentId = "0";


    /**
     * 是否删除
     */
    private Integer fileDel = 0;


    /**
     * 标签id
     */
    private String tagId;

}