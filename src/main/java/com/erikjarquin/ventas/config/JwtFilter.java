package com.erikjarquin.ventas.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.erikjarquin.ventas.config.security.SecurityAuthorityMapper;
import com.erikjarquin.ventas.model.entity.UserEntity;
import com.erikjarquin.ventas.repository.UserRepository;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final SecurityAuthorityMapper authorityMapper;

    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepository, SecurityAuthorityMapper authorityMapper){
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.authorityMapper=authorityMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);

            if(jwtUtil.isTokenValid(token)){
                if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
                    UserEntity user = userRepository.findByEmailWithRoleAndPermissions(email).orElse(null);

                    if(user != null){
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, authorityMapper.mapAuthorities(user));

                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch(ExpiredJwtException e) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
