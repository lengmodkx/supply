package com.art1001.supply.param.check.recyclebin;

import com.art1001.supply.api.request.RecycleBinParamDTO;

/**
 * @author shaohua
 * @date 2020/3/12 22:46
 */
@FunctionalInterface
public interface RecycleBinParamCheck {

    /**
     * 参数校验
     * @param recycleBinParamDTO 分装好的参数对象
     */
    void checkParam(RecycleBinParamDTO recycleBinParamDTO);
}
