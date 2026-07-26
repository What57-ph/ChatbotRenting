package com.chatbot_renting.coreservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

import java.security.Key;
import java.util.Optional;
import java.util.function.Function;

/**
 * Base class cung cấp cơ chế đọc JWT token từ Authorization header của
 * HttpServletRequest và parse claim dưới dạng type-safe Optional.
 *
 * Các subclass (ví dụ: SecurityUtils) extend class này để expose
 * các helper method cụ thể theo domain (getUserId, getUsername, ...).
 */
@Slf4j
public abstract class SecurityContextUtil {

    @Value("${jwt.base64-secret}")
    private String base64Secret;

    private final HttpServletRequest request;

    protected SecurityContextUtil(HttpServletRequest request) {
        this.request = request;
    }

    // ─────────────────────── Protected API ───────────────────────────────

    /**
     * Lấy một claim từ JWT token trong Authorization header.
     *
     * @param claimKey  tên claim cần lấy (ví dụ: "userId", "username")
     * @param claimType kiểu dữ liệu của claim
     * @return Optional chứa giá trị claim, hoặc empty nếu không tồn tại / lỗi
     */
    protected <T> Optional<T> getUserTokenClaim(String claimKey, Class<T> claimType) {
        try {
            Claims claims = parseClaimsFromRequest();
            if (claims == null) return Optional.empty();
            T value = claims.get(claimKey, claimType);
            return Optional.ofNullable(value);
        } catch (Exception e) {
            log.warn("Cannot extract claim '{}' from JWT: {}", claimKey, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Lấy subject (username) từ JWT token — đây là claim tiêu chuẩn.
     */
    protected Optional<String> getSubjectFromToken() {
        return extractClaim(Claims::getSubject);
    }

    // ─────────────────────── Private helpers ──────────────────────────────

    private Claims parseClaimsFromRequest() {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No Bearer token found in Authorization header");
            return null;
        }
        String token = authHeader.substring(7).trim();
        return parseClaims(token);
    }

    private <T> Optional<T> extractClaim(Function<Claims, T> resolver) {
        try {
            Claims claims = parseClaimsFromRequest();
            if (claims == null) return Optional.empty();
            return Optional.ofNullable(resolver.apply(claims));
        } catch (Exception e) {
            log.warn("Cannot extract claim from JWT: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
