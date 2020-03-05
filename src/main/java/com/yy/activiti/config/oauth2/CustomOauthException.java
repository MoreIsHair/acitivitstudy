package com.yy.activiti.config.oauth2;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yy.activiti.common.CustomOauthExceptionSerializer;
import lombok.Data;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * @author YY
 * @date 2020/1/9
 * @description
 */
@Data
@JsonSerialize(using = CustomOauthExceptionSerializer.class)
public class CustomOauthException extends OAuth2Exception {
    private Integer code;
    public CustomOauthException(String msg, Throwable t) {
        super(msg, t);
    }
    public CustomOauthException(String message, int httpErrorCode) {
        super(message);
        this.code = httpErrorCode;
    }
}
