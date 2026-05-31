package com.lecture_mind.authservice.config;

import com.lecture_mind.authservice.model.User;
import com.lecture_mind.authservice.repository.UserRepository;
import io.jsonwebtoken.*;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.base64-secret}")
    private String secret;

    private final UserRepository userRepository;

    private Key getSecretKey(){
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public String extractUsername(String token){return extractClaim(token, Claims::getSubject);}
    public Long extractUserId(String token){
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }
    public List<?> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }

    public Boolean isTokenValid(String token){
        Claims claims = extractAllClaims(token);
        return !claims.getExpiration().before(new Date());
    }

    public boolean validateToken(String token) {
        log.debug(">>>>Token when validate: "+token);
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String generateToken(UserDetails userDetails, Date expiration){

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow();

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .claim("userId", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(getSecretKey())
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver){
        return resolver.apply(
                Jwts.parserBuilder()
                        .setSigningKey(getSecretKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
        );
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
