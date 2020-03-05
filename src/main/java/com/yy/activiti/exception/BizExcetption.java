package com.yy.activiti.exception;

/**
 * @author YY
 * @date 2019/12/23
 * @description 自定义异常类
 */
public class BizExcetption extends RuntimeException{

    public BizExcetption() {
        super(); }

    public BizExcetption(String message) {
        super(message);
    }

    public BizExcetption(String message, Throwable cause) {
        super(message, cause);
    }

    public BizExcetption(Throwable cause) {
        super(cause);
    }

    public BizExcetption(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
