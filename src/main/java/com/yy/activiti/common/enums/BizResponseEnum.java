package com.yy.activiti.common.enums;

import lombok.Getter;

/**
 * @author YY
 * @date 2019/12/23
 * @description 自定义的返回结果
 */
@Getter
public enum  BizResponseEnum {
    ;
    private String msg;
    private Integer code;

    BizResponseEnum(String msg, Integer code) {
        this.msg = msg;
        this.code = code;
    }
}
