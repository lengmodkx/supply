package com.art1001.supply.service.recycle.impl.item;

import com.art1001.supply.api.request.RecycleBinParamDTO;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.service.recycle.AbstractRecycleBin;
import com.art1001.supply.service.share.ShareService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author heshaohua
 * @version 1.0.0
 * @date 2020年03月12日 14:17:00
 */
@Slf4j
@Service
public class ShareRecycleBinImp extends AbstractRecycleBin {

    @Resource
    private ShareService shareService;

    @Override
    public void moveToRecycleBin(RecycleBinParamDTO recycleBinParamDTO) {
        Share share = recycleBinAssembler.recycleBinParamTransFormShare(recycleBinParamDTO);
        share.setIsDel(1);
        shareService.updateById(share);
    }

    @Override
    public void recoveryItem(RecycleBinParamDTO recycleBinParamDTO) {
        Share share = recycleBinAssembler.recycleBinParamTransFormShare(recycleBinParamDTO);
        share.setIsDel(0);
        shareService.updateById(share);
    }

    @Override
    public void saveLog(String publicId) {
        if(StringUtils.isEmpty(publicId)){
            log.error("恢复分享成功后，分享id为空，无法执行日志操作！ [{}]", publicId);
        }
        logService.saveLog(publicId, TaskLogFunction.A27.getName(),1);
    }
}
