package com.art1001.supply.dtgrid.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 
 * dtgrid表格列对象，拷贝自大连首闻科技有限公司的DLShouWen Grid
 * @author wangyafeng
 * 2016年7月12日 下午3:05:29
 *
 */
@Data
@Accessors(chain = true)
public class Column {
	
	/**
	 * 编号
	 */
	private String id;
	
	/**
	 * 是否参与高级查询
	 */
	private String search;
	
	/**
	 * 是否作为导出列导出[default:true]
	 */
	private boolean export = true;
	
	/**
	 * 是否作为打印列打印[default:true]
	 */
	private boolean print = true;
	
	/**
	 * 是否作为扩展列隐藏备用[default:true(对于自定义的复选或相关操作内容，请设置为false以免数据冲突)]
	 */
	private boolean extra = true;
	
	/**
	 * 显示的列名
	 */
	private String title;
	
	/**
	 * 列宽
	 */
	private String width;
	
	/**
	 * 数据类型
	 */
	private String type;
	
	/**
	 * 格式化
	 */
	private String format;
	
	/**
	 * 原始数据类型
	 */
	private String otype;
	
	/**
	 * 原始格式
	 */
	private String oformat;
	
	/**
	 * 码表映射，用于高级查询及显示
	 */
	private Map<String, Object> codeTable;
	
	/**
	 * 列样式
	 */
	private String columnStyle;
	
	/**
	 * 列样式表
	 */
	private String columnClass;
	
	/**
	 * 列头样式
	 */
	private String headerStyle;
	
	/**
	 * 列头样式表
	 */
	private String headerClass;
	
	/**
	 * 彻底隐藏
	 */
	private boolean hide = false;
	
	/**
	 * 隐藏类别
	 */
	private String hideType;
	
	/**
	 * 快速查询
	 */
	private boolean fastQuery;
	
	/**
	 * 快速查询类别
	 */
	private String fastQueryType;
	
	/**
	 * 高级查询
	 */
	private boolean advanceQuery;
	
	/**
	 * 回调方法，参数：record value
	 */
	private String resolution;



}
