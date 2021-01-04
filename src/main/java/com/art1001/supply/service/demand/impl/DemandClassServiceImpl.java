package com.art1001.supply.service.demand.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.demand.DemandClass;
import com.art1001.supply.mapper.demand.DemandClassMapper;
import com.art1001.supply.service.demand.DemandClassService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName DemandClassServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/12/29 14:24
 * @Discription
 */
@Service
public class DemandClassServiceImpl extends ServiceImpl<DemandClassMapper, DemandClass> implements DemandClassService {

    @Override
    public List<DemandClass> demandClassList() {
        List<DemandClass> all = list(new QueryWrapper<>());
        List<DemandClass> list = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(all)) {
            list = all.stream().filter(f -> f.getParentId().equals(Constants.ZERO)).collect(Collectors.toList());
            list.forEach(r -> {
                r.setList(all.stream().filter(f -> f.getParentId().equals(r.getDcId())).collect(Collectors.toList()));
                r.getList().forEach(s -> s.setList(all.stream().filter(f -> f.getParentId().equals(s.getDcId())).collect(Collectors.toList())));
            });
        }
        return list;

    }

    @Override
    public void add(String dcName, String parentId) {
        DemandClass demandClass = new DemandClass();
        demandClass.setDcName(dcName);
        demandClass.setParentId(parentId);
        demandClass.setCreateTime(System.currentTimeMillis());
        demandClass.setUpdateTime(System.currentTimeMillis());
        save(demandClass);
    }

    @Override
    public void edit(String dcId, String dcName, String parentId) {
        DemandClass demandClass = new DemandClass();
        demandClass.setDcName(dcName);
        demandClass.setParentId(parentId);
        demandClass.setUpdateTime(System.currentTimeMillis());
        updateById(demandClass);
    }
}
