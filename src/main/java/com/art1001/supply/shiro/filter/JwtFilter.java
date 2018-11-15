package com.art1001.supply.shiro.filter;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.redis.RedisManager;
import com.art1001.supply.shiro.util.JwtUtil;
import com.art1001.supply.util.SerializeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtFilter extends BasicHttpAuthenticationFilter {
    /**
     * 执行登录认证
    */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String token = httpServletRequest.getHeader("Authorization");
            //检查请求头部是否含有token
            if(StringUtils.isEmpty(token)){
                JSONObject object = new JSONObject();
                object.put("result","0");
                object.put("msg","token is null");
                response.getWriter().print(object);
                return false;
            }

            //检查refreshToken是否存在在缓存中
            RedisManager redisManager = new RedisManager();
            String userName = JwtUtil.getUsername(token);
            if (!redisManager.exists(userName)){
                JSONObject object = new JSONObject();
                object.put("result","0");
                object.put("msg","refreshToken is invalid");
                response.getWriter().print(object);
                return false;
            }

            //校验token是否正确
            byte[] value = redisManager.getValueByKey(0,userName.getBytes());
            String refreshToken = (String) SerializeUtil.deserialize(value);
            if(!JwtUtil.verify(token,refreshToken)){
                JSONObject object = new JSONObject();
                object.put("result","0");
                object.put("msg","token is invalid");
                response.getWriter().print(object);
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
}
