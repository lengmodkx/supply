package com.art1001.supply.entity.collect;

import java.io.Serializable;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * collectEntity
 */
@Data
public class PublicCollect extends Model<PublicCollect> {
	
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private String id;

	/**
	 * collect_id
	 */
	private String publicId;


	/**
	 * 用户id
	 */
	private String memberId;


	/**
	 * 项目id
	 */
	private String projectId;


	/**
	 * 收藏类型  任务，日程 文件 分享
	 */
	private String collectType;

	/** 创建时间
	 *
	 */
	private Long createTime;
	/** 修改时间*/
	private Long updateTime;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}