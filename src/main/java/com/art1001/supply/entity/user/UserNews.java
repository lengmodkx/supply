package com.art1001.supply.entity.user;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * Entity
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName(value = "prm_user_news")
public class UserNews extends Model<UserNews> {
	
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
	 * 消息来自谁 id
	 */
	private String newsFromUserId;


	/**
	 * 消息发给谁 id
	 */
	private String newsToUserId;


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
	private Long createTime;

	/**
	 * 最后更新时间
	 */
	private Long updateTime;

	/**
	 * 消息发给谁的实体信息
	 */
	private UserEntity newsToUser;

	/**
	 * 消息来自谁的实体信息
	 */
	private UserEntity newsFromUser;

	public UserNews(String newsId, String newsName, String newsContent, String newsPublicId, Integer newsHandle, String newsFromUserId, String newsToUserId, String newsType, Integer newsCount, Long createTime, Long updateTime) {
		this.newsId = newsId;
		this.newsName = newsName;
		this.newsContent = newsContent;
		this.newsPublicId = newsPublicId;
		this.newsHandle = newsHandle;
		this.newsFromUserId = newsFromUserId;
		this.newsToUserId = newsToUserId;
		this.newsType = newsType;
		this.newsCount = newsCount;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	public UserNews() {
	}

	@Override
	protected Serializable pkVal() {
		return this.newsId;
	}
}