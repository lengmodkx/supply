package com.art1001.supply.service.file;

import com.art1001.supply.entity.file.File;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface FileEsService {
    /**
     *搜索素材库
     * @param fileName
     * @return
     */
    List<File> searchEsFile(String fileName, Integer pageNumber,Integer pageSize);

    long getSucaiTotle(String fileName) throws IOException;
}
