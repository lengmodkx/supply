package com.art1001.supply.entity.template;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName("template_relation")
public class TemplateRelation extends Model<TemplateRelation> {


    @TableId(type = IdType.UUID)
    private String relationId;

    private String relationName;

    private String templateId;

    private Long createTime;

    private Long updateTime;

    private Integer relationOrder;

    @Override
    protected Serializable pkVal() {
        return relationId;
    }
}
