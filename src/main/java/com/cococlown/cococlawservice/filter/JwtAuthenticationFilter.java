package com.cococlown.cococlawservice.filter;

import com.cococlown.cococlawservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器
 * 从Cookie中读取JWT Token进行认证
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String TOKEN_COOKIE_NAME = "auth_token";
    public static final String TOKEN_HEADER_NAME = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 尝试从Cookie获取Token
        String token = extractTokenFromCookie(request);

        // 2. 如果Cookie没有，尝试从Header获取（兼容旧版）
        if (token == null) {
            token = extractTokenFromHeader(request);
        }

        // 3. 验证Token并设置认证信息
        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            // 检查Token是否在黑名单（已登出）
            String blacklistKey = "token:blacklist:" + token;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey))) {
                // Token已失效，清除上下文
                SecurityContextHolder.clearContext();
            } else {
                // Token有效，设置认证信息
                try {
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    String email = jwtUtil.getUsernameFromToken(token);

                    // 创建认证令牌
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                        );

                    // 设置额外信息
                    authentication.setDetails(email);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    // Token解析失败，清除上下文
                    SecurityContextHolder.clearContext();
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从Cookie中提取Token
     */
    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    String token = cookie.getValue();
                    // 清理可能的空白字符
                    if (token != null) {
                        token = token.trim();
                    }
                    return token;
                }
            }
        }
        return null;
    }

    /**
     * 从Header中提取Token（兼容旧版）
     */
    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER_NAME);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // 公开接口不需要过滤
        return path.startsWith("/api/auth/") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v2/api-docs") ||
               path.startsWith("/swagger-resources") ||
               path.startsWith("/webjars/") ||
               path.equals("/favicon.ico");
    }
}
