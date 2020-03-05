package com.yy.activiti.config.security;

import com.yy.activiti.service.ISystemUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * @author YY
 * @date 2020/1/7
 * @description
 */
@Component
@Slf4j
@AllArgsConstructor
public class CustomAuthenticationSuccessHandler {

    private final ISystemUserService systemUserService;

    @EventListener({AuthenticationSuccessEvent.class, InteractiveAuthenticationSuccessEvent.class})
    public void processAuthenticationSuccessEvent(AbstractAuthenticationEvent event) {
        // 注意：登录包括oauth2客户端、用户名密码登录都会触发AuthenticationSuccessEvent，这里只记录用户名密码登录的日志
        if (event.getAuthentication().getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) event.getAuthentication().getPrincipal();
            String username = userDetails.getUsername();
            // 记录日志
            log.info("CustomAuthenticationSuccessHandler->登录成功, username: {}", username);

        }
    }


    /**
     * 获取用户名
     *
     * @param authentication authentication
     * @return String
     */
    private String getUsername(Authentication authentication) {
        String username = "";
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof Principal) {
            username = ((Principal) principal).getName();
        }
        return username;
    }
}
