package com.art1001.supply.base.basemodel;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 
 * 统一定义id的BaseEntity基类.基类统一定义id的属性名称、数据类型.子类可重载getId()函数.
 * @author wangyafeng
 * 2016年7月12日 下午2:59:56
 *
 */
@Data
@Accessors(chain = true)
public abstract class BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	public Long id;
	
}
