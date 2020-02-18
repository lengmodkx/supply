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
 * @since 2019-06-18
 */
@Data
@TableName("sys_pro_role_user")
public class ProRoleUser implements Serializable {

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
     * 项目id
     */
    private String projectId;

    /**
     * 创建时间
     */
    private LocalDateTime tCreateTime;

}
