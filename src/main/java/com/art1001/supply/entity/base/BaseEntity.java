package com.art1001.supply.entity.base;

import com.art1001.supply.util.DateUtils;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
public class BaseEntity {

	/** 创建时间-数据库字段
	 *
	 */
    private Long createTime;
	/** 修改时间-数据库字段 */
	private Long updateTime;
	/** 开始时间-数据库字段 */
	private Long startTime;
	/** 结束时间-数据库字段 */
	private Long endTime;
	/** 创建人 */
	private String createPerson;
	/** 修改人  */
	private String updatePerson;
	/** 创建时间－页面字段 */
	private Timestamp createTimeStr;
	/** 修改时间－页面字段 */
	private Timestamp updateTimeStr;
	/** 开始时间－页面字段 */
	private Timestamp startTimeStr;
	/** 结束时间－页面字段 */
	private Timestamp endTimeStr;
	/** 0:未删除;1.已删除 */
	private int isDel;

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
		if (null != createTime) {
			createTimeStr = DateUtils.getTimestampByLong(createTime);
		}
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
		if (null != updateTime) {
			updateTimeStr = DateUtils.getTimestampByLong(updateTime);
		}
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
		if (null != startTime) {
			this.startTimeStr = DateUtils.getTimestampByLong(startTime);
		}
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
		if (null != endTime) {
			this.endTimeStr = DateUtils.getTimestampByLong(endTime);
		}
	}

	/* 删除标记（0：正常；1：删除；2：审核；） */
	/** 正常 */
	public static final Integer DEL_FLAG_NORMAL = 0;
	/** 删除 */
	public static final Integer DEL_FLAG_DELETE = 1;
	/** 审核 */
	public static final Integer DEL_FLAG_AUDIT = 2;

	public Integer orderBy;
}
