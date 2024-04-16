package com.zhongqin.commons.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

/**
 * @author Kevin
 * @date 2023/3/30 13:16
 */
public class JwtUtil {

    /**
     * 签发人
     */
    private final static String WITH_ISSUER = "Kevin";

    /**
     * JWT加密盐值
     */
    private final static String JWT_SALT = "JAVA_SASS";

    /**
     * 解析Token
     *
     * @param token
     * @return
     */
    public static DecodedJWT parseToken(String token) {
        return JWT.decode(token);
    }

    /**
     * 获得token中的载荷信息无需secret解密也能获得
     *
     * @param token
     * @param key
     * @return
     */
    public static String getClaimVal(String token, String key) {
        DecodedJWT jwt = parseToken(token);
        return jwt.getClaim(key).asString();
    }

    /**
     * 效验Token是否已经失效And是否正确
     *
     * @param token
     * @return
     */
    public static boolean verifyToken(String token) {
        try {
            Algorithm algorithm = getAlgorithm();
            JWTVerifier jwtVerifier = JWT.require(algorithm).withIssuer(WITH_ISSUER).build();
            jwtVerifier.verify(token);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * 效验Token是否正确
     *
     * @param token
     * @return
     */
    public static boolean verifyTokenCorrectness(String token) {
        try {
            Algorithm algorithm = getAlgorithm();
            JWTVerifier jwtVerifier = JWT.require(algorithm).withIssuer(WITH_ISSUER).build();
            jwtVerifier.verify(token);
        } catch (TokenExpiredException tokenExpiredException) {
            return true;
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * 生成Token
     *
     * @param object        数据对象
     * @param withExpiresAt 过期时间
     * @return
     */
    public static String createToken(Object object, Date withExpiresAt) {
        Algorithm algorithm = getAlgorithm();
        JWTCreator.Builder builder = JWT.create();
        builder.withSubject(JsonTools.objectToJson(object));
        builder.withIssuer(WITH_ISSUER);
        return builder.withExpiresAt(withExpiresAt).sign(algorithm);
    }

    /**
     * 加密方式
     *
     * @return
     */
    private static Algorithm getAlgorithm() {
        return Algorithm.HMAC256(JWT_SALT);
    }


}
