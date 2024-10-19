package com.shinhan.VRRS.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    private static String SECRET_KEY;

    private static final long JWT_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 7일 유효

    @PostConstruct
    public void init() {
        SECRET_KEY = secretKey;
    }

    // SecretKey 객체로 변환
    private static SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 사용자명 추출
    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Claim 추출
    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 모든 Claim 추출
    private static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    // 사용자명으로 토큰 생성
    public static String generateToken(String username) {
        return Jwts.builder()
                   .setSubject(username)
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME))
                   .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                   .compact();
    }

    // 토큰 유효성 검사
    public static Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // 토큰 만료 여부 확인
    private static Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 토큰 만료 기간 추출
    private static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}