package com.art1001.supply.entity.file;
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

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName("prm_material_file")
@Document(indexName = "file",type = "docs", shards = 1, replicas = 0)
public class Material extends Model<Material> {
    @Id
    @TableId(value = "file_id",type = IdType.UUID)
    private String fileId;

    /**
     * 文件名
     */
    @Field(type = FieldType.text,analyzer = "ik_max_word" )
    private String fileName;

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
     * 创建者
     */
    @Field(type = FieldType.text)
    private String memberId;

    /**
     * 是否目录  1：目录  0：文件
     */
    @Field(type = FieldType.Integer)
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
     * 文件创建者信息
     */
    @TableField(exist = false)
    private UserEntity user;


    /**
     * 是否是模型文件(0:普通文件 1:模型文件)
     */
    private Integer isModel;

    /**
     * 文件的缩略图
     */
    private String fileThumbnail;

    /**
     * 模型文件路径
     */
    private String modelUrl;

    /**
     * 文件层级
     */
    private Integer level;

    /** 创建时间
     *
     */
    private Long createTime;

    /**
     *  修改时间
     */
    private Long updateTime;

    /**
     * 文件的下载量
     */
    private Integer fileDownloadCount;


    @Override
    protected Serializable pkVal() {
        return this.fileId;
    }
}
