package com.art1001.supply.entity.automation;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author heshaohua
 * @since 2019-08-07
 */
@Data
@TableName("prm_automation_rules")
public class AutomationRule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 项目id
     */
    @NotBlank(message = "项目id不能为空！")
    @Length(min = 32,max = 32,message = "项目id参数格式不正确！")
    private String projectId;

    /**
     * 规则名称
     */
    @NotBlank(message = "规则名称不能为空！")
    private String name;

    /**
     * 列表id
     */
    @NotBlank(message = "对象不能为空！")
    @Length(min = 32,max = 32,message = "对象参数格式不正确！")
    private String object;

    /**
     * 条件名称
     */
    @NotBlank(message = "条件名称不能为空！")
    private String conditionName;

    /**
     * 条件的值
     */
    private String conditionValue;

    /**
     * 指派人id
     */
    private String defaultAssign;

    /**
     * 自动跳转
     */
    private String automaticJump;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 创建人
     */
    private String createUser;
}
