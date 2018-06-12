package com.art1001.supply.entity.share;

import lombok.Data;

/**
 * @author heshaohua
 * @Title: Share
 * @Description: 分享实体类
 * @date 2018/6/12 14:34
 **/
@Data
public class Share {

    /**
     * id
     */
    private String id;

    /**
     * 分享标题
     */
    private String title;

    /**
     * 分享内容
     */
    private String text;
}
