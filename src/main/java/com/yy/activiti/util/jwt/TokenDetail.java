package com.yy.activiti.util.jwt;

/**
 * @author YY
 * @date 2020/1/17
 * @description 不直接使用 username 做参数的原因是可以方便未来增加其他要封装到 token 中的信息
 */
public interface TokenDetail {
    /**
     * 获取用户名
     * @return
     */
    String getUsername();
}
