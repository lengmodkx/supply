package com.art1001.supply.entity.chat;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * Entity
 */
@Data
public class Chat extends BaseEntity implements Serializable {
	
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


	/**
	 * 项目Id
	 */
	private String projectId;

}