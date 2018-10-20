package com.art1001.supply.service.system.impl;

import com.art1001.supply.entity.system.SystemLog;
import com.art1001.supply.mapper.system.SystemLogMapper;
import com.art1001.supply.service.system.SystemLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author heshaohua
 * @since 2018-10-19
 */
@Service
public class SystemLogServiceImpl implements SystemLogService {

    @Resource
    SystemLogMapper systemLogMapper;

    @Override
    public int save(SystemLog sl) {
        return systemLogMapper.save(sl);
    }
}
