package com.art1001.supply.shiro.cache.redis;

import com.art1001.supply.redis.RedisManager;
import com.art1001.supply.shiro.cache.ShiroCacheManager;
import org.apache.shiro.cache.Cache;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 
 * @ClassName RedisShiroCacheManager
 * @Description Redis管理
 *
 * @author wangyafeng
 * @data 2016年12月13日 下午1:43:57
 */
public class RedisShiroCacheManager implements ShiroCacheManager {

    private RedisManager cacheManager;

    public RedisShiroCacheManager(RedisManager cacheManager){
        this.cacheManager = cacheManager;
    }

	@Override
    public <K, V> Cache<K, V> getCache(String name) {
        return new RedisShiroCache<K, V>(name, cacheManager);
    }

    @Override
    public void destroy() {
    	//做一些需要释放资源的操作
    }

}
