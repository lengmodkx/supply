package com.art1001.supply.service.recycle;

import com.art1001.supply.api.request.RecycleBinParamDTO;

/**
 * @author heshaohua
 */
public abstract class AbstractRecycleBin {

    /**
     * 移入回收站/恢复信息
     * @param recycleBinParamDTO 封装后的参数
     */
    public abstract void recycleBin(RecycleBinParamDTO recycleBinParamDTO);

}

