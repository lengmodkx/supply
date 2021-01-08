package com.art1001.supply.annotation;

import java.lang.annotation.*;

/**
 * @author Administrator
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EsRule {
    /**
     * 1 新增 2修改 3删除
     * @return
     */
    int sort();

    /**
     * 保存类型
     * @return
     */
    EsRuleType type() default EsRuleType.DEFAULT;
}
