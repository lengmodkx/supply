/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author 邓凯欣 dengkaixin@art1001.com
 * @create 2021/1/1
 * @since 1.0.0
 */
package com.art1001.supply.service.fans.impl;

import com.art1001.supply.entity.fans.Fans;
import com.art1001.supply.mapper.fans.FansMapper;
import com.art1001.supply.service.fans.FansService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class FansServiceImpl extends ServiceImpl<FansMapper, Fans> implements FansService {
}
