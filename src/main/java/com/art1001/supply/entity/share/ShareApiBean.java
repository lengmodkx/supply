package com.art1001.supply.entity.share;

import lombok.Data;

/**
 * @author heshaohua
 * @Title: ShareApiBean
 * @Description: TODO
 * @date 2018/9/25 11:45
 **/
@Data
public class ShareApiBean {

    /**
     * 分享id
     */
    private String shareId;

    /**
     * 分享名称
     */
    private String shareName;

    /**
     * 分享所在项目名称
     */
    private String projectName;

    /**
     * 分享的标题
     */
    private String title;

    /**
     * 分享的内容
     */
    private String content;

    /**
     * 创建者头像
     */
    private String userImage;
}
