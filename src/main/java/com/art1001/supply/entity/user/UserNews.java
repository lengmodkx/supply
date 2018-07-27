package com.art1001.supply.entity.user;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * Entity
 */
@Data
public class UserNews extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * 消息id
	 */
	private String newsId;


	/**
	 * 消息名称
	 */
	private String newsName;


	/**
	 * 消息内容
	 */
	private String newsContent;


	/**
	 * 任务 文件 日程 分享 项目 id
	 */
	private String newsPublicId;


	/**
	 * 是否处理(0未处理 1已处理)
	 */
	private Integer newsHandle;


	/**
	 * 消息来自谁
	 */
	private String newsFromUser;


	/**
	 * 消息发给谁
	 */
	private String newsToUser;


	/**
	 * 提醒模式
	 */
	private String newsRemind;


	/**
	 * 消息的通知类型
	 */
	private String newsType;


	/**
	 * 消息通知数
	 */
	private Integer newsCount;


	/**
	 * 创建时间
	 */
	private Long careateTiem;

	/**
	 * 最后更新时间
	 */
	private Long updateTime;

}