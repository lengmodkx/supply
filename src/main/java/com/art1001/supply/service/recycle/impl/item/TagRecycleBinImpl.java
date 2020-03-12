package com.art1001.supply.service.recycle.impl.item;

import com.art1001.supply.api.request.RecycleBinParamDTO;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.service.recycle.AbstractRecycleBin;
import com.art1001.supply.service.tag.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author heshaohua
 * @version 1.0.0
 * @date 2020年03月12日 14:38:00
 */
@Slf4j
@Service
public class TagRecycleBinImpl extends AbstractRecycleBin {

    @Resource
    private TagService tagService;

    @Override
    public void moveToRecycleBin(RecycleBinParamDTO recycleBinParamDTO) {
        Tag tag = recycleBinAssembler.recycleBinParamTransFormTag(recycleBinParamDTO);
        tag.setIsDel(1);
        tagService.updateById(tag);
    }

    @Override
    public void recoveryItem(RecycleBinParamDTO recycleBinParamDTO) {
        Tag tag = recycleBinAssembler.recycleBinParamTransFormTag(recycleBinParamDTO);
        tag.setIsDel(0);
        tagService.updateById(tag);
    }

    @Override
    public void saveLog(String publicId) {

    }
}
