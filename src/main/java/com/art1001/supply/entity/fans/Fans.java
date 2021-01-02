/**
 * 〈一句话功能简述〉<br>
 * 〈粉丝表实体〉
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
@TableName("prm_fans")
public class Fans extends Model<Fans> {
    /**
     * 主键id
     */
    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 被关注人id
     */
    private String followedId;
    /**
     * 粉丝id
     */
    private String fansId;
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

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
