package com.yy.activiti.advice;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonInputMessage;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author YY
 * @date 2019/12/24
 * @description 请求参数，自定义处理增强
 */
@ControllerAdvice
@Slf4j
public class LogRequestAdvice implements RequestBodyAdvice {
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 判断是否有此注解
        // 只有为true时才会执行afterBodyRead
        return  methodParameter.getParameterAnnotation(RequestBody.class) != null;

    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return new MappingJacksonInputMessage(inputMessage.getBody(), inputMessage.getHeaders());
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
         // 去除注 回车 \n 水平制表符 \t 空格 \s 换行 \r
        String s2 = StrUtil.removeAllLineBreaks(JSONUtil.toJsonStr(body));
        log.info("请求参数为:{}", s2);
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        log.debug("this is EmptyBody");
        return null;
    }
}
