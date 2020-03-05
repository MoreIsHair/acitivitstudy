package com.yy.activiti.config.oauth2;

import com.yy.activiti.common.core.mobile.MobileSecurityConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

/**
 * @author YY
 * @date 2020/1/9
 * @description 资源服务器配置
 *  ResourceServerConfig 用于保护oauth相关的endpoints，同时主要作用于用户的登录(form login,Basic auth)
 * SecurityConfig 用于保护oauth要开放的资源，同时主要作用于client端以及token的认证(Bearer auth)
 * 所以我们让SecurityConfig优先于ResourceServerConfig，且在SecurityConfig 不拦截oauth要开放的资源，
 * 在ResourceServerConfig 中配置需要token验证的资源，也就是我们对外提供的接口。
 * 所以这里对于所有微服务的接口定义有一个要求，就是全部以/api开头。
 */

@Configuration
@EnableResourceServer
@Order(3)
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final String RESOURCE_ID = "resource_id";

    /**
     * 开放权限的URL
     */
    private final FilterIgnorePropertiesConfig filterIgnorePropertiesConfig;

    /**
     * 手机登录配置
     */
    private final MobileSecurityConfigurer mobileSecurityConfigurer;


    @Autowired
    public OAuth2ResourceServerConfig(FilterIgnorePropertiesConfig filterIgnorePropertiesConfig,MobileSecurityConfigurer mobileSecurityConfigurer) {
        this.filterIgnorePropertiesConfig = filterIgnorePropertiesConfig;
        this.mobileSecurityConfigurer = mobileSecurityConfigurer;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer config) {
        config.resourceId(RESOURCE_ID).stateless(false);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        String[] ignores = new String[filterIgnorePropertiesConfig.getUrls().size()];
        http
                .csrf().disable()
                //.httpBasic().disable()
                .authorizeRequests()
                .antMatchers(filterIgnorePropertiesConfig.getUrls().toArray(ignores)).permitAll()
                .anyRequest().authenticated()
                .and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
        // 手机号登录
        http.apply(mobileSecurityConfigurer);
       //http
       //        .requestMatchers().antMatchers("/api/**")
       //        .and()
       //        .authorizeRequests()
       //        .antMatchers("/api/**").authenticated()
       //        .and()
       //        .authorizeRequests()
       //        .antMatchers(filterIgnorePropertiesConfig.getUrls().toArray(new String[0])).permitAll()
       //        .and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
       //// 手机号登录
       //http.apply(mobileSecurityConfigurer);
    }
}
