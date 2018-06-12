package com.art1001.supply.entity.user;


import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName: UserInfoEntity
 * @Description: 用户基本信息
 * @author wangyafeng
 * @date 2016年7月12日 下午2:38:43
 *
 */
@Data
@Accessors(chain = true)
@Alias("userInfoEntity")
public class UserInfoEntity implements Serializable {

	public String id;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * 性别
	 */
	private Integer sex;
	/*
	 * 出生日期
	 */
	private Date birthday;
	/*
	 * 手机
	 */
	private String telephone;
	/*
	 * 邮箱
	 */
	private String email;
	/**
	 * 头像Url
	 */
	private String image;
	/*
	 * 联系地址
	 */
	private String address;
	/*
	 * 添加日期时间
	 */
	private Date createTime;

}
