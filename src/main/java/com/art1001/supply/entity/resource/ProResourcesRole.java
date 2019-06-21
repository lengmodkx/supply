package com.art1001.supply.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 角色权限映射表
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
@Data
@TableName("sys_pro_resources_role")
public class ProResourcesRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 资源id
     */
    private String sId;

    /**
     * 角色id
     */
    private Integer rId;

    /**
     * 创建时间
     */
    private LocalDateTime tCreateTime;

}
