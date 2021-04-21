package com.art1001.supply.config;


import com.art1001.supply.shiro.JWTShiroRealm;
import com.art1001.supply.shiro.MyDBRealm;
import com.art1001.supply.shiro.filter.JwtFilter;
import com.art1001.supply.shiro.filter.PermissionFilter;
import com.art1001.supply.shiro.filter.RoleFilter;
import com.art1001.supply.shiro.service.ChainDefinitionService;
import com.art1001.supply.shiro.service.impl.ChainDefinitionServiceImpl;
import com.art1001.supply.util.RedisUtil;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSessionStorageEvaluator;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.*;
@Configuration
public class ShiroConfig {


//    @Bean("redisManager")
//    public RedisManager redisManager(){
//        return new RedisManager();
//    }

    @Resource
    private RedisUtil redisUtil;


    @Bean("credentialsMatcher")
    public HashedCredentialsMatcher credentialsMatcher(){
        //hashAlgorithmName必须的，没有默认值。可以有MD5或者SHA-1，如果对密码安全有更高要求可以用SHA-256或者更高。
        //这里使用MD5 storedCredentialsHexEncoded默认是true，此时用的是密码加密用的是Hex编码；false时用Base64编码
        //hashIterations迭代次数，默认值是1。
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("MD5");
        hashedCredentialsMatcher.setHashIterations(2);
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(false);
        return hashedCredentialsMatcher;
    }

    @Bean(name="securityManager")
    public SecurityManager securityManager(@Qualifier("authenticator") Authenticator authenticator) {
        System.err.println("--------------shiro已经加载----------------");
        DefaultWebSecurityManager manager=new DefaultWebSecurityManager();
        manager.setAuthenticator(authenticator);
        List<Realm> rs = new ArrayList<>();
        rs.add(jwtShiroRealm());
        rs.add(myDBRealm());
        manager.setRealms(rs);
        return manager;
    }

    @Bean
    public FilterRegistrationBean<Filter> filterRegistrationBean(@Qualifier("securityManager") SecurityManager securityManager) throws Exception{
        FilterRegistrationBean<Filter> filterRegistration = new FilterRegistrationBean<>();
        filterRegistration.setFilter((Filter)shiroFilter(securityManager).getObject());
        filterRegistration.addInitParameter("targetFilterLifecycle", "true");
        filterRegistration.setAsyncSupported(true);
        filterRegistration.setEnabled(true);
        filterRegistration.setDispatcherTypes(DispatcherType.REQUEST,DispatcherType.ASYNC);
        return filterRegistration;
    }

    @Bean("authenticator")
    public Authenticator authenticator() {
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        authenticator.setRealms(Arrays.asList(jwtShiroRealm(), myDBRealm()));
        authenticator.setAuthenticationStrategy(new FirstSuccessfulStrategy());
        return authenticator;
    }
    @Bean("jwtRealm")
    public Realm jwtShiroRealm() {
        return new JWTShiroRealm(redisUtil);
    }

    @Bean("dbRealm")
    public Realm myDBRealm(){
        MyDBRealm myDBRealm = new MyDBRealm();
        myDBRealm.setCredentialsMatcher(credentialsMatcher());
        return myDBRealm;
    }

    @Bean
    protected SessionStorageEvaluator sessionStorageEvaluator(){
        DefaultWebSessionStorageEvaluator sessionStorageEvaluator = new DefaultWebSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        return sessionStorageEvaluator;
    }


    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager){

        ShiroFilterFactoryBean filter = new ShiroFilterFactoryBean();
        //shiro的核心安全接口
        filter.setSecurityManager(securityManager);
        filter.setLoginUrl("/login");
        Map<String,Filter> filtersMap = new HashMap<>();
        //KickoutSessionFilter kickoutSessionFilter = new KickoutSessionFilter();
        //filtersMap.put("kickout",kickoutSessionFilter);
        //filtersMap.put("kickoutAuth",new KickoutAuthFilter());
        //filtersMap.put("login",new LoginFilter());
        //filtersMap.put("remember",new RememberMeFilter());
        filtersMap.put("jwt", new JwtFilter());
        filtersMap.put("perm",new PermissionFilter());
        filtersMap.put("roleFilter",new RoleFilter());
        //filtersMap.put("baseUrl",new URLFilter());
//        SslFilter sslFilter = new SslFilter();
//        sslFilter.setPort(8443);
//        filtersMap.put("ssl",sslFilter);
        filter.setFilters(filtersMap);
        ChainDefinitionService chainDefinitionService = new ChainDefinitionServiceImpl();
        filter.setFilterChainDefinitions(chainDefinitionService.initFilterChainDefinitions());
        //filter.setFilterChainDefinitionMap(chain);
        return filter;
    }
//    @Bean(name = "lifecycleBeanPostProcessor")
//    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
//        return new LifecycleBeanPostProcessor();
//    }
//
//    /**
//     * DefaultAdvisorAutoProxyCreator，Spring的一个bean，由Advisor决定对哪些类的方法进行AOP代理。
//     */
//    @Bean
//    @ConditionalOnMissingBean
//    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
//        DefaultAdvisorAutoProxyCreator defaultAAP = new DefaultAdvisorAutoProxyCreator();
//        defaultAAP.setProxyTargetClass(true);
//        return defaultAAP;
//    }
//
//    /**
//     * AuthorizationAttributeSourceAdvisor，shiro里实现的Advisor类，
//     * 内部使用AopAllianceAnnotationsAuthorizingMethodInterceptor来拦截用以下注解的方法。
//     */
//    @Bean
//    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
//        AuthorizationAttributeSourceAdvisor aASA = new AuthorizationAttributeSourceAdvisor();
//        aASA.setSecurityManager(securityManager());
//        return aASA;
//    }
//
//    @Bean
//    public MethodInvokingFactoryBean getMethodInvokingFactoryBean(){
//        MethodInvokingFactoryBean factoryBean = new MethodInvokingFactoryBean();
//        factoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
//        factoryBean.setArguments(securityManager());
//        return factoryBean;
//    }

    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator creator=new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") SecurityManager manager) {
        AuthorizationAttributeSourceAdvisor advisor=new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(manager);
        return advisor;
    }
}
