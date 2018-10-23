package com.art1001.supply.entity.log;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * @author heshaohua
 * @Title: Log
 * @Description: 日志实体类
 * @date 2018/7/25 12:00
 **/
@Data
@ToString
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
	private Long createTime;

	/**
	 * 创建时间字符串
	 */
	@TableField(exist = false)
	private String createTimeStr;

	public String getCreateTimeStr() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(createTime);
	}


	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}