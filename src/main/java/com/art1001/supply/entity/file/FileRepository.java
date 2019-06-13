package com.art1001.supply.entity.file;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Auther: yanglujing
 * @Date: 2019/6/12 16:17
 * @Description:
 */
public interface FileRepository extends ElasticsearchRepository<File,String> {

}
