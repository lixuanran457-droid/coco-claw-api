package com.cococlown.cococlawservice.filter;

import com.cococlown.cococlawservice.entity.SysAdmin;
import com.cococlown.cococlawservice.service.AdminAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Cookie;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * 管理员JWT认证过滤器
 */
@Component
public class AdminJwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private AdminAuthService adminAuthService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 只处理管理员相关路径
        if (!path.startsWith("/api/admin")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 获取Token
        String token = getTokenFromCookie(request);

        if (token != null) {
            try {
                SysAdmin admin = adminAuthService.verifyToken("Admin " + token);
                if (admin != null) {
                    // 设置认证信息
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            admin,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // Token无效，继续处理（Spring Security会处理）
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("admin_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
