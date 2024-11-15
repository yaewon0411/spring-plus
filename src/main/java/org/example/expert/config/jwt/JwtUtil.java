package org.example.expert.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.config.security.loginuser.LoginUser;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    public static final String BEARER_PREFIX = "Bearer ";

    public static final String HEADER = "Authorization";
    private static final long TOKEN_TIME = 1000 * 60 * 60 * 24 * 7; // 일주일

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createToken(User user) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(String.valueOf(user.getId()))
                        .claim("email", user.getEmail())
                        .claim("userRole", user.getUserRole())
                        .claim("nickname",user.getNickname())
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME))
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }

    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        throw new JwtException("JWT이 존재하지 않습니다");
    }

    //TODO 얘는 지울 거
    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            throw new JwtException("잘못된 JWT 서명입니다");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());
            throw new JwtException("만료된 JWT 토큰입니다");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
            throw new JwtException("지원되지 않는 JWT 토큰입니다");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            throw new JwtException("JWT 토큰이 비어있습니다");
        } catch (Exception e) {
            log.error("JWT token error: {}", e.getMessage());
            throw new JwtException("JWT 토큰 오류가 발생했습니다");
        }
    }


    public LoginUser validateToken (String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Long userId = Long.parseLong(claims.getSubject());
            String email = claims.get("email", String.class);
            UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));
            String nickname = claims.get("nickname", String.class);

            AuthUser authUser = new AuthUser(userId, email, userRole, nickname);
            return new LoginUser(User.fromAuthUser(authUser));
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            throw new JwtException("잘못된 JWT 서명입니다");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());
            throw new JwtException("만료된 JWT 토큰입니다");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
            throw new JwtException("지원되지 않는 JWT 토큰입니다");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            throw new JwtException("JWT 토큰이 비어있습니다");
        } catch (Exception e) {
            log.error("JWT token error: {}", e.getMessage());
            throw new JwtException("JWT 토큰 오류가 발생했습니다");
        }
    }
}
