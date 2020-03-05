package com.yy.activiti.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author YY
 * @date 2020/1/6
 * @description
 */
@Slf4j
public class CustomUserDetailsAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";

    private PasswordEncoder passwordEncoder;

    private CustomUserDetailsService userDetailsService;

    private String userNotFoundEncodedPassword;

    public CustomUserDetailsAuthenticationProvider(PasswordEncoder passwordEncoder, CustomUserDetailsService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            log.debug("认证失败: 密码为空.");
            throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "密码为空."));
        }
        // 获取密码
        String presentedPassword = authentication.getCredentials().toString();
        // 匹配密码
        if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            log.debug("认证失败: 密码错误.");
            throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "密码错误."));
        }
    }

    @Override
    protected void doAfterPropertiesSet() throws Exception {
        if (this.userDetailsService == null)
            throw new IllegalArgumentException("UserDetailsService不能为空.");
        this.userNotFoundEncodedPassword = this.passwordEncoder.encode(USER_NOT_FOUND_PASSWORD);
    }


    /**
     * 加载用户信息
     *
     * @param username       username
     * @param authentication authentication
     * @return UserDetails
     * @throws AuthenticationException
     */
    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException{
        UserDetails loadedUser;
        try {
            // TODO 加载用户信息
            loadedUser = this.userDetailsService.loadUserByIdentifier(authentication.getPrincipal().toString());
        }catch (UsernameNotFoundException notFound) {
            if (authentication.getCredentials() != null) {
                String presentedPassword = authentication.getCredentials().toString();
                passwordEncoder.matches(presentedPassword, userNotFoundEncodedPassword);
            }
            throw notFound;
        } catch (Exception repositoryProblem) {
            throw new InternalAuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
        }
        if (loadedUser == null) {
            throw new InternalAuthenticationServiceException("获取用户信息失败.");
        }
        return loadedUser;
    }
}
