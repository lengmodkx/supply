/**
 * 
 */
package com.art1001.supply.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;


/**
 * @ClassName CustomerShiroCacheManager
 * @Description 用户自定义cachemanager，方便扩展
 *
 * @author wangyafeng
 * @data 2016年12月13日 下午3:42:19
 */
public class CustomShiroCacheManager implements CacheManager, Destroyable {

    private ShiroCacheManager shiroCacheManager;

    public CustomShiroCacheManager(ShiroCacheManager shiroCacheManager){
        this.shiroCacheManager = shiroCacheManager;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        return shiroCacheManager.getCache(name);
    }

    @Override
    public void destroy() {
        shiroCacheManager.destroy();
    }

}
