package com.art1001.supply.application.assembler;

import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.log.LogSendParam;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import org.springframework.stereotype.Component;

/**
 * @author shaohua
 * @date 2020/3/6 15:38
 */
@Component
public class LogAssembler {

    public Log logSendParamTransFormLog(LogSendParam param){
        Log log = param;
        log.setCreateTime(System.currentTimeMillis());
        log.setLogType(1);
        log.setCreateTime(System.currentTimeMillis());
        log.setMemberId(ShiroAuthenticationManager.getUserId());
        return log;
    }
}
