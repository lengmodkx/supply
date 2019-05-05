package com.art1001.supply.entity.tag;

import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.task.Task;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * tagEntity
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName("prm_tag")
public class Tag extends Model<Tag> {
	
	private static final long serialVersionUID = 1L;

	/**
	 * tag_id
	 */
	@TableId(value = "tag_id",type = IdType.AUTO)
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
	 * 项目
	 */
	private String projectId;

	/**
	 * 任务
	 */
	@TableField(exist = false)
	private List<Task> taskList = new ArrayList<>();

	/**
	 * 日程
	 */
	@TableField(exist = false)
	private List<Schedule> scheduleList = new ArrayList<>();

	/**
	 * 分享
	 */
	@TableField(exist = false)
	private List<Share> shareList = new ArrayList<>();

	/**
	 * 文件
	 */
	@TableField(exist = false)
	private List<File> fileList = new ArrayList<>();

	/**
	 * 判断标签是否选中
	 */
	@TableField(exist = false)
	private boolean flag;


	/**
	 * 创建时间
	 */
	private Long createTime;

	/**
	 * 修改时间
	 */
	private Long updateTime;


	private Integer isDel;

	@Override
	protected Serializable pkVal() {
		return this.tagId;
	}
}