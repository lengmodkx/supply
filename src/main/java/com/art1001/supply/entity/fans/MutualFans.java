/**
 * 〈一句话功能简述〉<br>
 * 〈互粉表实体〉
 *
 * @author 邓凯欣 dengkaixin@art1001.com
 * @create 2021/1/1
 * @since 1.0.0
 */
package com.art1001.supply.entity.fans;

import com.art1001.supply.util.LongToDeteSerializer;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName("prm_mutual_fans")
public class MutualFans extends Model<MutualFans> {
    /**
     * 主键id
     */
    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 互粉角色id
     */
    private String mutualMemberId;
    /***
     * 互粉角色id
     */
    private String mutualFansId;
    /***
     * 创建时间
     */
    @JsonSerialize(using = LongToDeteSerializer.class)
    private Long createTime;
    /**
     * 修改时间
     */
    @JsonSerialize(using = LongToDeteSerializer.class)
    private Long updateTime;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
