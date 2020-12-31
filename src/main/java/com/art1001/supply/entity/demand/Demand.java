package com.art1001.supply.entity.demand;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

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
      * 需求分类id
      */
    private String dcId;

    /**
      * 需求详情
      */
    private String demandDetails;

    /**
      * 附件
      */
    private String demandFile;

    /**
      * 需求预算
      */
    private BigDecimal demandBudget;

    /**
      * 解决方式
      */
    private Integer solveWay;

    /**
      * 需求发布人id
      */
    private String memberId;

    /**
      * 联系电话
      */
    private String memberTel;

    /**
      * 是否删除 0否 1是
      */
    private Integer isDel;

    /**
      * 托管金额
      */
    private BigDecimal hostingMoney;

    /**
      * 需求状态 1 需求已提交 2 需求待领取 3需求已完成
      */
    private Integer demandState;

    /**
      * 是否加急 0否 1是
      */
    private Integer isExpedited;

    /**
      * 是否置顶 0否 1是
      */
    private Integer isTop;

    /**
      * null
      */
    private Long createTime;

    /**
      * null
      */
    private Long updateTime;

    @Override
    protected Serializable pkVal() {
        return this.demandId;
    }
}
