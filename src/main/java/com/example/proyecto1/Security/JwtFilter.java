package com.example.proyecto1.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/register") ||
                (path.equals("/api/empresas") && request.getMethod().equals("POST")) ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (jwtUtil.validateToken(token)) {
                    String userJson = jwtUtil.extractEmail(token);
                    String role = jwtUtil.extractRole(token);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userJson, token, Collections.singletonList(new SimpleGrantedAuthority(role)));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    System.out.println("Token inválido o expirado para request: " + path);
                }
            } catch (Exception e) {
                System.out.println("Excepción en JwtFilter: " + e.getMessage());
            }
        } else {
            if(!request.getMethod().equals("OPTIONS")) {
                System.out.println("Header Authorization faltante o incorrecto en: " + path);
            }
        }

        filterChain.doFilter(request, response);
    }
}