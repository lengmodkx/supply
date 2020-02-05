package com.art1001.supply.entity.role;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 用户角色映射表
 * </p>
 *
 * @author heshaohua
 * @since 2019-05-28
 */
@Data
@TableName("sys_role_user")
public class RoleUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 角色id
     */
    private Integer roleId;

    /**
     * 用户id
     */
    private String uId;

    /**
     * 企业id
     */
    private String orgId;

    /**
     * 创建时间
     */
    private LocalDateTime tCreateTime;
}
