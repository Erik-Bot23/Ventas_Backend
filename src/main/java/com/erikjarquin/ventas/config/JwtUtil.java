package com.erikjarquin.ventas.config;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.erikjarquin.ventas.model.entity.UserEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "1234567890123456789012345678901234567890123456789012345678901234";

    public String generateToken(UserEntity user){
        return Jwts.builder().setSubject(user.getEmail())
                .claim("role", user.getRole().getName())
                .setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 *5))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256).compact();
    }

    public String extractEmail(String token){
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token){
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY.getBytes())
                .build().parseClaimsJws(token).getBody();
    }
}
