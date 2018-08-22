package com.art1001.supply.entity.fabulous;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * 得赞Entity
 */
@Data
public class Fabulous extends BaseEntity implements Serializable {
	
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

}