package com.art1001.supply.wechat.message.template;

import lombok.Data;

/**
 * @author heshaohua
 * @date 2019/11/7 15:08
 **/
@Data
public class TemplateData {

    /**
     * 值
     */
    private String value;

    public TemplateData(String value) {
        this.value = value;
    }

    public TemplateData() {
    }
}
