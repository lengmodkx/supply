package com.art1001.supply.service.system;

import com.art1001.supply.entity.system.SystemLog;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author heshaohua
 * @since 2018-10-19
 */
public interface SystemLogService {

    /**
     * 添加日志信息
     * @param sl 系统日志对象
     * @return
     */
     int save(SystemLog sl);
}
