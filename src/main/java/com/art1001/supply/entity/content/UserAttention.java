package com.art1001.supply.entity.content;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * @ClassName UserAttentionMapper
 * @Author lemon lengmodkx@163.com
 * @Discription 用户关注表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prm_user_attention")
public class UserAttention extends Model<UserAttention>{

    /**
      * 主键id
      */
    @TableId(value = "id",type = IdType.UUID)
    private String id;

    /**
      * 当前登录人id
      */
    private String memberId;

    /**
      * 被关注人id
      */
    private String attentionMemberId;

    /**
      * 创建时间
      */
    private Long createTime;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
