//package com.seoulotakus.takumapbe.domain.auth.jwt;
//
//import com.nimbusds.jwt.JWT;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.oauth2.jwt.JwtClaimsSet;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.spec.SecretKeySpec;
//import java.security.Key;
//import java.util.Base64;
//import java.util.Date;
//import java.util.stream.Collectors;
//
//@Component
//public class TokenProvider implements InitializingBean {
//
//    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
//
//    private static final String AUTHORITIES_KEY = "auth";
//
//    private final String secret;
//    private final long tokenValidityInMilliseconds;
//
//    private Key key;
//
//    public TokenProvider(@Value("${jwt.secret}") String secret,
//                         @Value("${jwt.token-validity-in-seconds}") long tokenValidityInMilliseconds) {
//        this.secret = secret;
//        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds;
//    }
//
//    @Override
//    public void afterPropertiesSet(){
//        byte[] keyBytes = Base64.getDecoder().decode(secret);
//        this.key = new SecretKeySpec(keyBytes, AUTHORITIES_KEY);
//    }
//
//    public String createToken(Authentication authentication){
//        String authorities = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(","));
//
//        long now = (new Date()).getTime();
//        Date validity = new Date(now + tokenValidityInMilliseconds);
//
//        return JwtClaimsSet.builder()
//                .subject(authentication.getName())
//                .claim(AUTHORITIES_KEY, authorities)
//                .expiresAt(validity)
//                .compact();
//    }
//}
