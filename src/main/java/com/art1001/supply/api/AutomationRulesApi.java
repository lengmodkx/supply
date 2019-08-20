package com.art1001.supply.api;


import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.entity.automation.AutomationRule;
import com.art1001.supply.entity.automation.check.AutomationRuleParamCheck;
import com.art1001.supply.exception.AutomationRuleParamException;
import com.art1001.supply.service.automation.AutomationRulesService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author heshaohua
 * @since 2019-08-07
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/automation_rules")
public class AutomationRulesApi extends BaseController {

    /**
     * 注入自动化规则逻辑层Bean
     */
    @Resource
    private AutomationRulesService automationRulesService;

    /**
     * 注入automation验证Bean
     */
    @Resource
    private AutomationRuleParamCheck automationRuleCheck;

    /**
     * 创建一个自动化规则
     * @return 是否成功
     */
    @PostMapping
    public JSONObject addAutoRule(@RequestBody AutomationRule automationRule){
        ValidatorUtils.validateEntity(automationRule);
        try {
            //验证参数
            int params = automationRuleCheck.checkAutomationRule(automationRule);
            if(params == 1){
                int result = automationRulesService.saveAutomationRule(automationRule);
                return success(result);
            }
        } catch (AutomationRuleParamException e) {
            log.error(ShiroAuthenticationManager.getUserId() + ":" + e.getMessage(),e);
            return error(e.getMessage());
        }
        return error("创建失败！");
    }


    /**
     * 获取自动化规则
     * @param id 自动化规则id
     * @return 自动化规则信息
     */
    @GetMapping("/id/{id}")
    public JSONObject getRule(@PathVariable String id){
        return success(automationRulesService.getRuleById(id));
    }

    /**
     * 获取项目下的所有自动化规则
     * @return 自动化规则列表
     */
    @GetMapping("/pro/{projectId}")
    public JSONObject getProAutoRule(@PathVariable String projectId){
        return success(automationRulesService.getAutoRuleListByProject(projectId));
    }

    /**
     * 更新名称
     * @param id 自动化规则id
     * @param name 更新的名称
     * @return 是否成功
     */
    @PutMapping("/{id}/name")
    public JSONObject updateAutoRuleName(@PathVariable String id, @RequestParam @NotBlank(message = "修改名称不能为空！") String name){
        return success(automationRulesService.updateAutoRuleName(id,name));
    }

    /**
     * 删除自动化规则
     * @param id 自动化规则id
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public JSONObject updateAutoRuleName(@PathVariable String id){
        return success(automationRulesService.removeById(id) ? 1:0);
    }
}

