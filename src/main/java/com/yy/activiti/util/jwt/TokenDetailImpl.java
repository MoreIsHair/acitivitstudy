package com.yy.activiti.util.jwt;

/**
 * @author YY
 * @date 2020/1/17
 * @description
 */
public class TokenDetailImpl implements TokenDetail {
    private final String username;

    public TokenDetailImpl(String username) {
        this.username = username;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
