package com.art1001.supply.config;

import com.art1001.supply.redis.RedisManager;
import com.art1001.supply.listener.ShiroSessionListener;
import com.art1001.supply.shiro.LimitRetryCredentialsMatcher;
import com.art1001.supply.shiro.MyDBRealm;
import com.art1001.supply.shiro.cache.CustomShiroCacheManager;
import com.art1001.supply.shiro.cache.ShiroCacheManager;
import com.art1001.supply.shiro.cache.redis.RedisShiroCacheManager;
import com.art1001.supply.shiro.filter.*;
import com.art1001.supply.shiro.service.ChainDefinitionService;
import com.art1001.supply.shiro.service.impl.ChainDefinitionServiceImpl;
import com.art1001.supply.shiro.session.ShiroSessionDAO;
import com.art1001.supply.shiro.session.redis.RedisShiroSessionRepository;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.ExecutorServiceSessionValidationScheduler;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authz.SslFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean("redisManager")
    public RedisManager redisManager(){
        RedisManager redisManager = new RedisManager();
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(600);
        config.setMaxIdle(300);
        config.setTimeBetweenEvictionRunsMillis(30000);
        config.setMinEvictableIdleTimeMillis(30000);
        config.setSoftMinEvictableIdleTimeMillis(30000);
        config.setTestOnBorrow(true);
        JedisPool jedisPool = new JedisPool(config, "localhost");
        redisManager.setJedisPool(jedisPool);
        return redisManager;
    }


    @Bean("cacheManager")
    public CustomShiroCacheManager cacheManager(){
        ShiroCacheManager redisShiroCacheManager = new RedisShiroCacheManager(redisManager());
        return new CustomShiroCacheManager(redisShiroCacheManager);
    }


    @Bean("credentialsMatcher")
    public LimitRetryCredentialsMatcher credentialsMatcher(){
        //hashAlgorithmName必须的，没有默认值。可以有MD5或者SHA-1，如果对密码安全有更高要求可以用SHA-256或者更高。
        //这里使用MD5 storedCredentialsHexEncoded默认是true，此时用的是密码加密用的是Hex编码；false时用Base64编码
        //hashIterations迭代次数，默认值是1。
        LimitRetryCredentialsMatcher limitRetryCredentialsMatcher = new LimitRetryCredentialsMatcher(redisManager(), cacheManager());
        limitRetryCredentialsMatcher.setHashAlgorithmName("MD5");
        limitRetryCredentialsMatcher.setHashIterations(2);
        limitRetryCredentialsMatcher.setStoredCredentialsHexEncoded(false);
        return limitRetryCredentialsMatcher;
    }

    @Bean("myDBRealm")
    public MyDBRealm myDBRealm(){
        MyDBRealm myDBRealm = new MyDBRealm();
        myDBRealm.setCredentialsMatcher(credentialsMatcher());
        //开启缓存
        myDBRealm.setCachingEnabled(true);
        //认证信息:这里不进行缓存
        myDBRealm.setAuthenticationCachingEnabled(false);
        //cache中配置的认证缓存名称
        myDBRealm.setAuthenticationCacheName("authenticationCache");
        //授权信息:这里进行缓存
        myDBRealm.setAuthorizationCachingEnabled(true);
        //cache中配置的授权缓存名称
        myDBRealm.setAuthorizationCacheName("authorizationCache");
        return myDBRealm;
    }

    @Bean("shiroSessionDAO")
    public ShiroSessionDAO shiroSessionDAO(){
        RedisShiroSessionRepository shiroSessionRepository = new RedisShiroSessionRepository();
        shiroSessionRepository.setRedisManager(redisManager());
        return new ShiroSessionDAO(shiroSessionRepository);
    }

    @Bean("sessionManager")
    public DefaultWebSessionManager sessionManager(){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        //是否删除无效的，默认也是开启
        sessionManager.setDeleteInvalidSessions(true);
        //使用redis保存session
        sessionManager.setSessionDAO(shiroSessionDAO());
        //session 有效时间为半小时 （毫秒单位）
        sessionManager.setGlobalSessionTimeout(1800000);
        //相隔多久检查一次session的有效性
        sessionManager.setSessionValidationInterval(1800000);
        //是否开启 检测，默认开启
        sessionManager.setSessionValidationSchedulerEnabled(true);
        ExecutorServiceSessionValidationScheduler scheduler = new ExecutorServiceSessionValidationScheduler(sessionManager);
        sessionManager.setSessionValidationScheduler(scheduler);
        //设置名称，防止和servlet的cookie冲突
        SimpleCookie simpleCookie = new SimpleCookie("SUPPLY-SID");
        sessionManager.setSessionIdCookie(simpleCookie);
        ArrayList<SessionListener> arrayList = new ArrayList<>();
        ShiroSessionListener sessionListener = new ShiroSessionListener();
        arrayList.add(sessionListener);
        sessionManager.setSessionListeners(arrayList);
        return sessionManager;
    }


    @Bean("securityManager")
    public DefaultWebSecurityManager securityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myDBRealm());
        securityManager.setSessionManager(sessionManager());
        securityManager.setCacheManager(cacheManager());

        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        SimpleCookie simpleCookie = new SimpleCookie("SUPPLY-SID");
        simpleCookie.setMaxAge(604800);
        rememberMeManager.setCipherKey(Base64.decode("GsHaWo4m1eNbE0kNSMULhg=="));
        rememberMeManager.setCookie(simpleCookie);
        securityManager.setRememberMeManager(rememberMeManager);
        return securityManager;
    }

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(){

        ShiroFilterFactoryBean filter = new ShiroFilterFactoryBean();
        //shiro的核心安全接口
        filter.setSecurityManager(securityManager());
        filter.setLoginUrl("/login.html");
        filter.setSuccessUrl("/index.html");
        filter.setUnauthorizedUrl("/denied.html");
        ChainDefinitionService chainDefinitionService = new ChainDefinitionServiceImpl();
        filter.setFilterChainDefinitions(chainDefinitionService.initFilterChainDefinitions());

        Map<String,Filter> filtersMap = new HashMap<>();
        KickoutSessionFilter kickoutSessionFilter = new KickoutSessionFilter();
        filtersMap.put("kickout",kickoutSessionFilter);
        filtersMap.put("kickoutAuth",new KickoutAuthFilter());
        filtersMap.put("login",new LoginFilter());
        filtersMap.put("remember",new RememberMeFilter());
        filtersMap.put("perm",new PermissionFilter());
        filtersMap.put("roleFilter",new RoleFilter());
        filtersMap.put("baseUrl",new URLFilter());
//        SslFilter sslFilter = new SslFilter();
//        sslFilter.setPort(8443);
//        filtersMap.put("ssl",sslFilter);
        filter.setFilters(filtersMap);
        return filter;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager());
        return advisor;
    }

    @Bean()
    public MethodInvokingFactoryBean getBean(){
        MethodInvokingFactoryBean bean = new MethodInvokingFactoryBean();
        bean.setArguments(securityManager());
        bean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        return bean;
    }

}
