package com.art1001.supply.entity.partment;

import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.omg.PortableInterceptor.INACTIVE;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author DindDangMao
 * @since 2019-04-29
 */
@Data
@TableName("prm_partment_member")
public class PartmentMember implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(type = IdType.UUID)
    private String id;

    /**
     * 部门id
     */
    private String partmentId;

    /**
     * 用户id
     */
    private String memberId;

    /**
     * 是否是负责人  0:否   1:是 
     */
    private Boolean isMaster;

    /**
     * 成员身份 1:成员 2:拥有者 3:管理员
     */
    private Integer memberLabel;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 最后更新时间
     */
    private Long updateTime;

    /**
     * 成员的基本信息
     */
    @TableField(exist = false)
    private UserEntity userEntity;

}
