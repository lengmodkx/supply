package com.art1001.supply.service.demand;

import com.art1001.supply.entity.demand.DemandClass;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DemandClassService extends IService<DemandClass> {

    /**
     * 需求分类列表
     * @return
     */
    List<DemandClass> demandClassList();

    /**
     * 新增分类
     * @param dcName
     * @param parentId
     */
    void add(String dcName, String parentId);

    /**
     * 修改分类
     * @param dcId
     * @param dcName
     * @param parentId
     */
    void edit(String dcId, String dcName, String parentId);
}
