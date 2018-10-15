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
	@TableId(value = "s_id",type = IdType.AUTO)
	public Integer id;

	/**
	 * 父节点ID
	 */
	@TableField(value = "s_parent_id")
	private Integer parentId;

	/**
	 * 权限名称
	 */
	@TableField(value = "s_name")
	private String name;

	/**
	 * 资源标识key
	 */
	@TableField(value = "s_source_key")
	private String sourceKey;

	/**
	 * 类型：0：菜单；1：按钮
	 */
	@TableField(value = "s_type")
	private Integer type;

	/**
	 * 菜单URL
	 */
	@TableField(value = "s_source_url")
	private String sourceUrl;

	/**
	 * 菜单的展开层级(暂不用)
	 */
	@TableField(value = "s_level")
	private Integer level;

	/**
	 * 是否隐藏
	 */
	@TableField(value = "s_is_hide")
	private Integer isHide;

	/**
	 * 资源描述
	 */
	@TableField(value = "s_description")
	private String description;

	/**
	 * 资源创建时间
	 */
	@TableField(value = "s_create_time")
	private Timestamp createTime;

	/**
	 * 资源更新时间
	 */
	@TableField(value = "s_update_time")
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
		return this.id;
	}
}
