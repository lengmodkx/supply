package com.art1001.supply.entity.demand;

import com.art1001.supply.util.LongToDeteSerializer;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName DemandClass
 * @Author lemon lengmodkx@163.com
 * @Discription 需求分类表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prm_demand_class")
public class DemandClass extends Model<DemandClass>{

    /**
      * 需求分类id
      */
    @TableId(value = "ac_id",type = IdType.UUID)
    private String dcId;

    /**
      * 需求名称
      */
    private String dcName;

    /**
      * 父级id
      */
    private String parentId;

    /**
      * 创建时间
      */
    @JsonSerialize(using = LongToDeteSerializer.class)
    private Long createTime;

    /**
      * 修改时间
      */
    @JsonSerialize(using = LongToDeteSerializer.class)
    private Long updateTime;

    @TableField(exist = false)
    private List<DemandClass>list;

    @Override
    protected Serializable pkVal() {
        return this.dcId;
    }
}
