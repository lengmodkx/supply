package com.art1001.supply.entity.share;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.art1001.supply.entity.tag.Tag;
import lombok.Data;

/**
 * shareEntity
 */
@Data
public class Share extends BaseEntity implements Serializable {

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
	 * 私密
	 * 0：公开  1：私密
	 */
	private Integer isPrivacy;

	private List<Tag> tagList = new ArrayList<>();

}
