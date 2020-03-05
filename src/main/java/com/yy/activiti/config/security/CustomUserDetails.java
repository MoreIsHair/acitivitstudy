package com.yy.activiti.config.security;

import com.yy.activiti.common.enums.LoginType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @author YY
 * @date 2020/1/6
 * @description
 */
public class CustomUserDetails extends User {
    private static final long serialVersionUID = 1818665858359058967L;

    /**
     * 登录类型
     */
    private LoginType loginType;

    /**
     * 开始授权时间
     */
    private long start;

    public CustomUserDetails(long start, LoginType loginType, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.start = start;
        this.loginType = loginType;
    }
}
