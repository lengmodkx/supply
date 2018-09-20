package com.art1001.supply.entity.fabulous;
import java.io.Serializable;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * 得赞Entity
 */
@Data
public class Fabulous extends Model<Fabulous> {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
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