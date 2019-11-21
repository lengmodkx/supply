package com.art1001.supply.wechat.util;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author heshaohua
 * @date 2019/11/20 15:45
 **/
public class WxMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter{

    public WxMappingJackson2HttpMessageConverter(){
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_PLAIN);
        mediaTypes.add(MediaType.TEXT_HTML);
        setSupportedMediaTypes(mediaTypes);
    }
}
