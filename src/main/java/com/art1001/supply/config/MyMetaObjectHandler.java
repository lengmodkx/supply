package com.art1001.supply.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Description mybatis-plus 自动填充插件
 * @Date:2019/3/19 18:25
 * @Author heshaohua
 **/
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        if(metaObject == null){
            setFieldValByName("createTime", System.currentTimeMillis(),metaObject);

        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if(metaObject == null){
            setFieldValByName("updateTime", System.currentTimeMillis(),metaObject);
        }
    }
}
