package com.art1001.supply.util;

import java.util.*;

/**
 *  * OrderedProperties
 * <p>
 *  * @author 程序猫
 * <p>
 *  * @date 2019-03-27
 * <p>
 *  
 */

public class OrderedProperties extends Properties {


    private static final long serialVersionUID = -4627607243846121965L;


    private final LinkedHashSet<Object> keys = new LinkedHashSet<Object>();

    @Override
    public Enumeration<Object> keys() {

        return Collections.<Object>enumeration(keys);

    }

    @Override
    public Object put(Object key, Object value) {

        keys.add(key);

        return super.put(key, value);

    }


    @Override
    public Set<Object> keySet() {

        return keys;

    }

    @Override
    public Set<String> stringPropertyNames() {

        Set<String> set = new LinkedHashSet<String>();

        for (Object key : this.keys) {

            set.add((String) key);

        }


        return set;

    }

}