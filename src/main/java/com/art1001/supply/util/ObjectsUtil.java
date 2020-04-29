
package com.art1001.supply.util;

import javax.servlet.http.Cookie;
import java.util.Collection;
import java.util.Map;

/**
 * 对象判空工具类
 *
 * @author chippy
 */
public class ObjectsUtil {

    private ObjectsUtil() {
    }

    /**
     * 判断某对象(String, Collection, Map, obj)是否不为空.
     *
     * @param obj 需要判断的目标哦对象
     * @return boolean
     * @author by K2 Aug 21, 2015
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 判断某对象(String, Collection, Map, obj)是否为空.
     *
     * @param obj 需要判断的目标对象
     * @return boolean
     * @author by K2 Aug 21, 2015
     */
    public static boolean isEmpty(Object obj) {
        boolean result;
        if (obj == null) {
            return Boolean.TRUE;
        }
        if (obj instanceof String) {
            result = (obj.toString().trim().length() == 0) || obj.toString().trim().equals("null");
        } else if (obj instanceof Collection) {
            result = ((Collection<?>) obj).isEmpty();
        } else if (obj instanceof Map) {
            result = ((Map<?, ?>) obj).isEmpty();
        } else {
            result = obj.toString().trim().length() < 1;
        }
        return result;
    }

    public static String getValue(Cookie[] cookies, String key) {
        String value = null;
        if (null != cookies && cookies.length > 0) {
            for (Cookie c : cookies) {
                if (key.equals(c.getName())) {
                    value = c.getValue();
                    break;
                }
            }
        }
        return value;
    }
}
