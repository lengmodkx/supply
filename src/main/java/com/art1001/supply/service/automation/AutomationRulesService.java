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
}
