package com.cjvisions.tradefx_backend.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class JwtTokenGeneratorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null){
            SecretKey key = Keys.hmacShaKeyFor("secretsecretsecretsecretsecret12345678".getBytes(StandardCharsets.UTF_8));
            String jwt = Jwts.builder().setIssuer("TraderFX").setSubject("JWT token")
                    .claim("username", authentication.getName())
                    .claim("authorities", populateAuthorities(authentication.getAuthorities()))
                    .setIssuedAt(new Date())
                    .setExpiration(new Date((new Date()).getTime() + 3000000))
                    .signWith(key).compact();
            response.setHeader("Authorization",jwt);


        }

        filterChain.doFilter(request, response);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/test");
    }

    public String populateAuthorities(Collection<? extends GrantedAuthority> collection){
        Set<String> authorities = new HashSet<>();
        for (GrantedAuthority authority: collection){
            authorities.add(authority.getAuthority());
        }
        return String.join(",", authorities);
    }
};
