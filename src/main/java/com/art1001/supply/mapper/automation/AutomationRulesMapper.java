package com.art1001.supply.mapper.automation;

import com.art1001.supply.entity.automation.AutomationRule;
import com.art1001.supply.entity.automation.dto.AutomationRuleDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author heshaohua
 * @since 2019-08-07
 */
@Mapper
public interface AutomationRulesMapper extends BaseMapper<AutomationRule> {

    /**
     * 根据id获取自动化规则信息
     * @param id 自动化规则id
     * @return 自动化规则数据
     */
    AutomationRuleDTO selectRuleById(@Param("id") String id);

    /**
     * 根据menuId和projectId查询出该menuId下最新创建的自动化规则对象
     * @param menuId 列表id
     * @param projectId 项目id
     * @return 最早一条自动化规则信息
     */
    AutomationRule selectNewestRuleByCreateTime(@Param("menuId") String menuId, @Param("projectId") String projectId);
}
