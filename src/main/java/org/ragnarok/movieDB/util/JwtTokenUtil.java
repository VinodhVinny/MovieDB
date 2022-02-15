package org.ragnarok.movieDB.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Data
public class JwtTokenUtil {

    @Value("${movieDB.jwt.expiration-time}")
    private Long jwtExpirationTime;

    @Value("${movieDB.jwt.refresh-token.expiration-time}")
    private Long refreshTokenExpirationTime;

    private Key key;

    @PostConstruct
    public void init() {
        key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String generateJwtToken(UserDetails userDetails) {
        Map<String, String> claims = new HashMap<>();

        claims.put("role", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","))
        );

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String username) {

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                .signWith(key)
                .compact();
    }

    private Claims getAllClaimsFromToken(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUserNameFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);

        return claims.getSubject();
    }

    public boolean isValidToken(String token) {
        if (!StringUtils.hasText(token)) return false;

        Jwts.parserBuilder()
                .setSigningKey(key)
                .build();

        return true;
    }
}
