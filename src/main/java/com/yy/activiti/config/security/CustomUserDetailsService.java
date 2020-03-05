package com.yy.activiti.config.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author YY
 * @date 2020/1/6
 * @description
 */
public interface CustomUserDetailsService {

    UserDetails loadUserByIdentifier(String username) throws UsernameNotFoundException;
}
