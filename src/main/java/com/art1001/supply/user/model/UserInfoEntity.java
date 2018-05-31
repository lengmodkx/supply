package com.art1001.supply.user.model;

import com.art1001.supply.base.basemodel.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.Alias;

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
public class UserInfoEntity extends BaseEntity {

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
	/*
	 * 联系地址
	 */
	private String address;
	/*
	 * 添加日期时间
	 */
	private Date createTime;


	
	
	
}
