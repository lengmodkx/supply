package com.art1001.supply.entity.role;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 角色权限映射
 * </p>
 *
 * @author 少华
 * @since 2018-09-26
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName("sys_resources_role")
public class ResourcesRole extends Model<ResourcesRole> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 资源id
     */
    @TableField("s_id")
    private String resourceId;

    /**
     * 角色id
     */
    @TableField("r_id")
    private Integer roleId;

    /**
     * 创建时间
     */
    @TableField("t_create_time")
    private LocalDateTime createTime;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
