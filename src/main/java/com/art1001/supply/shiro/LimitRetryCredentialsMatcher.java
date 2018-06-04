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

    public LimitRetryCredentialsMatcher(CacheManager cacheManager) {
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
            passwordRetryCache.put(username, retryCount);
        }

        if(retryCount.incrementAndGet() > 5) {
        	throw new ExcessiveAttemptsException();
        }else{
            passwordRetryCache.put(username, retryCount);
        }

        boolean matches = super.doCredentialsMatch(token, info);

        if (matches) {
            //从缓存中移除该用户的登录记录
            passwordRetryCache.remove(username);
        }

        return matches;
    }
	
}
