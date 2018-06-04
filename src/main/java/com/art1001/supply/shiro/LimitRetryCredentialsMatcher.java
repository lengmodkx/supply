package com.art1001.supply.shiro;

import com.art1001.supply.redis.RedisManager;
import com.art1001.supply.shiro.cache.redis.RedisShiroCache;
import com.art1001.supply.util.SerializeUtil;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * 
 * @ClassName: LimitRetryHashedMatcher
 * @Description: 限制登录次数，如果连续5次输错用户名或密码，锁定10分钟，依靠Ehcache自带的timeToIdleSeconds来保证锁定时间
 * @author wangyafeng
 * @date 2016年7月12日 下午4:44:23
 *
 */
public class LimitRetryCredentialsMatcher extends HashedCredentialsMatcher {
    
	private Cache<String, AtomicInteger> passwordRetryCache;

    private RedisManager redisManager;

    public LimitRetryCredentialsMatcher(RedisManager redisManager ,CacheManager cacheManager) {
        this.redisManager =redisManager;
        passwordRetryCache = cacheManager.getCache("passwordRetryCache");
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = token.getPrincipal().toString();
        // 尝试登录次数+1
        AtomicInteger retryCount = passwordRetryCache.get(username);

        // 查看缓存中的尝试次数
        if (retryCount == null) {
            retryCount = new AtomicInteger(0);
            if(passwordRetryCache instanceof RedisShiroCache) {
                redisManager.saveValueByKey(RedisShiroCache.DB_INDEX, ((RedisShiroCache<String, AtomicInteger>) passwordRetryCache).generateCacheKey(username).getBytes(), SerializeUtil.serialize(retryCount), 600);
            }
        }
        if(retryCount.intValue() >= 5)
        {
            throw new LockedAccountException();
        }else if (retryCount.incrementAndGet() >= 5) {
            // 如果尝试登录次数大于5
            throw new ExcessiveAttemptsException();
        }

        if(passwordRetryCache instanceof RedisShiroCache) {
            redisManager.saveValueByKey(RedisShiroCache.DB_INDEX, ((RedisShiroCache<String, AtomicInteger>)passwordRetryCache).generateCacheKey(username).getBytes(), SerializeUtil.serialize(retryCount), 600);
        }

        boolean matches = super.doCredentialsMatch(token, info);

        if (matches) {
            //从缓存中移除该用户的登录记录
            passwordRetryCache.remove(username);
        }

        return matches;
    }
	
}
