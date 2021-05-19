package com.art1001.supply.entity.template;

import com.art1001.supply.entity.task.Task;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName("template_relation")
public class TemplateRelation extends Model<TemplateRelation> {


    /**
     * relation_id
     */
    @TableId(value = "relation_id",type = IdType.ASSIGN_UUID)
    private String relationId;

    /**
     * 分组的创建人id
     */
    private String creator;

    /**
     * 名称
     */
    private String relationName;


    /**
     * 父级id
     */
    private String parentId;


    /**
     * 项目id
     */
    private String templateId;

    /**
     * 标识是分组还是菜单
     */
    @TableField("lable")
    private Integer lable;

    /**
     * 删除分组/菜单 0不删除，1删除
     */
    @TableField("relation_del")
    private Integer relationDel;

    @TableField(exist = false)
    private List<Task> taskList;

    /**
     * 菜单的当前顺序
     */
    @TableField("`order`")
    private Integer order;

    /**
     * 创建时间
     */
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private Long createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

    /**
     * 是否有任务
     */
    @TableField(exist = false)
    private Boolean subIsExist;


    private  Integer defaultGroup;


    @Override
    protected Serializable pkVal() {
        return this.relationId;
    }
}
