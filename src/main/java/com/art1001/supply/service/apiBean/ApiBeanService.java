package com.art1001.supply.service.apiBean;

import org.springframework.stereotype.Service;

/**
 * @author heshaohua
 * @Description: 用于更新 表 字段上的json数据
 * @date 2018/9/25 16:17
 */
public interface ApiBeanService {


    /**
     * 更新json数据内容
     * @param id 要更新的内容id
     */
    void updateJSON(String id, Object obj, String type);
}
