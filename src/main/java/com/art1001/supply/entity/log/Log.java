package com.art1001.supply.entity.log;

import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

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
	private String id;


	/**
	 * 任务id
	 */
	private String publicId;

	/**
	 * 下面id
	 */
	private String projectId;
	/**
	 * 用户id
	 */
	private String memberId;

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
	 * (任务,文件,日程,分享) 的日志区分
	 */
	private int logFlag;

	/**
	 * 返回的结果集
	 */
	@TableField(exist = false)
	private int result;

	private String fileIds;
	/**
	 * 用户实体信息
	 */
	private UserEntity userEntity;

	@TableField(exist = false)
	private List<File> fileList;

	/**
	 * 标记是否为撤回消息
	 * (0.正常 1.撤回)
	 */
	private int logIsWithdraw;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}