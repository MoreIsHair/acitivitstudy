package com.yy.activiti.config.security;

import com.yy.activiti.common.enums.LoginType;
import com.yy.activiti.model.entity.SystemUser;
import com.yy.activiti.service.ISystemUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @author YY
 * @date 2020/1/6
 * @description
 */

@Service
@AllArgsConstructor
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService{

    ISystemUserService systemUserService;

    static final  String USERNAME_DEFALUT = "test";

    static final  String PASSWORD_DEFALUT = "test";

    @Override
    public UserDetails loadUserByIdentifier(String username) throws UsernameNotFoundException {
        SystemUser byUsername = systemUserService.getByUsername(username);
        long l = System.currentTimeMillis();
        CustomUserDetails customUserDetails = new CustomUserDetails(l, LoginType.PWD
                , byUsername==null?USERNAME_DEFALUT:username
                , byUsername==null?PASSWORD_DEFALUT:byUsername.getPassword()
                ,getAuthority(byUsername));
        return customUserDetails;
    }

    /**
     * 获取用户权限
     * @param user user
     * @return Set
     */
    private Set<GrantedAuthority> getAuthority(SystemUser user) {
        // 权限集合
        Set<GrantedAuthority> authorities = new HashSet<>();
        // 根据角色查找菜单权限
        return authorities;
    }
}
