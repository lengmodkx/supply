package com.art1001.supply.entity.collect;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * collectEntity
 */
@Data
@TableName(value = "prm_public_collect")
public class PublicCollect extends Model<PublicCollect> {
	
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId(value = "id",type = IdType.UUID)
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