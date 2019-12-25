package com.art1001.supply.service.automation.impl;

import com.art1001.supply.entity.automation.AutomationRule;
import com.art1001.supply.entity.automation.dto.AutomationRuleDTO;
import com.art1001.supply.mapper.automation.AutomationRulesMapper;
import com.art1001.supply.service.automation.AutomationRulesService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.Stringer;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author heshaohua
 * @since 2019-08-07
 */
@Service
public class AutomationRulesServiceImpl extends ServiceImpl<AutomationRulesMapper, AutomationRule> implements AutomationRulesService {

    /**
     * 注入自动化规则dao层Bean
     */
    @Resource
    AutomationRulesMapper automationRulesMapper;

    @Override
    public int saveAutomationRule(AutomationRule automationRule) {
        automationRule.setCreateTime(System.currentTimeMillis());
        automationRule.setUpdateTime(System.currentTimeMillis());
        automationRule.setCreateUser(ShiroAuthenticationManager.getUserId());
        return this.saveOrUpdate(automationRule) ? 1:0;
    }

    @Override
    public AutomationRuleDTO getRuleById(String id) {
        return automationRulesMapper.selectRuleById(id);
    }

    @Override
    public List<AutomationRule> getAutoRuleListByProject(String projectId) {
        //构造出根据项目id查询自动化规则列表的sql表达式
        LambdaQueryWrapper<AutomationRule> selectAutoListByProQw = new QueryWrapper<AutomationRule>().lambda()
                .eq(AutomationRule::getProjectId, projectId)
                .select(AutomationRule::getId, AutomationRule::getName)
                .orderByDesc(AutomationRule::getCreateTime);
        return this.list(selectAutoListByProQw);
    }

    @Override
    public Integer updateAutoRuleName(String id, String name) {
        AutomationRule automationRule = new AutomationRule();
        automationRule.setId(id);
        automationRule.setName(name);
        automationRule.setCreateTime(System.currentTimeMillis());
        return updateById(automationRule) ? 1:0;
    }

    @Override
    public Integer checkTaskMenuObjectCount(String menuId, String projectId) {
        //参数校验
        boolean paramIncorrect = (Stringer.isNullOrEmpty(menuId) && Stringer.isNullOrEmpty(projectId))
                || Stringer.isNullOrEmpty(menuId);
        if(paramIncorrect){
            return -1;
        }

        Wrapper<AutomationRule> selectMenuIdIsObjectQw = createMenuObjectQueryWrapper(menuId, projectId);
        //返回该menuId的自动化规则的条数
        return automationRulesMapper.selectCount(selectMenuIdIsObjectQw);
    }

    @Override
    public AutomationRule getNewestRuleByCreateTime(String menuId, String projectId) {
        //参数校验
        boolean paramIncorrect = (Stringer.isNullOrEmpty(menuId) && Stringer.isNullOrEmpty(projectId))
                || Stringer.isNullOrEmpty(menuId);
        if(paramIncorrect){
            return null;
        }
        return automationRulesMapper.selectNewestRuleByCreateTime(menuId,projectId);
    }

    /**
     * 构建查询sql表达式（仅供 menuId 和 projectId 查询条件的sql表达式使用）
     * @param menuId 列表id
     * @param projectId  项目id
     * @return sql表达式
     */
    private Wrapper<AutomationRule> createMenuObjectQueryWrapper(String menuId, String projectId){
        //构造出sql表达式,如果projectId不为空就在sql中加上projectId筛选条件。
        LambdaQueryWrapper<AutomationRule> selectMenuIdIsObjectQw = new QueryWrapper<AutomationRule>().lambda();
        if(Stringer.isNotNullOrEmpty(projectId)){
            selectMenuIdIsObjectQw.eq(AutomationRule::getProjectId, projectId);
        }
        selectMenuIdIsObjectQw.eq(AutomationRule::getObject, menuId);
        return selectMenuIdIsObjectQw;
    }
}
