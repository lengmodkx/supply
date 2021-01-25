package com.art1001.supply.entity.demand;

import com.art1001.supply.util.LongToDeteSerializer;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName DemandBid
 * @Author lemon lengmodkx@163.com
 * @Discription 需求竞标表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prm_demand_bid",autoResultMap = true)
public class DemandBid extends Model<DemandBid>{

    /**
      * 主键id
      */
    private String id;

    /**
      * 需求id
      */
    private String demandId;

    /**
      * 竞标企业id
      */
    private String organizationId;

    /**
      * 竞标人id
      */
    private String memberId;

    /**
      * 服务详情
      */
    private String details;

    /**
      * 作品展示图
      */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<String> detailsImages;

    /**
      * 出价
      */
    private BigDecimal bid;

    /**
     * 是否中标 0否1是
     */
    private Integer state;

    /**
     * 是否删除 0否1是
     */
    private Integer isDel;
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

}
