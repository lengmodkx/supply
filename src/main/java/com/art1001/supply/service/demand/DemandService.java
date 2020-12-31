package com.art1001.supply.service.demand;

import com.art1001.supply.entity.demand.Demand;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

public interface DemandService extends IService<Demand> {
    /**
     * 保存需求
     * @param demandName      需求名称
     * @param dcId            需求分类
     * @param demandDetails   需求详情
     * @param demandFiles     需求附件
     * @param demandBudget    需求预算
     * @param solveWay        解决方式 1招标 2比稿
     * @param memberTel       发布人电话
     * @return
     */
    void add(String demandName, String dcId, String demandDetails, List<String> demandFiles, BigDecimal demandBudget, Integer solveWay, String memberTel);



    /**
     * 修改需求
     * @param demandId      需求id
     * @param demandName    需求名称
     * @param dcId          需求分类id
     * @param demandDetails 需求详情
     * @param demandFiles   需求附件
     * @param demandBudget  需求预算
     * @param memberTel     发布人电话
     * @param isExpedited   是否加急
     * @param isTop         是否置顶
     * @return
     */
    void edit(String demandId, String demandName, String dcId, String demandDetails, List<String> demandFiles, BigDecimal demandBudget, String memberTel, Integer isExpedited, Integer isTop);

    /**
     * 需求列表
     * @param pageNum
     * @param pageSize
     * @param dcId
     * @return
     */
    IPage<Demand> getList(Integer pageNum, Integer pageSize, String dcId);
}
