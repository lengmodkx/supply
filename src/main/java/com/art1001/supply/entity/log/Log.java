package com.art1001.supply.entity.log;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.alibaba.fastjson.annotation.JSONField;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.task.Task;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Reader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
	@NotNull(message = "publicId不能为空")
	private String publicId;

	/**
	 * 任务id
	 */
	@TableField(exist = false)
	private String taskId;

	@TableField(exist = false)
	private String groupId;
	/**
	 * 任务名
	 */
	@TableField(exist = false)
	private String taskName;

	@NotNull(message = "logFlag不能为空")
	private Integer logFlag;
	/**
	 * 项目id
	 */
	@NotNull(message = "项目id不能为空")
	private String projectId;

	/**
	 * 项目名
	 */
	@Excel(name = "项目名")
	@TableField(exist = false)
	private String projectName;

	/**
	 * 用户id
	 */
	@Excel(name = "用户id")
	private String memberId;


	/**
	 * 用户名
	 */
	@Excel(name = "用户名")
	@TableField(exist = false)
	private String memberName;

	/**
	 * 电话号
	 */
	@Excel(name = "电话号")
	@TableField(exist = false)
	private String memberPhone;


	/**
	 * 头像
	 */
	@TableField(exist = false)
	private String memberImg;

	/**
	 * 内容
	 */
	@Excel(name = "内容")
	@NotNull(message = "发送内容不能为空")
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

	@Excel(name = "创建时间")
	@TableField(exist = false)
	private String outPutTime;

	/**
	 * 时间字符串
	 */
	@TableField(exist = false)
	private String dateStr;

	/**
	 * 被@的用户id (逗号隔开)
	 */
	private String mentions;

	/**
	 * 任务列表
	 */
	@TableField(exist = false)
	private List<Task> tasks;


	/**
	 *文件ids，现在用不到
	 */
	private Reader fileIds;

	/**
	 * 是否为撤回的消息 0正常 1撤回
	 */
	private Integer logIsWithDraw;
	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}