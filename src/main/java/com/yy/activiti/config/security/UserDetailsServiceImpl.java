package com.yy.activiti.config.security;

import com.yy.activiti.common.enums.LoginType;
import com.yy.activiti.model.entity.SystemUser;
import com.yy.activiti.service.ISystemUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author YY
 * @date 2020/1/7
 * @description  由于activity中ActivitiUserGroupManagerImpl需要注入一个UserDetailsService
 */
@Component
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private ISystemUserService systemUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUser byUsername = systemUserService.getByUsername(username);
        long l = System.currentTimeMillis();
        CustomUserDetails customUserDetails = new CustomUserDetails(l, LoginType.PWD, username, byUsername.getPassword(), new ArrayList<>(16));
        return customUserDetails;
    }
}
