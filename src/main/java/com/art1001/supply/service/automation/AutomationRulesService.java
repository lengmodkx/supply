package com.art1001.supply.service.automation;

import com.art1001.supply.entity.automation.AutomationRule;
import com.art1001.supply.entity.automation.dto.AutomationRuleDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author heshaohua
 * @since 2019-08-07
 */
public interface AutomationRulesService extends IService<AutomationRule> {

    /**
     * 创建自动化规则
     * @author heShaoHua
     * @describe 暂无
     * @param automationRule 自动化规则信息
     * @updateInfo 暂无
     * @date 2019/8/7 14:29
     * @return 成功返回 1
     */
    int saveAutomationRule(AutomationRule automationRule);

    /**
     * 根据id获取自动化规则的数据信息
     * @author heShaoHua
     * @describe 暂无
     * @param id 自动化规则id
     * @updateInfo 暂无
     * @date 2019/8/7 16:16
     * @return 自动化规则封装数据
     */
    AutomationRuleDTO getRuleById(String id);

    /**
     * 根据自动化规则名称查询项目下是否已经存在该名称的自动化规则信息
     * @param projectId 项目id
     * @param ruleName 规则名称
     * @return 是否存在
     */
    Boolean checkAutomationRuleIsExistByName(String projectId, String ruleName);

    /**
     * 获取自动化规则
     * @author heShaoHua
     * @describe 暂无
     * @param projectId 项目id
     * @updateInfo 暂无
     * @date 2019/8/7 17:30
     * @return 自动化规则信息列表
     */
    List<AutomationRule> getAutoRuleListByProject(String projectId);

    /**
     * 更新自动化规则名称
     * @author heShaoHua
     * @describe 暂无
     * @param id 自动化规则id
     * @param name 要更新的名称
     * @updateInfo 暂无
     * @date 2019/8/7 17:48
     * @return 是否成功
     */
    Integer updateAutoRuleName(String id, String name);

    /**
     * 查询该列表id是否是自动化规则对象
     * @author heShaoHua
     * @describe 如果menuId为空返回-1 ， 如果projectId为空则继续进行查询，如果两者全部为空 则返回-1
     * @param menuId 列表id
     * @param projectId 项目id
     * @updateInfo 暂无
     * @date 2019/8/20 10:21
     * @return 如果是 返回 1 ，否则返回0
     */
    Integer checkTaskMenuObjectCount(String menuId, String projectId);

    /**
     * 根据menuId和projectId查询出该menuId下最新创建的自动化规则对象
     * @author heShaoHua
     * @describe 如果menuId为空返回null ， 如果projectId为空则继续进行查询，如果两者全部为空 则返回null
     * @param menuId 列表id
     * @param projectId 项目id
     * @updateInfo 暂无
     * @date 2019/8/20 10:21
     * @return 最新的自动化规则对象信息
     */
    AutomationRule getNewestRuleByCreateTime(String menuId, String projectId);
}
