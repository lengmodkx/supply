package com.art1001.supply.entity.tag;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * tagEntity
 */
@Data
public class Tag extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * tag_id
	 */
	private String tagId;


	/**
	 * 标签名称
	 */
	private String tagName;

}