package com.art1001.supply.wechat.message.context;

import com.art1001.supply.wechat.message.template.WeChatAppMessageTemplate;

/**
 * @author heshaohua
 * @date 2019/12/5 10:32
 **/
public class WeChatAppMessageTemplateBuild {

    public static WeChatAppMessageTemplate createTask(){
        WeChatAppMessageTemplate weChatAppMessageTemplate = new WeChatAppMessageTemplate();
        //模板id
        weChatAppMessageTemplate.setTemplate_id("5jlQyk_m4Vt7hSSijlwcsmXbLrWFxWiuqvTZJ9jk14k");
        //点击卡片跳转的路径
        weChatAppMessageTemplate.setPage("/pages/task/task-info");
        //放大的字体
        weChatAppMessageTemplate.setEmphasis_keyword("用户名");

        return weChatAppMessageTemplate;
    }
}
