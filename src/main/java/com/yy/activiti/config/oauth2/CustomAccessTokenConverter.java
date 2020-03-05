package com.yy.activiti.config.oauth2;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author YY
 * @date 2020/1/10
 * @description 自定义token
 */
@Component
public class CustomAccessTokenConverter extends DefaultAccessTokenConverter {

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> claims) {
        OAuth2Authentication authentication =
                super.extractAuthentication(claims);
        authentication.setDetails(claims);
        return authentication;
    }
}