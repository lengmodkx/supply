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
import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName Demand
 * @Author lemon lengmodkx@163.com
 * @Discription 需求表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prm_demand")
public class Demand extends Model<Demand>{

    /**
      * 需求id
      */
    @TableId(value = "demand_id",type = IdType.UUID)
    private String demandId;

    /**
      * 需求名称
      */
    private String demandName;


    /**
      * 需求详情
      */
    private String demandDetails;

    /**
      * 附件
      */
    private String demandFiles;

    /**
      * 需求预算
      */
    private BigDecimal bid;


    /**
      * 需求发布人id
      */
    private String memberId;


    /**
      * 是否删除 0否 1是
      */
    private Integer isDel;


    /**
      * 需求状态 0需求未承接 1需求已承接 2需求已取消 3需求已完成
      */
    private Integer demandState;

    /**
     * 是否审核 0否1是
     */
    private Integer isCheck;

    /**
     * 审核失败原因
     */
    private String checkFailReason;

    /**
     * 竞标企业
     */
    @TableField(exist = false)
    private List<DemandBid> bidList;

    /**
      * null
      */
    @JsonSerialize(using = LongToDeteSerializer.class)
    private Long createTime;

    /**
      * null
      */
    @JsonSerialize(using = LongToDeteSerializer.class)
    private Long updateTime;

    @Override
    protected Serializable pkVal() {
        return this.demandId;
    }
}
