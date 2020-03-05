package com.yy.activiti.config.security;

import cn.hutool.core.lang.Console;
import com.yy.activiti.util.SpringContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * @author YY
 * @date 2020/1/6
 * @description     重写参数为HttpSecurity的configure方法，配置拦截策略
 *          安全配置（修改springboot自带的安全配置）
 *      *   *    *    * XSS利用站点内的信任用户
 *      *   *    *    * CSRF则通过伪装来自受信任用户的请求来利用受信任的网站
 *      // 禁用缓存
 *      httpSecurity.headers().cacheControl();
 *       使用内容安全策略(启用CSP标头)来防止XSS攻击
 *         security.headers()
 *                 .contentSecurityPolicy("default-src 'self';'unsafe-inline'");
 *
 *          启用CSRF保护
 *         security
 *                 .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(2)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;


    /*
    会出现循环依赖
    @Autowired
    private AuthorizationServerEndpointsConfiguration endpoints;
    */

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        Console.log("=========================================");
        AuthorizationServerEndpointsConfiguration endpoints = SpringContextHolder.getApplicationContext().getBean(AuthorizationServerEndpointsConfiguration.class);
        Console.log(endpoints);
        if (!endpoints.getEndpointsConfigurer().isUserDetailsServiceOverride()) {
            endpoints.getEndpointsConfigurer().userDetailsService(http.getSharedObject(UserDetailsService.class));
        }
        // 认证管理器
        endpoints.getEndpointsConfigurer().authenticationManager(authenticationManager());

        // accessDeniedHandler
        http.exceptionHandling()
                .accessDeniedHandler(new OAuth2AccessDeniedHandler());

        http
                /*.authorizeRequests()
                .antMatchers(
                        "/actuator/"
                        , "/token/"
                        ,"/oauth/authorize"
                        ,"/moblie/token").permitAll().and()*/
                .csrf().disable();
        http
                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll();

        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and().formLogin()
                .usernameParameter("username").passwordParameter("password")
                .loginProcessingUrl("/login").successHandler(new CustomLoginSuccessHandler())
                .and().logout().logoutUrl("/logout").logoutSuccessHandler(new CustomLogoutSuccessHandler());
        // 在用户名密码过滤器之前添加对token的解析，从而实现用户的登录
        http.addFilterBefore(new AuthenticationTokenFilter(customUserDetailsService), UsernamePasswordAuthenticationFilter.class);


    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider());
    }


    /**
     *  装载BCrypt密码编码器
     */
    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证Provider，提供获取用户信息、认证、授权等功能
     *
     * @return AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authProvider() {
        return new CustomUserDetailsAuthenticationProvider(encoder(), customUserDetailsService);
    }

    /**
     * 不定义没有password grant_type
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

}
