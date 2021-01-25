package com.art1001.supply.service.demand;

import com.art1001.supply.entity.demand.DemandBid;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

public interface DemandBidService extends IService<DemandBid> {
    /**
     * 参与竞标
     * @param demandId          需求id
     * @param orgId             竞标企业id
     * @param details           服务详情
     * @param detailImages      焦点主图
     * @param bid               出价
     * @return
     */
    void joinBidding(String demandId, String orgId, String details, List<String> detailImages, BigDecimal bid);

    /**
     * 竞标成功
     * @param demandId     需求id
     * @param id           竞标id
     * @return
     */
    String successBidding(String demandId, String id);
}
