package com.art1001.supply.entity.fabulous;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 得赞Entity
 */
@Data
@TableName("prm_fabulous")
public class Fabulous extends Model<Fabulous> {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	@TableId(value = "fabulous_id",type = IdType.AUTO)
	private Long fabulousId;


	/**
	 * 成员id
	 */
	private String memberId;


	/**
	 * 任务,文件,分享,日程id
	 */
	private String publicId;

	@Override
	protected Serializable pkVal() {
		return this.fabulousId;
	}
}