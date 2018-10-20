package com.art1001.supply.mapper.system;

import com.art1001.supply.entity.system.SystemLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author heshaohua
 * @since 2018-10-19
 */
@Mapper
public interface SystemLogMapper{

    /**
     * 保存系统日志信息
     * @param sl
     * @return
     */
    int save(SystemLog sl);
}
