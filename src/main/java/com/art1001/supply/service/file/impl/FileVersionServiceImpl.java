package com.art1001.supply.service.file.impl;

import com.art1001.supply.entity.file.FileVersion;
import com.art1001.supply.mapper.file.FileVersionMapper;
import com.art1001.supply.service.file.FileVersionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * fileServiceImpl
 */
@Service
public class FileVersionServiceImpl extends ServiceImpl<FileVersionMapper,FileVersion> implements FileVersionService {

}