package com.art1001.supply.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
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

    @TableId(value = "user_id",type = IdType.UUID)
    public String userId;

    /*
     * 用户真实姓名
     */
    private String userName;

    /**
     * 用户头像
     */
    private String image;


    @Override
    protected Serializable pkVal() {
        return userId;
    }
}
