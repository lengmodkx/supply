package com.art1001.supply.service.recycle;

import com.art1001.supply.api.request.RecycleBinParamDTO;
import com.art1001.supply.application.assembler.recyclebin.RecycleBinParamDTOAssembler;
import com.art1001.supply.service.log.LogService;

import javax.annotation.Resource;

/**
 * @author heshaohua
 */
public abstract class AbstractRecycleBin {

    @Resource
    public LogService logService;

    @Resource
    public RecycleBinParamDTOAssembler recycleBinAssembler;

    /**
     * 移入回收站
     * @param recycleBinParamDTO 封装后的参数
     */
    public abstract void moveToRecycleBin(RecycleBinParamDTO recycleBinParamDTO);

    /**
     * 在回收站中恢复信息项
     * @param recycleBinParamDTO 封装后的参数
     */
    public abstract void recoveryItem(RecycleBinParamDTO recycleBinParamDTO);

    /**
     * 恢复信息完成后保存操作日志
     * @param publicId 需要绑定的信息id
     */
    public abstract void saveLog(String publicId);

}

