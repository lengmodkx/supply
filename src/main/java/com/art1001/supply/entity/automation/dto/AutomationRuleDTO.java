package com.art1001.supply.entity.automation.dto;

import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.user.UserEntity;
import lombok.Data;

/**
 * @Description
 * @Date:2019/8/7 16:10
 * @Author heshaohua
 **/
@Data
public class AutomationRuleDTO {


    /**
     * id
     */
    private String id;

    /**
     * 项目id
     */
    private String projectId;

    /**
     * 规则名称
     */
    private String name;

    /**
     * 列表id
     */
    private Relation object;

    /**
     * 条件名称
     */
    private String conditionName;

    /**
     * 条件的值
     */
    private String conditionValue;

    /**
     * 指派人id
     */
    private UserEntity defaultAssign;

    /**
     * 自动跳转
     */
    private Relation automaticJump;

}
