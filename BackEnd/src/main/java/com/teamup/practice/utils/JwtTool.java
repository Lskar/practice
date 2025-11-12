// JwtTool.java
package com.teamup.practice.utils;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtTool {

    private final JWTSigner jwtSigner;

    public JwtTool(KeyPair keyPair) {
        this.jwtSigner = JWTSignerUtil.createSigner("rs256", keyPair);
    }

    /**
     * 创建 access-token
     */
    public String createToken(Long userId, Duration ttl) {
        return JWT.create()
                .setPayload("user", userId)
                .setExpiresAt(new Date(System.currentTimeMillis() + ttl.toMillis()))
                .setSigner(jwtSigner)
                .sign();
    }

    /**
     * 解析token
     */
    public Long parseToken(String token) {
        // 1.校验token是否为空
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("未登录");
        }

        try {
            // 2.校验并解析jwt
            JWT jwt = JWT.of(token);
            if (!jwt.setSigner(jwtSigner).verify()) {
                throw new RuntimeException("无效的token");
            }

            // 3.校验是否过期
            JWTValidator.of(jwt).validateDate();

            // 4.数据格式校验
            Object userPayload = jwt.getPayload("user");
            if (userPayload == null) {
                throw new RuntimeException("无效的token");
            }
            // 5.数据解析
            return Long.valueOf(userPayload.toString());
        } catch (ValidateException e) {
            throw new RuntimeException("token已经过期");
        } catch (NumberFormatException e) {
            throw new RuntimeException("无效的token");
        } catch (Exception e) {
            throw new RuntimeException("无效的token");
        }
    }
}
