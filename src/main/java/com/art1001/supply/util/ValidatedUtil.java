package com.art1001.supply.util;

import com.art1001.supply.exception.BaseException;

/**
 * @program: shop-macronet-server
 * @author: chippy
 */
public class ValidatedUtil {

    public static void filterNullParam(Object... objects) {
        for (Object object : objects) {
            if (ObjectsUtil.isEmpty(object)) {
                throw new BaseException(String.format("该[%s]参数不能为空", object));
            }
        }
    }
}
