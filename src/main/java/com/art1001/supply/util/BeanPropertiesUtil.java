package com.art1001.supply.util;

import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.BaseException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

/**
 * @author heshaohua
 * @Title: BeanPropertiesUtil
 * @Description: TODO
 * @date 2018/11/19 11:31
 **/
public class BeanPropertiesUtil {
    /**
     * @param obj        要查询的对象
     * @param fieldNames 指定要查询的字段名称数组
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @Description: 如果查询对象指定的所有字段都不为空，则返回true，否则异常
     * @author: heshaohua
     * @date: 2018/11/19 11:31
     */
    public static boolean fieldsNotNullOrEmpty(Object obj, String[] fieldNames) throws IllegalArgumentException, IllegalAccessException {
        Class<?> cls = obj.getClass();

        for (String fieldName : fieldNames) {
            Field field = getBeanProperty(cls, fieldName);
            Object value = field.get(obj);
        }

        return true;
    }

    /**
     * @param obj
     * @param fieldNames
     * @return
     * @Description: 将对象中指定的所有字段置空，成功返回true
     * @author: heshaohua
     * @date: 2018/11/19 11:35
     */
    public static boolean setFieldsNull(Object obj, String[] fieldNames) {
        Class<?> cls = obj.getClass();
        try {
            for (String fieldName : fieldNames) {
                Field field = getBeanProperty(cls, fieldName);
                field.set(obj, null);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param source     源对象
     * @param target     目标对象
     * @param fieldNames 字段名
     * @return
     * @Description: 从源对象拷贝属性值到目标对象 属性值不能为基本数据类型
     * @author: heshaohua
     * @date: 2018/11/19 11:31
     */
    public static boolean copyFields(Object source, Object target, String[] fieldNames) {
        Class<?> sCls = source.getClass();
        Class<?> tCls = target.getClass();
        try {
            for (String fieldName : fieldNames) {
                Field tField = getBeanProperty(tCls, fieldName);
                Field sField = getBeanProperty(sCls, fieldName);
                tField.set(target, sField.get(source));
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
        return true;
    }

    static Field getBeanProperty(Class<?> cls, String fieldName) {
        Field field = null;
        try {
            field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (Exception e) {
            // TODO: handle exception
            if (cls.getSuperclass() != Object.class) {
                field = getBeanProperty(cls.getSuperclass(), fieldName);
            }
        }
        return field;
    }
}
