package com.art1001.supply.entity.resource;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
@Accessors(chain = true)
@Alias("resourceEntity")
public class ResourceEntity extends Model<ResourceEntity> {

	public Long id;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * 父节点ID
	 */
	private Integer parentId;
	/*
	 * 权限名称
	 */
	private String name;
	/*
	 * 资源标识key
	 */
	private String sourceKey;
	/*
	 * 类型：0：菜单；1：按钮
	 */
	private Integer type;
	/*
	 * 菜单URL
	 */
	private String sourceUrl;
	/*
	 * 菜单的展开层级(暂不用)
	 */
	private Integer level;
	/*
	 * 菜单的图标
	 */
	private String icon;
	/*
	 * 是否隐藏
	 */
	private Integer isHide;
	/*
	 * 资源描述
	 */
	private String description;
	/*
	 * 资源创建时间
	 */
	private Date createTime;
	/*
	 * 资源更新时间
	 */
	private Date updateTime;
	/*
	 * 节点是否展开
	 */
	private boolean isExpanded;
	/*
	 * 是否叶子节点
	 */
	private boolean isLeaf;
	/*
	 * 是否加载完成
	 */
	private boolean loaded = true;
	/*
	 * 父节点名称
	 */
	private String parentName;
	/*
	 * 是否被选中
	 */
	private boolean selected;
	/*
	 * 叶子节点集合
	 */
	private List<ResourceEntity> children = new ArrayList<ResourceEntity>();


	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}
