package com.art1001.supply.entity.log;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author heshaohua
 * @Title: Log
 * @Description: 日志实体类
 * @date 2018/7/25 12:00
 **/
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName("prm_log")
public class Log extends Model<Log> {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	@TableId(value = "id",type = IdType.UUID)
	private String id;


	/**
	 * 任务，分享，日程，文件
	 */
	private String publicId;

	/**
	 * 项目id
	 */
	private String projectId;
	/**
	 * 用户id
	 */
	private String memberId;


	/**
	 * 用户名
	 */
	@TableField(exist = false)
	private String memberName;


	/**
	 * 头像
	 */
	@TableField(exist = false)
	private String memberImg;

	/**
	 * 内容
	 */
	private String content;

	/**
	 * 日志的类型  0: 日志  1:聊天内容
	 */
	private Integer logType;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private Long createTime;

	/**
	 * 时间字符串
	 */
	@TableField(exist = false)
	private String dateStr;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}