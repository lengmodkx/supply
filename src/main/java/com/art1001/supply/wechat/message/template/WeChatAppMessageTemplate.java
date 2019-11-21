package com.art1001.supply.wechat.message.template;

import lombok.Data;

import java.util.Map;

/**
 * @author heshaohua
 * @date 2019/11/7 15:04
 **/
@Data
@SuppressWarnings("all")
public class WeChatAppMessageTemplate {

    private String template_id;

    private String page;

    private String form_id;

    private Map<String,TemplateData> data;

    /**
     * 放大关键字
     */
    private String emphasis_keyword;

}
