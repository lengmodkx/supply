package com.art1001.supply.entity.resource;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 *
 * @ClassName: ResourceEntity
 * @Description: 资源信息(权限)
 * @author gaogang
 * @date 2016年7月12日 下午2:41:27
 *
 */
@Data
@TableName(value = "tb_resource")
public class ResourceEntity extends Model<ResourceEntity> {

	private static final long serialVersionUID = 1L;

	/**
	 * 资源id
	 */
	@TableId(value = "resource_id",type = IdType.AUTO)
	public Integer resourceId;

	/**
	 * 父节点ID
	 */
	private Integer parentId;

	/**
	 * 权限名称
	 */
	private String resourceName;

	/**
	 * 资源标识key
	 */
	private String resourceKey;

	/**
	 * 类型：0：菜单；1：按钮
	 */
	private Integer resourceType;

	/**
	 * 菜单URL
	 */
	private String resourceUrl;

	/**
	 * 菜单的展开层级(暂不用)
	 */
	private Integer resourceLevel;

	/**
	 * 是否隐藏
	 */
	private Integer isHide;

	/**
	 * 资源描述
	 */
	private String description;

	/**
	 * 资源创建时间
	 */
	private Timestamp createTime;

	/**
	 * 资源更新时间
	 */
	private Timestamp updateTime;

	/**
	 * 子资源集合
	 */
	@TableField(exist = false)
	private List<ResourceEntity> subResource;

	/**
	 * 标识角色是否拥有该权限
	 */
	@TableField(exist = false)
	private boolean isHave;

	/**
	 * 用户判断角色是否拥有该权限的id
	 */
	@TableField(exist = false)
	private Integer rsId;

	@Override
	protected Serializable pkVal() {
		return this.resourceId;
	}
}
