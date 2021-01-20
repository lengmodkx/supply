package com.art1001.supply.service.content.impl;

import com.art1001.supply.entity.content.Topic;
import com.art1001.supply.mapper.content.TopicMapper;
import com.art1001.supply.service.content.TopicService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @ClassName TopicServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/1/12 11:30
 * @Discription
 */
@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {
}
