package com.art1001.supply.annotation;

import java.lang.annotation.*;

/**
 * @Description 鉴权标记
 * @Date:2019/6/18 16:51
 * @Author ddm
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProjectAuth {

    String value();
}
