package com.art1001.supply.entity.chat;

import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.catalina.User;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Entity
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName("prm_chat")
public class Chat extends Model<Chat> {
	
	private static final long serialVersionUID = 1L;


	/**
	 * 聊天记录id
	 */
	@TableId(value = "chat_id",type = IdType.UUID)
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

	/**
	 * 标识信息是否被撤回
	 */
	private int chatDel;

	/**
	 * 群聊文件
	 */
	@TableField(exist = false)
	private List<File> fileList;

	/**
	 * 消息发送人的相关信息
	 */
	@TableField(exist = false)
	private UserEntity user;

	@Override
	protected Serializable pkVal() {
		return this.chatId;
	}
}