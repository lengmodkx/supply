package com.art1001.supply.entity.chat;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

/**
 * Entity
 */
@Data
@ToString
public class Chat extends Model<Chat> {
	
	private static final long serialVersionUID = 1L;


	/**
	 * 聊天记录id
	 */
	private String chatId;


	/**
	 * 发送人id
	 */
	private String memberId;


	/**
	 * 聊天内容
	 */
	private String content;


	/**
	 * 聊天发送时间
	 */
	private long createTime;

	/**
	 * 项目Id
	 */
	private String projectId;

	@Override
	protected Serializable pkVal() {
		return this.chatId;
	}
}