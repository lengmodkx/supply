package com.art1001.supply.service.demand.impl;

import com.art1001.supply.entity.demand.Demand;
import com.art1001.supply.entity.demand.DemandBid;
import com.art1001.supply.mapper.demand.DemandBidMapper;
import com.art1001.supply.mapper.demand.DemandMapper;
import com.art1001.supply.service.demand.DemandBidService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName DemandBidServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/1/22 11:46
 * @Discription
 */
@Service
public class DemandBidServiceImpl extends ServiceImpl<DemandBidMapper, DemandBid> implements DemandBidService {

    @Resource
    private DemandBidMapper demandBidMapper;

    @Resource
    private DemandMapper demandMapper;

    @Override
    public void joinBidding(String demandId, String orgId, String details, List<String> detailImages, BigDecimal bid) {
        String userId = ShiroAuthenticationManager.getUserId();
        Integer count = demandBidMapper.selectCount(new QueryWrapper<DemandBid>().eq("member_id", userId).eq("demand_id", demandId).eq("is_del", 0));

        if (count==0) {
            DemandBid demandBid = DemandBid.builder().demandId(demandId).organizationId(orgId).details(details).bid(bid)
                    .createTime(System.currentTimeMillis()).updateTime(System.currentTimeMillis()).memberId(userId)
                    .build();
            if (CollectionUtils.isNotEmpty(detailImages)) {
                demandBid.setDetailsImages(detailImages);
            }
            save(demandBid);
            Demand demand = new Demand();
            demand.setDemandId(demandId);
            demand.setDemandState(1);
            demand.setUpdateTime(System.currentTimeMillis());
            demandMapper.updateById(demand);
        }

    }

    @Override
    public String successBidding(String demandId, String id) {
        String userId = ShiroAuthenticationManager.getUserId();
        Demand demand = demandMapper.selectOne(new QueryWrapper<Demand>().eq("member_id", userId).eq("is_del", 0));
        if (demand!=null) {
            if (demand.getMemberId().equals(userId)) {
                demand.setUpdateTime(System.currentTimeMillis());
                demand.setDemandState(1);
                DemandBid demandBid = getById(id);
                demandBid.setState(1);
                demandBid.setUpdateTime(System.currentTimeMillis());
                demandMapper.updateById(demand);
                updateById(demandBid);
                // todo 准备生成订单
                return "已达成合作，请准备签约合同";
            }
            return "系统错误，请稍后再试";
        }
        return "系统错误，请稍后再试";
    }
}
