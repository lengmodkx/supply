package com.art1001.supply.service.file.impl;

import com.alibaba.fastjson.JSON;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.service.file.FileEsService;
import com.google.common.collect.Lists;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName FileEsServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/8/26 17:14
 * @Discription
 */
@Service
public class FileEsServiceImpl implements FileEsService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Override
    public List<File> searchEsFile(String fileName, Integer pageNumber,Integer pageSize) {
        SearchRequest searchRequest = new SearchRequest("file");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

//        sourceBuilder.from(pageNumber);
//        sourceBuilder.size(pageSize);
        sourceBuilder.query(QueryBuilders.multiMatchQuery(fileName,"fileName","ext"));

        searchRequest.source(sourceBuilder);
        searchRequest.scroll(TimeValue.timeValueMinutes(1L));
        List<File>files= Lists.newArrayList();

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
            String scrollId = response.getScrollId();
            Arrays.asList(response.getHits().getHits()).forEach(r->{
                File file = JSON.parseObject(r.getSourceAsString(), File.class);
                files.add(file);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;

    }

    @Override
    public long getSucaiTotle(String fileName)  {
        SearchRequest searchRequest = new SearchRequest("file");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.multiMatchQuery(fileName,"fileName","ext"));
        searchRequest.source(sourceBuilder);
        long totalHits=0;
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest);
             totalHits = response.getHits().getTotalHits();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return totalHits;
    }
}
