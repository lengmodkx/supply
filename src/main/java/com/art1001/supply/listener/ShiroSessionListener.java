package com.art1001.supply.listener;


import com.art1001.supply.shiro.session.ShiroSessionRepository;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 
 * shiro session 会话监听器
 *
 * @author wangyafeng
 * 2016年12月13日 下午2:49:29
 */
public class ShiroSessionListener implements SessionListener {

    @Resource
    private ShiroSessionRepository shiroSessionRepository;



    /**
     * session会话开始
     */
    @Override
    public void onStart(Session session) {
    }
    /**
     * session会话结束
     */
    @Override
    public void onStop(Session session) {
    }

    /**
     * session会话到期
     */
    @Override
    public void onExpiration(Session session) {
    	//session过期进行清理
        shiroSessionRepository.deleteSession(session.getId());
    }
}

