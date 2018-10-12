package com.art1001.supply.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.*;

/**
 * @author heshaohua
 * @Title: JsonUtil
 * @Description: TODO
 * @date 2018/10/10 15:40
 **/
public class JsonUtil {

    /**
     * 过滤json结构数据中为null的节点
     * @param jsonStr
     * @return
     * @throws Exception
     */
    public static Object filterNone(String jsonStr) throws Exception{
        if(jsonStr==null||jsonStr.equals("")||jsonStr.equals("null"))
            return null;
        if(jsonStr.indexOf("[")==0){
            List<Object> lists = new ArrayList<Object>();
            List<HashMap> list = JSON.parseArray(jsonStr, HashMap.class);
            for(int i=0;i<list.size();i++){
                Map<String,Object> map = list.get(i);
                for (Map.Entry<String, Object> entrys : map.entrySet()) {
                    Map<String,Object> maps = new HashMap<String, Object>();
                    Object objs = JsonUtil.filterNone(entrys.getValue().toString());
                    if(objs!=null){
                        maps.put(entrys.getKey(),objs);
                        lists.add(maps);
                    }
                }
            }
            if(lists.size()==0)
                return null;
            return lists;
        }
        if(jsonStr.indexOf("{")==0){
            Map<String, Object> targetMap = new HashMap<String, Object>();
            LinkedHashMap<String, Object> jsonMap = JSON.parseObject(jsonStr, new TypeReference<LinkedHashMap<String, Object>>(){});
            for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
                Object obj = JsonUtil.filterNone(entry.getValue().toString());
                if(obj==null)
                    continue;
                targetMap.put(entry.getKey(),obj);
            }
            if(targetMap.isEmpty())
                return null;
            return targetMap;
        }
        return jsonStr;
    }

}
