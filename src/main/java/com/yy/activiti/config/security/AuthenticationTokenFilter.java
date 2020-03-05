package com.yy.activiti.config.security;

import com.yy.activiti.util.jwt.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author YY
 * @date 2020/1/17
 * @description
 */
public class AuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter {

    private CustomUserDetailsService userDetailsService;

    public AuthenticationTokenFilter(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        // 将 ServletRequest 转换为 HttpServletRequest 才能拿到请求头中的 token
        HttpServletRequest httpRequest = (javax.servlet.http.HttpServletRequest) req;
        // 尝试获取请求头的 token
        String authToken = httpRequest.getHeader(JwtUtil.TOKENHEADER);
        // 尝试拿 token 中的 username
        // 若是没有 token 或者拿 username 时出现异常，那么 username 为 null
        String username = JwtUtil.getUsernameFromToken(authToken);

        // 如果上面解析 token 成功并且拿到了 username 并且本次会话的权限还未被写入
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 用 UserDetailsService 从数据库中拿到用户的 UserDetails 类
            // UserDetails 类是 Spring Security 用于保存用户权限的实体类
            UserDetails userDetails = this.userDetailsService.loadUserByIdentifier(username);
            // 检查用户带来的 token 是否有效
            // 包括 token 和 userDetails 中用户名是否一样， token 是否过期， token 生成时间是否在最后一次密码修改时间之前
            // 若是检查通过
            if (JwtUtil.validateToken(authToken, userDetails)) {
                // 生成通过认证
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                // 将权限写入本次会话
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            if (!userDetails.isEnabled()){
                res.setCharacterEncoding("UTF-8");
                res.setContentType("application/json;charset=UTF-8");
                res.getWriter().print("{\"code\":\"452\",\"data\":\"\",\"message\":\"账号处于黑名单\"}");
                return;
            }
        }
        chain.doFilter(req, res);
    }
}
