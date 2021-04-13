package com.art1001.supply.shiro;

import com.art1001.supply.shiro.util.JwtUtil;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JWTCredentialsMatcher implements CredentialsMatcher {
	
	private final Logger log = LoggerFactory.getLogger(JWTCredentialsMatcher.class);

    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
        String token = (String) authenticationToken.getCredentials();
        try {
            return JwtUtil.verify(token,"1qaz2wsx#EDC");
        } catch (JWTVerificationException e) {
            log.error("Token Error:{}", e.getMessage());
        }
        return false;
    }

}
