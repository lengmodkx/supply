package com.art1001.supply.dtgrid.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 
 * dtgrid表格条件查询对象，拷贝自大连首闻科技有限公司的DLShouWen Grid
 * @author wangyafeng
 * 2016年7月12日 下午3:07:47
 *
 */
@Data
@Accessors(chain = true)
public class Condition {
	
	/**
	 * 左括号
	 */
	private String leftParentheses;
	
	/**
	 * 字段名称
	 */
	private String field;
	
	/**
	 * 条件
	 */
	private String condition;
	
	/**
	 * 值
	 */
	private String value;
	
	/**
	 * 右括号
	 */
	private String rightParentheses;
	
	/**
	 * 查询逻辑
	 */
	private String logic;


}
