package com.art1001.supply.service.apiBean;

/**
 * @author heshaohua
 * @Description: 用于更新 表 字段上的json数据
 * @date 2018/9/25 16:17
 */
public interface ApiBean {


    /**
     * 更新json数据内容
     * @param id 要更新的内容id
     * @param userImage 用户头像更新
     * @param name 信息名称更新
     */
    void updateJSON(String id, String userImage, String name, String type);
}
