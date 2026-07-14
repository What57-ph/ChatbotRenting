package com.chatbot_renting.subscriptionservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.List;
import java.util.Optional;

/**
 * Tiện ích tĩnh để parse JWT token và trích xuất các thông tin quan trọng.
 *
 * Khác với {@link SecurityContextUtil} (dựa vào Spring bean + HttpServletRequest),
 * class này hoạt động độc lập — chỉ cần chuỗi token thô hoặc Authorization header,
 * phù hợp cho các context ngoài Spring bean (filter, interceptor, unit test).
 *
 * Cách dùng điển hình:
 * <pre>
 *   String bearer = request.getHeader("Authorization");
 *   Optional&lt;Long&gt; userId  = JwtTokenUtils.extractUserId(bearer, secret);
 *   Optional&lt;String&gt; username = JwtTokenUtils.extractUsername(bearer, secret);
 *   Optional&lt;List&lt;String&gt;&gt; roles = JwtTokenUtils.extractRoles(bearer, secret);
 * </pre>
 */
@Slf4j
public final class JwtTokenUtils {

    // Claim keys — phải khớp với authservice JwtUtil
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_ROLES   = "roles";

    private JwtTokenUtils() {
        // Utility class — không cho instantiate
    }

    // ─────────────────────── userId ─────────────────────────────────────────

    /**
     * Trích xuất userId từ Authorization header (Bearer &lt;token&gt;).
     *
     * @param authorizationHeader giá trị của header "Authorization"
     * @param base64Secret        secret key mã hoá Base64 (khớp với authservice)
     * @return Optional&lt;Long&gt; userId hoặc empty nếu token không hợp lệ
     */
    public static Optional<Long> extractUserId(String authorizationHeader, String base64Secret) {
        return extractClaim(authorizationHeader, base64Secret, CLAIM_USER_ID, Object.class)
                .map(obj -> {
                    if (obj instanceof Number num) return num.longValue();
                    try {
                        return Long.parseLong(obj.toString());
                    } catch (NumberFormatException e) {
                        log.warn("Cannot parse userId claim to Long: {}", obj);
                        return null;
                    }
                });
    }

    // ─────────────────────── username (subject) ─────────────────────────────

    /**
     * Trích xuất username (JWT subject) từ Authorization header.
     */
    public static Optional<String> extractUsername(String authorizationHeader, String base64Secret) {
        return parseClaimsFromHeader(authorizationHeader, base64Secret)
                .map(Claims::getSubject);
    }

    // ─────────────────────── roles ──────────────────────────────────────────

    /**
     * Trích xuất danh sách roles từ JWT claim "roles".
     */
    @SuppressWarnings("unchecked")
    public static Optional<List<String>> extractRoles(String authorizationHeader, String base64Secret) {
        return extractClaim(authorizationHeader, base64Secret, CLAIM_ROLES, List.class)
                .map(list -> (List<String>) list);
    }

    // ─────────────────────── generic claim ──────────────────────────────────

    /**
     * Trích xuất bất kỳ claim nào theo key và type.
     *
     * @param authorizationHeader Authorization header ("Bearer xxx")
     * @param base64Secret        secret
     * @param claimKey            tên claim (ví dụ: "email", "tenantId")
     * @param claimType           kiểu trả về
     * @param <T>                 generic type
     * @return Optional chứa giá trị claim
     */
    public static <T> Optional<T> extractClaim(
            String authorizationHeader,
            String base64Secret,
            String claimKey,
            Class<T> claimType
    ) {
        return parseClaimsFromHeader(authorizationHeader, base64Secret)
                .map(claims -> claims.get(claimKey, claimType));
    }

    // ─────────────────────── validation ─────────────────────────────────────

    /**
     * Kiểm tra token trong Authorization header có còn hợp lệ không
     * (chữ ký đúng và chưa hết hạn).
     */
    public static boolean isTokenValid(String authorizationHeader, String base64Secret) {
        return parseClaimsFromHeader(authorizationHeader, base64Secret).isPresent();
    }

    // ─────────────────────── Private helpers ────────────────────────────────

    private static Optional<Claims> parseClaimsFromHeader(String authorizationHeader, String base64Secret) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }
        String token = authorizationHeader.substring(7).trim();
        return parseClaims(token, base64Secret);
    }

    private static Optional<Claims> parseClaims(String token, String base64Secret) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(buildSigningKey(base64Secret))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Optional.ofNullable(claims);
        } catch (Exception e) {
            log.warn("Failed to parse JWT token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private static Key buildSigningKey(String base64Secret) {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
