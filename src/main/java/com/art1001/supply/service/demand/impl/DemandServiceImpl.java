package com.art1001.supply.service.demand.impl;

import com.art1001.supply.entity.demand.Demand;
import com.art1001.supply.entity.demand.DemandBid;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.demand.DemandBidMapper;
import com.art1001.supply.mapper.demand.DemandMapper;
import com.art1001.supply.mapper.organization.OrganizationMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.demand.DemandService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.CommonUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName DemandServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/12/29 14:23
 * @Discription
 */
@Service
public class DemandServiceImpl extends ServiceImpl<DemandMapper, Demand> implements DemandService {

    @Resource
    private DemandBidMapper demandBidMapper;

    @Resource
    private DemandMapper demandMapper;

    @Resource
    private OrganizationMapper organizationMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public void add(String demandName, String demandDetails,String orgId, List<String> demandFiles, BigDecimal bid) {
        Demand demand = new Demand();
        demand.setDemandName(demandName);
        demand.setDemandDetails(demandDetails);
        demand.setOrgId(orgId);
        if (CollectionUtils.isNotEmpty(demandFiles)) {
            demand.setDemandFiles(CommonUtils.listToString(demandFiles));
        }
        demand.setBid(bid);
        demand.setMemberId(ShiroAuthenticationManager.getUserId());
        demand.setIsCheck(0);
        demand.setIsDel(0);
        demand.setCreateTime(System.currentTimeMillis());
        demand.setUpdateTime(System.currentTimeMillis());
        save(demand);
    }

    @Override
    public void edit(String demandId, String demandName, String demandDetails, List<String> demandFiles, BigDecimal bid) {
        Demand demand = new Demand();
        demand.setDemandId(demandId);
        if (StringUtils.isNotEmpty(demandName)) {
            demand.setDemandName(demandName);
        }
        if (StringUtils.isNotEmpty(demandDetails)) {
            demand.setDemandDetails(demandDetails);
        }
        if (CollectionUtils.isNotEmpty(demandFiles)) {
            demand.setDemandFiles(CommonUtils.listToString(demandFiles));
        }
        if (bid != null) {
            demand.setBid(bid);
        }
        demand.setIsCheck(0);
        demand.setUpdateTime(System.currentTimeMillis());
        updateById(demand);
    }

    /**
     * 需求列表
     * @param pageNum      页数
     * @param pageSize     条数
     * @param type         1我发布的 2我竞标的 3我中标的 4所有需求
     * @param state        1 审核过的 2未审核的
     * @return  Page<Demand>
     */
    @Override
    public IPage<Demand> getList(Integer pageNum, Integer pageSize, Integer type,Integer state) {
        Page<Demand> page = new Page<>(pageNum, pageSize);
        String userId = ShiroAuthenticationManager.getUserId();
        List<String> demandIds;
        // 审核过的
        if (state.equals(ONE)) {
            switch (type) {
                // 1我发布的
                case 1:
                    page = page(page, new QueryWrapper<Demand>().eq("is_del", 0).eq("is_check",1).eq("member_id", userId));
                    break;
                // 2我竞标的
                case 2:
                    demandIds = demandBidMapper.selectList(new QueryWrapper<DemandBid>().eq("member_id", userId)
                            .eq("is_del", 0).eq("state", 0)).stream().map(DemandBid::getDemandId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(demandIds)) {
                        page = demandMapper.selectPage(page, new QueryWrapper<Demand>().eq("is_del", 0).eq("is_check",1).in("demand_id", demandIds));
                    }
                    break;
                // 3我中标的
                case 3:
                    demandIds = demandBidMapper.selectList(new QueryWrapper<DemandBid>().eq("member_id", userId)
                            .eq("is_del", 0).eq("state", 1)).stream().map(DemandBid::getDemandId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(demandIds)) {
                        page = demandMapper.selectPage(page, new QueryWrapper<Demand>().eq("is_del", 0).in("demand_id", demandIds).eq("is_check",1));
                    }
                    break;
                case 4:
                    page = page(page, new QueryWrapper<Demand>().eq("is_del", 0).eq("is_check",1));
                    if (CollectionUtils.isNotEmpty(page.getRecords())) {
                        page.setRecords(page.getRecords().stream().filter(r->r.getDemandState()!=3).collect(Collectors.toList()));
                    }
                default:
                    break;
            }
            if (CollectionUtils.isNotEmpty(page.getRecords())) {
                List<Demand> collect = page.getRecords().stream().sorted(Comparator.comparingLong(Demand::getCreateTime).reversed()).collect(Collectors.toList());
                collect.forEach(r->{
                    UserEntity userEntity = userMapper.selectById(r.getMemberId());
                    r.setMemberImg(userEntity.getImage());
                    r.setMemberName(userEntity.getUserName());
                });
                page.setRecords(collect);
            }
        }else{
            page=demandMapper.selectPage(page,new QueryWrapper<Demand>().eq("is_del",0).eq("is_check",0));
        }
        return page;
    }

    @Override
    public void checkDemand(String demandId, Integer isCheck, String reason) {
        UpdateWrapper<Demand> query = new UpdateWrapper<>();
        query.set("is_check", isCheck).set("update_time", System.currentTimeMillis());
        if (isCheck.equals(ZERO)) {
            if (StringUtils.isNotEmpty(reason)) {
                query.set("check_fail_reason", reason);
            }
        }
        query.eq("demand_id", demandId);
        update(query);
    }

    @Override
    public Demand demandInfo(String demandId) {
        String userId = ShiroAuthenticationManager.getUserId();
        Demand demand = getById(demandId);

        if (userId.equals(demand.getMemberId())) {
            List<DemandBid> demandBids = demandBidMapper.selectList(new QueryWrapper<DemandBid>().eq("demand_id", demandId).eq("is_del", 0));
            Optional.ofNullable(demandBids).ifPresent(bids -> {
                bids.forEach(this::setOrgAndMemberInfo);
                demand.setBidList(bids);
            });
        } else {
            DemandBid demandBid = demandBidMapper.selectOne(new QueryWrapper<DemandBid>().eq("demand_id", demandId).eq("is_del", 0).eq("member_id", userId));
            if (demandBid != null) {
                setOrgAndMemberInfo(demandBid);
                demand.setBidList(Collections.singletonList(demandBid));
            }
        }
        return demand;
    }

    /**
     * 设置企业名企业头像及用户名及用户头像
     * @param demandBid
     */
    private void setOrgAndMemberInfo(DemandBid demandBid) {
        Organization organization = organizationMapper.selectById(demandBid.getOrganizationId());
        UserEntity userEntity = userMapper.selectById(demandBid.getMemberId());
        demandBid.setOrganizationName(organization.getOrganizationName());
        demandBid.setOrganizationImage(organization.getOrganizationImage());
        demandBid.setMemberName(userEntity.getUserName());
        demandBid.setMemberImage(userEntity.getImage());
        demandBid.setOrganizationPhone(userEntity.getAccountName());
    }


}
