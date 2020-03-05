package com.yy.activiti.util.jwt;

import com.yy.activiti.config.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YY
 * @date 2020/1/17
 * @description
 */
@Slf4j
public class JwtUtil {

    public static final String SECRET = "123";

    public static final Long EXPIRATION = 1000 * 60 * 60 * 2L;

    public static final String TOKENHEADER = "";

    /**
     * 根据 TokenDetail 生成 Token
     *
     * @param tokenDetail
     * @return
     */
    public  static String generateToken(TokenDetail tokenDetail) {
        Map<String, Object> claims = new HashMap<>(16);
        claims.put("sub", tokenDetail.getUsername());
        claims.put("created", generateCurrentDate());
        return generateToken(claims);
    }

    /**
     * 根据 claims 生成 Token
     *
     * @param claims
     * @return
     */
    private static String generateToken(Map<String, Object> claims) {
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(generateExpirationDate())
                    .signWith(SignatureAlgorithm.HS512, JwtUtil.SECRET.getBytes("UTF-8"))
                    .compact();
        } catch (UnsupportedEncodingException ex) {
            //didn't want to have this method throw the exception, would rather log it and sign the token like it was before
            log.warn(ex.getMessage());
            return Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(generateExpirationDate())
                    .signWith(SignatureAlgorithm.HS512, JwtUtil.SECRET)
                    .compact();
        }
    }

    /**
     * token 过期时间
     *
     * @return
     */
    private static Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + JwtUtil.EXPIRATION * 1000);
    }

    /**
     * 获得当前时间
     *
     * @return
     */
    private static Date generateCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * 从 token 中拿到 username
     *
     * @param token
     * @return
     */
    public static String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 解析 token 的主体 Claims
     *
     * @param token
     * @return
     */
    private static Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(JwtUtil.SECRET.getBytes("UTF-8"))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }
    /**
     * 检查 token 是否处于有效期内
     * @param token
     * @param userDetails
     * @return
     */
    public static Boolean validateToken(String token, UserDetails userDetails) {
        CustomUserDetails user = (CustomUserDetails) userDetails;
        final String username = getUsernameFromToken(token);
        final Date created = getCreatedDateFromToken(token);
        return (username.equals(user.getUsername()) && !(isTokenExpired(token)));
    }

    /**
     * 获得我们封装在 token 中的 token 创建时间
     * @param token
     * @return
     */
    public static Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token);
            created = new Date((Long) claims.get("created"));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    /**
     * 获得我们封装在 token 中的 token 过期时间
     * @param token
     * @return
     */
    public static Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    /**
     * 检查当前时间是否在封装在 token 中的过期时间之后，若是，则判定为 token 过期
     * @param token
     * @return
     */
    private static Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(generateCurrentDate());
    }

    /**
     * 检查 token 是否是在最后一次修改密码之前创建的（账号修改密码之后之前生成的 token 即使没过期也判断为无效）
     * @param created
     * @param lastPasswordReset
     * @return
     */
    private static Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

}
