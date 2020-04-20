package com.art1001.supply.service.file.impl;

import com.art1001.supply.entity.file.Material;
import com.art1001.supply.mapper.file.MaterialMapper;
import com.art1001.supply.service.file.MaterialService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements MaterialService {
}
