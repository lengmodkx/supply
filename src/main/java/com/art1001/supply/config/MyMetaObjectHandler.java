package com.art1001.supply.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * @Description mybatis-plus 自动填充插件
 * @Date:2019/3/19 18:25
 * @Author heshaohua
 **/
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Object createTime = metaObject.getValue("createTime");
        if(createTime == null){
            setFieldValByName("createTime", System.currentTimeMillis(),metaObject);
        }

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object updateTime = metaObject.getValue("updateTime");
        if(updateTime == null){
            setFieldValByName("updateTime", System.currentTimeMillis(),metaObject);
        }
    }
}
