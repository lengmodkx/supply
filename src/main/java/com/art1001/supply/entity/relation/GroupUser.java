package com.art1001.supply.entity.relation;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author heshaohua
 * @since 2019-10-09
 */
@Data
@ToString
@TableName("prm_group_user")
public class GroupUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.UUID)
    private String id;

    /**
     * 分组id
     */
    @TableField("group_id")
    private String groupId;

    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;


}
