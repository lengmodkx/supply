package com.art1001.supply.util;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.elasticsearch.common.unit.TimeValue.timeValueMillis;

/**
 * @ClassName EsUtil
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/1/7 14:07
 * @Discription es工具类
 */
@Slf4j
@Component
public class EsUtil<T> {

    @Resource
    private RestHighLevelClient esClient;

    /**
     * 保存数据
     *
     * @param indexName 名称
     * @param type      类型
     * @param object    对象
     * @param indices   对象id，用于验证
     */
    public void save(String indexName, String type, Object object, String indices) {
        try {

            SearchRequest request = new SearchRequest();
            SearchSourceBuilder builder = new SearchSourceBuilder();
            Map<String, String> fileMap = BeanUtils.describe(object);
            builder.query(QueryBuilders.matchQuery(indices, fileMap.get(indices)));
            request.source(builder);
            SearchResponse search = esClient.search(request, RequestOptions.DEFAULT);
            if (search.getHits().getTotalHits() == 0) {
                IndexRequest indexRequest = new IndexRequest(indexName, type);
                indexRequest.source(fileMap);
                esClient.index(indexRequest, RequestOptions.DEFAULT);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 修改数据
     *
     * @param indexName 名称
     * @param type      类型
     * @param key       修改的列
     * @param value     修改的条件
     * @param object    修改的数据
     */
    public void update(String indexName, String type, String key, String value, Object object) {
        SearchResponse search = searchListById(key, value);
        Arrays.asList(search.getHits().getHits()).forEach(r -> {
            try {
                UpdateRequest updateRequest = new UpdateRequest(indexName, type, r.getId());
                Map<String, String> describe = BeanUtils.describe(object);
                updateRequest.doc(describe);
                esClient.update(updateRequest, RequestOptions.DEFAULT);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 删除数据
     *
     * @param indexName 名称
     * @param type      类型
     * @param key       删除的列
     * @param value     删除的条件
     */
    public void delete(String indexName, String type, String key, String value) {
        SearchResponse search = searchListById(key, value);
        Arrays.asList(search.getHits().getHits()).forEach(r -> {
            try {
                DeleteRequest deleteRequest = new DeleteRequest(indexName, type, r.getId());
                esClient.delete(deleteRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 查询某一个对象
     *
     * @param clazz
     * @param sourceBuilder
     * @param indices
     * @return
     */
    public T search(Class<T> clazz, SearchSourceBuilder sourceBuilder, String indices) {

        try {
            SearchRequest searchRequest = new SearchRequest(indices);
            searchRequest.source(sourceBuilder);
            SearchResponse search = esClient.search(searchRequest, RequestOptions.DEFAULT);
            if (search.getHits().getHits().length != 0) {
                for (SearchHit hit : search.getHits().getHits()) {
                    String sourceAsString = hit.getSourceAsString();
                    return JSONObject.parseObject(sourceAsString, clazz);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询列表
     *
     * @param clazz
     * @param sourceBuilder
     * @param indices
     * @return
     */
    public List<T> searchList(Class<T> clazz, SearchSourceBuilder sourceBuilder, String indices) {
        List<T> list = Lists.newArrayList();
        try {
            SearchRequest searchRequest = new SearchRequest(indices);
            searchRequest.source(sourceBuilder);
            SearchResponse search = esClient.search(searchRequest, RequestOptions.DEFAULT);
            if (search.getHits().getTotalHits() != 0) {
                for (SearchHit hit : search.getHits().getHits()) {
                    list.add(JSONObject.parseObject(hit.getSourceAsString(), clazz));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 查询列表
     *
     * @param clazz
     * @param sourceBuilder
     * @param indices
     * @return
     */
    public Page<T> searchListByPage(Class<T> clazz, SearchSourceBuilder sourceBuilder, String indices,Integer pageNum) {
        Page<T> page = new Page<>();
        page.setCurrent(pageNum);
        SearchResponse searchResponse;
        SearchHit[] hits;
        String scrollId;
        try {
            sourceBuilder.size(20);
            SearchRequest searchRequest=new SearchRequest(indices);
            Scroll scroll = new Scroll(timeValueMillis(10L));
            searchRequest.scroll(scroll);
            searchRequest.source(sourceBuilder);

            // 发起请求并接收响应
             searchResponse = esClient.search(searchRequest,RequestOptions.DEFAULT);

            // 初始化查询结果List
            List<String> jsonStringList=Lists.newArrayList();

            // 获取ScrollId
            scrollId = searchResponse.getScrollId();

            // 设置总数
            page.setTotal(searchResponse.getHits().getTotalHits());


            int ceil = (int) Math.ceil((float) page.getTotal() / 20);
            if (searchResponse.getHits().getHits().length!=0) {
                for (int i = 1; i <= ceil; i++) {
                    if (pageNum.equals(i)) {
                        Arrays.stream(searchResponse.getHits().getHits()).forEach(v->jsonStringList.add(v.getSourceAsString()));
                        page.setSize(jsonStringList.size());
                        page.setRecords(hitsToObject(jsonStringList,clazz));
                    }else {
                        //获取scroll_id并再次查询
                        scrollId = searchResponse.getScrollId();
                        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                        scrollRequest.scroll(TimeValue.timeValueSeconds(3000));
                        searchResponse = esClient.scroll(scrollRequest,RequestOptions.DEFAULT);
                    }
                }

            }

            //及时清除es快照，释放资源
            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollId);
            esClient.clearScroll(clearScrollRequest,RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return page;
    }

    private List<T> hitsToObject(List<String> jsonStringList,Class<T> clazz) {
        List<T>list=Lists.newArrayList();
        Optional.ofNullable(jsonStringList).ifPresent(l-> l.forEach(r->
                list.add(JSONObject.parseObject(r,clazz))
        ));
        return list;
    }


    /**
     * 根据id查询列表
     *
     * @param value
     * @param key
     * @return
     * @throws IOException
     */
    private SearchResponse searchListById(String key, String value) {
        SearchResponse search = new SearchResponse();
        try {
            SearchRequest searchRequest = new SearchRequest();
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.multiMatchQuery(value, key));
            searchRequest.source(sourceBuilder);
            search = esClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return search;
    }


}
