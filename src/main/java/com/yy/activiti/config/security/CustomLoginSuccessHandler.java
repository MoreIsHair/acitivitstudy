package com.yy.activiti.config.security;

import cn.hutool.json.JSONUtil;
import com.yy.activiti.util.jwt.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YY
 * @date 2020/1/16
 * @description 自定义登录成功处理
 */
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Map<String,String> map=new HashMap<>(3);
        map.put("code", "200");
        map.put("msg", authentication.getName()+"登录成功");
        map.put("token", JwtUtil.generateToken(()-> authentication.getName()));
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONUtil.toJsonStr(map));
    }

}
