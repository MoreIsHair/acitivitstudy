package com.yy.activiti.config;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author YY
 * @date 2020/1/6
 * @description 自定义的springscurity权限实现类
 */
public class GrantedAuthorityImpl implements GrantedAuthority {
    private static final long serialVersionUID = -6643766279874218936L;

    private String roleName;

    public GrantedAuthorityImpl(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String getAuthority() {
        return roleName;
    }
}
