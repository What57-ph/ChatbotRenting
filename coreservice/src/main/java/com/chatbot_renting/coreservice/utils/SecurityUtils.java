package com.chatbot_renting.coreservice.utils;

import com.chatbot_renting.commonservice.exception.AppError;
import com.chatbot_renting.commonservice.exception.AppUnauthorizedException;
import com.chatbot_renting.commonservice.exception.code.CommonErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Bean cụ thể cho subscriptionservice, extend SecurityContextUtil.
 * Expose các helper method để lấy userId, username từ JWT token
 * trong Authorization header — không cần X-User-Id header nữa.
 *
 * Cách dùng trong Controller:
 * <pre>
 *   Long userId = securityUtils.getUserIdOrElseThrow();
 *   Optional&lt;String&gt; username = securityUtils.getUsername();
 * </pre>
 */
@Slf4j
@Component
public class SecurityUtils extends SecurityContextUtil {

    // Tên các claims khớp với authservice JwtUtil khi tạo token
    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_ROLES   = "roles";

    public SecurityUtils(HttpServletRequest request) {
        super(request);
    }

    // ──────────────────────────── userId ────────────────────────────────

    /**
     * Lấy userId từ JWT claim "userId".
     * Claim trong authservice được set dưới dạng Long/Integer (Number).
     */
    public Optional<UUID> getUserId() {
        return getUserTokenClaim(CLAIM_USER_ID, Object.class)
                .map(obj -> {
                    try { return UUID.fromString(obj.toString()); }
                    catch (IllegalArgumentException e) { return null; }
                });
    }

    /**
     * Lấy userId hoặc throw 401 nếu không có token hợp lệ.
     */
    public UUID getUserIdOrElseThrow() {
        return getUserId().orElseThrow(() ->
                new AppUnauthorizedException(new AppError(CommonErrorCode.UNAUTHORIZED)));
    }

    // ──────────────────────────── username ──────────────────────────────

    /**
     * Lấy username từ JWT subject (standard claim).
     */
    public Optional<String> getUsername() {
        return getSubjectFromToken();
    }

    /**
     * Lấy username hoặc throw 401.
     */
    public String getUsernameOrElseThrow() {
        return getUsername().orElseThrow(() ->
                new AppUnauthorizedException(new AppError(CommonErrorCode.UNAUTHORIZED)));
    }

    // ──────────────────────────── roles ─────────────────────────────────

    /**
     * Lấy danh sách roles từ JWT claim "roles".
     */
    @SuppressWarnings("unchecked")
    public Optional<List<String>> getRoles() {
        return getUserTokenClaim(CLAIM_ROLES, List.class)
                .map(list -> (List<String>) list);
    }
}
