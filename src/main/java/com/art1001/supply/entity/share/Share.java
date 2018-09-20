package com.art1001.supply.entity.share;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.art1001.supply.entity.binding.BindingVo;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

/**
 * shareEntity
 */
@Data
@ToString
public class Share extends Model<Share> {

	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	private String id;


	/**
	 * 分享标题
	 */
	private String title;


	/**
	 * 分享内容
	 */
	private String content;

	/**
	 * 项目id
	 */
	private String projectId;


	/**
	 * 标签id
	 */
	private String tagIds;


	/**
	 * 用户id
	 */
	private String memberId;


	/**
	 * 用户名
	 */
	private String memberName;


	/**
	 * 头像
	 */
	private String memberImg;

	/**
	 * 分享的创建者信息
	 */
	private UserEntity userEntity;

	/**
	 * 创建时间
	 */
	private Long createTime;

	/**
	 * 更新时间
	 */
	private Long updateTime;

	/**
	 * 私密
	 * 0：公开  1：私密
	 */
	private Integer isPrivacy;

	/**
	 * 该分享的关联内容
	 */
	private BindingVo bindingVo;

	private List<Tag> tagList = new ArrayList<>();

	/**
	 * 该任务的 聊天记录 和  log 日志
	 */
	private List<Log> logs = new ArrayList<Log>();

	private Project project;

	/**
	 * 分享的参与者信息
	 */
	private String uids;

	List<UserEntity> joinInfo;

	/**
	 * 是否被登录用户收藏
	 */
	private int collect;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}
