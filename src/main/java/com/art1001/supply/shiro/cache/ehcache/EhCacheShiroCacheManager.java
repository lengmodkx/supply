package com.art1001.supply.shiro.cache.ehcache;

import com.art1001.supply.shiro.cache.ShiroCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.stereotype.Component;

/**
 * 
 * @ClassName EhCacheShiroCacheManager
 * @Description ehCache管理
 *
 * @author wangyafeng
 * @data 2016年12月13日 下午3:18:44
 */
public class EhCacheShiroCacheManager implements ShiroCacheManager {

    private org.springframework.cache.CacheManager cacheManager;

    /**
     * 设置spring cacheManager
     *
     * @param cacheManager
     */
    public void setCacheManager(org.springframework.cache.CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        org.springframework.cache.Cache cache = cacheManager.getCache(name);
        return new EhcacheShiroCache<K, V>(cache);
    }


	/* (non-Javadoc)
	 * @see com.webside.shiro.cache.ShiroCacheManager#destroy()
	 */
	@Override
	public void destroy() {
		
	}


}
