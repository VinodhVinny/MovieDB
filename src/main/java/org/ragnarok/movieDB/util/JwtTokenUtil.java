package org.ragnarok.movieDB.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {

    @Value("${jwt.expiration-time}")
    private Long jwtExpirationTime;

    private Key key;

    @PostConstruct
    public void init() {
        key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String generateJwtToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Map<String, String> claims = new HashMap<>();

        claims.put("role", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","))
        );

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
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

    public String getRole(String token) {
        Claims claims = getAllClaimsFromToken(token);

        return claims.get("role", String.class);
    }

    public boolean isValidToken(String token) {
        return StringUtils.hasText(token) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Claims claims = getAllClaimsFromToken(token);

        return claims.getExpiration().before(new Date());
    }
}
