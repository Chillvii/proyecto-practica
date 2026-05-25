package com.example.proyectopractica.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        final String header = request.getHeader(AUTHORIZATION_HEADER);
        String username = null;
        String jwt = null;

        if (Objects.nonNull(header) && header.startsWith(BEARER_PREFIX)) {
            jwt = header.substring(7);
            try {
                username = jwtService.extractUsername(jwt);
            } catch (IllegalArgumentException e) {
                log.error("No se pudo obtener el token: {}", e.getMessage());
            } catch (ExpiredJwtException e) {
                log.warn("Token expirado: {}", e.getMessage());
            }
        }

        if (Objects.nonNull(username) &&
                Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
            final var userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isValid(jwt)) {
                var auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        chain.doFilter(request, response);
    }
}