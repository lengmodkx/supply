package com.art1001.supply.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
@Data
@TableName("sys_pro_resource_role_bind_template")
public class ProResourceRoleBindTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色key
     */
    private String roleKey;

    /**
     * 资源id 逗号隔开
     */
    private String resourceId;

}
