package com.cjvisions.tradefx_backend.utils;

import com.cjvisions.tradefx_backend.domain.models.UserLoginInfo;
import com.cjvisions.tradefx_backend.domain.models.UserRegistrationInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private static final long TOKEN_DURATION = 24 * 60 * 60 * 1000;

    public boolean validateAccessToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor("secretsecretsecretsecretsecret12345678".getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            System.out.println("JWT expired" + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.out.println("Token is null, empty or only whitespace" + ex.getMessage());
        } catch (MalformedJwtException ex) {
            System.out.println("JWT is invalid" + ex);
        } catch (UnsupportedJwtException ex) {
            System.out.println("JWT is not supported" + ex);
        } catch (SignatureException ex) {
            System.out.println("Signature validation failed");
        }

        return false;
    }

    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor("secretsecretsecretsecretsecret12345678".getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateAccessToken(UserRegistrationInfo user){
        return Jwts.builder()
                .setSubject(user.getId() + ", " + user.getEmail())
                .setIssuer("cj-visions")
//                .claim("roles", user.getRoles().toString())
                .claim("roles", List.of())
                .setIssuedAt(new Date())
                .setExpiration(new Date(TOKEN_DURATION + System.currentTimeMillis()))
                .signWith(Keys.hmacShaKeyFor("secretsecretsecretsecretsecret12345678".getBytes(StandardCharsets.UTF_8)),SignatureAlgorithm.HS256)
                .compact();
    }
}
