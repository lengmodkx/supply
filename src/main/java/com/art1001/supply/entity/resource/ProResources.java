package com.art1001.supply.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 资源表
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
@TableName("sys_pro_resources")
@Data
public class ProResources implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资源id
     */
    @TableId(value = "s_id", type = IdType.AUTO)
    private Integer sId;

    /**
     * 资源父id
     */
    private Integer sParentId;

    /**
     * 资源名称
     */
    private String sName;

    /**
     * 资源唯一标识
     */
    private String sSourceKey;

    /**
     * 资源类型,0:目录;1:菜单;2:按钮
     */
    private Integer sType;

    /**
     * 资源url
     */
    private String sSourceUrl;

    /**
     * 层级
     */
    private Integer sLevel;

    /**
     * 图标
     */
    private String sIcon;

    /**
     * 是否隐藏
     */
    private Integer sIsHide;

    /**
     * 描述
     */
    private String sDescription;

    /**
     * 创建时间
     */
    private LocalDateTime sCreateTime;

    /**
     * 更新时间
     */
    private LocalDateTime sUpdateTime;

}
