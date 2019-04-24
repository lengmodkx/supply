package com.art1001.supply.entity.statistics;

/**
 * @Auther: yanglujing
 * @Date: 2019/4/20 16:36
 * @Description:  统计页面表格工具类
 */
public class TitleVO {

    private  String  title;

    private  String  key;

    public TitleVO(String title, String key) {
        this.title = title;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
