package com.art1001.supply.service.demand.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.demand.Demand;
import com.art1001.supply.mapper.demand.DemandMapper;
import com.art1001.supply.service.demand.DemandService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.CommonUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName DemandServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/12/29 14:23
 * @Discription
 */
@Service
public class DemandServiceImpl extends ServiceImpl<DemandMapper, Demand> implements DemandService {

    @Override
    public void add(String demandName, String dcId, String demandDetails, List<String> demandFiles, BigDecimal demandBudget, Integer solveWay, String memberTel) {
        Demand demand = new Demand();
        demand.setDemandName(demandName);
        demand.setDcId(dcId);
        demand.setDemandDetails(demandDetails);
        if (CollectionUtils.isNotEmpty(demandFiles)) {
            demand.setDemandFiles(CommonUtils.listToString(demandFiles));
        }
        demand.setDemandBudget(demandBudget);
        demand.setSolveWay(solveWay);
        demand.setMemberId(ShiroAuthenticationManager.getUserId());
        demand.setMemberTel(memberTel);
        demand.setIsDel(0);
        demand.setCreateTime(System.currentTimeMillis());
        demand.setUpdateTime(System.currentTimeMillis());
        save(demand);
    }

    @Override
    public void edit(String demandId, String demandName, String dcId, String demandDetails, List<String> demandFiles, BigDecimal demandBudget, String memberTel, Integer isExpedited, Integer isTop) {
        Demand demand = new Demand();
        demand.setDemandId(demandId);
        demand.setDemandName(demandName);
        demand.setDcId(dcId);
        demand.setDemandDetails(demandDetails);
        if (CollectionUtils.isNotEmpty(demandFiles) && demandFiles != null) {
            demand.setDemandFiles(CommonUtils.listToString(demandFiles));
        }
        demand.setDemandBudget(demandBudget);
        demand.setMemberTel(memberTel);
        demand.setIsExpedited(isExpedited);
        demand.setIsTop(isTop);
        demand.setUpdateTime(System.currentTimeMillis());
        updateById(demand);
    }

    @Override
    public IPage<Demand> getList(Integer pageNum, Integer pageSize, String dcId,Integer type) {
        Page<Demand> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Demand> query = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(dcId)) {
            query.eq("dc_id",dcId).eq("is_del",0);
        }
        query.eq("is_del",0);
        IPage<Demand> demandIpage = page(page, query);
        List<Demand> collect= Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(demandIpage.getRecords())) {

            if (type.equals(Constants.B_ONE)) {
                collect= demandIpage.getRecords().stream()
                        .filter(r->!r.getMemberId().equals(ShiroAuthenticationManager.getUserId()))
                        .sorted(Comparator.comparing(Demand::getIsExpedited).reversed().thenComparing(Demand::getIsTop))
                        .collect(Collectors.toList());

            }
            if (type.equals(Constants.B_TWO)) {
                collect= demandIpage.getRecords().stream()
                        .filter(r->r.getMemberId().equals(ShiroAuthenticationManager.getUserId()))
                        .sorted(Comparator.comparing(Demand::getIsExpedited).reversed().thenComparing(Demand::getIsTop))
                        .collect(Collectors.toList());
            }
            demandIpage.setRecords(collect);
        }
        return demandIpage;
    }

    @Override
    public void checkDemand(String demandId,Integer isCheck, String reason) {
        UpdateWrapper<Demand> query = new UpdateWrapper<>();
        query.set("demand_state",2).set("is_check",isCheck).set("update_time",System.currentTimeMillis());
        if (StringUtils.isNotEmpty(reason)) {
            query.set("check_fail_reason",reason);
        }
        query.eq("demand_id",demandId);
        update(query);
    }


}
