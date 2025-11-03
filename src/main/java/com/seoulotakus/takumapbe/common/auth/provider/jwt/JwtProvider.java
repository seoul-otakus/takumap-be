package com.seoulotakus.takumapbe.common.auth.provider.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidity;

//    @PostConstruct
//    public void init() {
//        System.out.println("====================================================");
//        System.out.println(">> [INIT] Secret Key (first 5 chars): " + (secretKey != null && secretKey.length() > 5 ? secretKey.substring(0, 5) : "null or too short"));
//        System.out.println(">> [INIT] Access Token Validity (seconds): " + accessTokenValidity);
//        System.out.println("====================================================");
//    }

    // Access Token 리턴
    public String createAccessToken(String userId){
        return create(userId, accessTokenValidity);
    }

    // Refresh Token 리턴
    public String createRefreshToken(String userId){
        return create(userId, refreshTokenValidity);
    }

    // Access Token과 Refresh Token 생성
    private String create(String userId, long validityInSeconds){
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInSeconds * 1000);
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(validity)
                .compact();
    }

    // jwt 검증하는 메소드
    public String validate(String jwt){
        String subject = null;
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            subject = claims.getSubject();

        }catch (ExpiredJwtException e) {
            System.out.println("!!! Exception Message: " + e.getMessage());
            throw e; // Re-throw to be caught by GlobalExceptionHandler
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            System.out.println("!!! Exception Type: " + e.getClass().getName());
            System.out.println("!!! Exception Message: " + e.getMessage());
            throw e; // Re-throw to be caught by GlobalExceptionHandler
        } catch (Exception e){ // Catch any other unexpected exceptions
            System.out.println("!!! Exception Type: " + e.getClass().getName());
            System.out.println("!!! Exception Message: " + e.getMessage());
            throw e; // Re-throw
        }

        return subject;
    }
}

