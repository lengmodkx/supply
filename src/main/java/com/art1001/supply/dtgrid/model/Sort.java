package com.art1001.supply.dtgrid.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 
 * dtgrid表格排序对象，拷贝自大连首闻科技有限公司的DLShouWen Grid
 * @author wangyafeng
 * 2016年7月12日 下午3:08:20
 *
 */
@Data
@Accessors(chain = true)
public class Sort {
	
	/**
	 * 字段
	 */
	private String field;
	
	/**
	 * 排序逻辑
	 */
	private String logic;


	
}
