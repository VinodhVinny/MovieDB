package org.ragnarok.MovieDB.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private Long jwtExpirationTime;

    public String generateJwtToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Map<String, String> claims = new HashMap<>();

        claims.put("role", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","))
        );

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationTime)))
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, base64EncodedKey())
                .compact();
    }

    public boolean isValidToken(String token, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Claims claims = getAllClaimsFromToken(token);

        return user.getUsername().equals(getUserNameFromToken(token)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Claims claims = getAllClaimsFromToken(token);

        return claims.getExpiration().before(new Date());
    }

    private String getUserNameFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);

        return claims.getSubject();
    }

    private Claims getAllClaimsFromToken(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(base64EncodedKey())
                .build()
                .parseClaimsJwt(token)
                .getBody();
    }

    private byte[] base64EncodedKey() {
        return Base64.getEncoder().encode(secretKey.getBytes());
    }
}
