package com.bms.bms_app.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bms.bms_app.repository.UserRepository;


import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }


    public void doFilterInternal(HttpServletRequest request, 
                                 HttpServletResponse response, 
                                 FilterChain filterChain) 
                                throws IOException, ServletException {
        
        log.debug("Incoming request: {} {}", request.getMethod(), request.getRequestURI());

        // Skip JWT filter for /admin/** paths
        // String requestURI = request.getRequestURI();
        // if (requestURI.startsWith("/admin/")) {
        //     log.debug("Skipping JWT filter for admin endpoint: {}", requestURI);
        //     filterChain.doFilter(request, response);
        //     return;
        // }

        String token = null;
        String email = null;

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            try {
                email = jwtUtil.extractEmail(token);
                log.debug("Found bearer token. extracted email={}", email);
            } catch (io.jsonwebtoken.JwtException e) {
                log.warn("JWT parse/validation failed: {}", e.getMessage());
            }
        } else {
            log.debug("No bearer token found in Authorization header");
        }
        
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails user = userDetailsService.loadUserByUsername(email);

            try {
                if (user != null && jwtUtil.validateToken(token)) {
                    log.debug("JWT validated, setting authentication for email={}", email);

                    UsernamePasswordAuthenticationToken authToken =
                         new UsernamePasswordAuthenticationToken(
                                user, 
                                null, 
                                user.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    log.warn("JWT validation failed for email={}", email);
                }
            } catch (io.jsonwebtoken.JwtException e) {
                log.warn("JWT validation failed for email={} : {}", email, e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }

}
