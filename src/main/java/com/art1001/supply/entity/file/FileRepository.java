package com.art1001.supply.entity.file;

//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @Auther: yanglujing
 * @Date: 2019/6/12 16:17
 * @Description:
 */
public interface FileRepository
        /*extends ElasticsearchRepository<File,String>*/ {


    /**
     * 根据文件名称搜索
     * @return
     */
    List<File>  findByFileName(String fileName);

    //List<File>  findByFileNamePage(String fileName, Pageable page);

}
