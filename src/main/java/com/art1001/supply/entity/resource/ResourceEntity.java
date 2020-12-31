package com.art1001.supply.entity.resource;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName(value = "sys_resources")
public class ResourceEntity extends Model<ResourceEntity> {

	private static final long serialVersionUID = 1L;

	/**
	 * 资源id
	 */
	@TableId(value = "s_id",type = IdType.AUTO)
	public Integer resourceId;

	/**
	 * 父节点ID
	 */
	@TableField("s_parent_id")
	private Integer parentId;

	/**
	 * 权限名称
	 */
	@TableField("s_name")
	private String resourceName;

	/**
	 * 资源标识key
	 */
	@TableField("s_source_key")
	private String resourceKey;

	/**
	 * 类型：0：菜单；1：按钮
	 */
	@TableField("s_type")
	private Integer resourceType;

	/**
	 * 菜单URL
	 */
	@TableField("s_source_url")
	private String resourceUrl;

	/**
	 * 菜单的展开层级(暂不用)
	 */
	@TableField("s_level")
	private Integer resourceLevel;

	/**
	 * 是否隐藏
	 */
	@TableField("s_is_hide")
	private Integer isHide;

	/**
	 * 资源描述
	 */
	@TableField("s_description")
	private String description;

	/**
	 * 资源创建时间
	 */
	@TableField("s_create_time")
	private Timestamp createTime;

	/**
	 * 资源更新时间
	 */
	@TableField("s_update_time")
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
