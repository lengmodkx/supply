package com.art1001.supply.entity.user;

import com.art1001.supply.shiro.util.JwtUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName("tb_user")
public class UserInfo extends Model<UserInfo> {
    private static final long serialVersionUID = -6743567631108323096L;


    /**
     * 用户id
     */
    @TableId(value = "user_id",type = IdType.UUID)
    public String userId;

    /**
     * 用户真实姓名
     */
    private String userName;

    /**
     * 用户名
     */
    private String accountName;

    /**
     * 默认企业id
     */
    @TableField(exist = false)
    private String orgId;

    @TableField(exist = false)
    private String orgName;
    /**
     * 用户头像
     */
    private String image;

    /**
     * 校验token
     */
    @TableField(exist = false)
    private String accessToken;
    /**
     * 是否绑定手机号，微信登录需要用到
     */
    @TableField(exist = false)
    private Boolean bindPhone;

    public String getAccessToken() {
        return accessToken = JwtUtil.sign(userId,"1qaz2wsx#EDC");
    }

    @Override
    protected Serializable pkVal() {
        return userId;
    }
}
