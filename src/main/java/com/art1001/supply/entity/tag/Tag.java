package com.art1001.supply.entity.tag;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import java.util.Objects;

import lombok.Data;

/**
 * tagEntity
 */
@Data
public class Tag extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * tag_id
	 */
	private Long tagId;

	/**
	 * 标签名称
	 */
	private String tagName;

	/**
	 * 背景颜色
	 */
	private String bgColor;

	/**
	 * 标签创建人
	 */
	private String memberId;

	/**
	 * 项目id
	 */
	private String projectId;

	/**
	 * 任务id
	 */
	private String taskId;

	/**
	 * 日程id
	 */
	private String scheduleId;

	/**
	 * 分享id
	 */
	private String shareId;


	private boolean flag;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Tag tag = (Tag) o;
		return flag == tag.flag &&
				Objects.equals(tagName, tag.tagName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), tagName);
	}
}