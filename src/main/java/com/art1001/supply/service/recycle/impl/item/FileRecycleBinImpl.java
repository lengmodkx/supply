package com.art1001.supply.service.recycle.impl.item;

import com.art1001.supply.api.request.RecycleBinParamDTO;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.recycle.AbstractRecycleBin;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author heshaohua
 * @version 1.0.0
 * @date 2020年03月12日 13:26:00
 */
@Slf4j
@Service
public class FileRecycleBinImpl extends AbstractRecycleBin {

    @Resource
    private FileService fileService;

    @Override
    public void moveToRecycleBin(RecycleBinParamDTO recycleBinParamDTO) {
        List<File> files = recycleBinAssembler.recycleBinParamTransFormFileList(recycleBinParamDTO);
        fileService.updateBatchById(files);
    }

    @Override
    public void recoveryItem(RecycleBinParamDTO recycleBinParamDTO) {

        File file = recycleBinAssembler.recycleBinParamTransFormFile(recycleBinParamDTO);
        file.setFileDel(0);

        //恢复文件只能恢复到项目的根目录，需要查询出项目根目录的id
        File fileTier = fileService.findFileTier(recycleBinParamDTO.getProjectId());
        if(fileTier != null && StringUtils.isNotEmpty(fileTier.getFileId())){

            file.setParentId(fileTier.getFileId());
            fileService.updateById(file);
            this.saveLog(recycleBinParamDTO.getPublicId());
        } else {
            throw new ServiceException("项目根目录不存在，无法恢复!");
        }
    }

    @Override
    public void saveLog(String publicId) {

        if(StringUtils.isEmpty(publicId)){
            log.error("恢复文件成功后，文件id为空，无法保存操作日志。 [{}]", publicId);
            return;
        }

        logService.saveLog(publicId, TaskLogFunction.A28.getName(),2);
    }
}
