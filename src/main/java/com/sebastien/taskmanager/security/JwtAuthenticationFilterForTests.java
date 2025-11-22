package com.sebastien.taskmanager.security;

import com.sebastien.taskmanager.service.CustomUserDetailService;
import com.sebastien.taskmanager.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component("jwtAuthenticationFilter")
@Profile("test")
public class JwtAuthenticationFilterForTests extends OncePerRequestFilter {

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final CustomUserDetailService customUserDetailService;

    public JwtAuthenticationFilterForTests(JwtService jwtService, CustomUserDetailService customUserDetailService) {
        this.jwtService = jwtService;
        this.customUserDetailService = customUserDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken("test@example.com",
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.startsWith("/auth/")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs/");
    }
}
