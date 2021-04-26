package com.art1001.supply.service.demand;

import com.art1001.supply.entity.demand.Demand;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
* @Author: 邓凯欣
* @Email：dengkaixin@art1001.com
* @create: 16:37 2021/1/21
*/
public interface DemandService extends IService<Demand> {

    Integer ZERO=0;
    Integer ONE=1;
    /**
     * 保存需求
     * @param demandName      需求名称
     * @param demandDetails   需求详情
     * @param demandFiles     需求附件
     * @param bid             出价
     * @return
     */
    void add(String demandName, String demandDetails,String orgId, List<String> demandFiles, BigDecimal bid);



    /**
     * 修改需求
     * @param demandId      需求id
     * @param demandName    需求名称
     * @param demandDetails 需求详情
     * @param demandFiles   需求附件
     * @param bid           需求预算
     * @return
     */
    void edit(String demandId, String demandName, String demandDetails, List<String> demandFiles, BigDecimal bid);

    /**
     * 需求列表
     * @param pageNum      页数
     * @param pageSize     条数
     * @param type         1我发布的 2我竞标的 3我中标的 4所有需求
     * @param state        1 审核过的 2未审核的
     * @return  Page<Demand>
     */
    IPage<Demand> getList(Integer pageNum, Integer pageSize,Integer type,Integer state,String userId);

    /**
     * 审核/发布需求
     * @param demandId
     * @param isCheck   审核是否通过 0否1是
     * @param reason    审核失败原因
     * @return
     */
    void checkDemand(String demandId,Integer isCheck, String reason);

    /**
     * 获取需求详情
     * @param demandId  需求id
     * @return Demand
     */
    Demand demandInfo(String demandId);
}
