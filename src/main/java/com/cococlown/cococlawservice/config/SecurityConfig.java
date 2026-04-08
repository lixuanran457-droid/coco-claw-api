package com.cococlown.cococlawservice.config;

import com.cococlown.cococlawservice.filter.AdminJwtAuthenticationFilter;
import com.cococlown.cococlawservice.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 配置
 * 前后端分离项目，禁用CSRF，使用JWT无状态认证
 * Token存储在httpOnly Cookie中，防止XSS攻击
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private AdminJwtAuthenticationFilter adminJwtAuthenticationFilter;

    /**
     * 密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全过滤器链
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（前后端分离项目使用JWT，不需要CSRF）
            .csrf().disable()
            
            // 配置CORS
            .cors().and()
            
            // 配置会话管理为无状态（使用JWT）
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            
            // 添加JWT认证过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(adminJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 配置请求授权
            .authorizeRequests()
                // 公开接口：认证相关接口、Swagger文档、静态资源
                .antMatchers(
                    "/api/auth/**",
                    "/api/skills/**",
                    "/api/skill/**",
                    "/api/categories/**",
                    "/api/category/**",
                    "/api/config/**",
                    "/api/recommend/**",
                    "/api/payment/**",
                    "/api/orders/guest/**",
                    "/swagger-ui/**",
                    "/v2/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/favicon.ico",
                    "/api/dashboard/stats",
                    "/api/dashboard/order-trend",
                    "/api/dashboard/user-growth",
                    "/api/dashboard/top-skills",
                    "/api/dashboard/recent-orders"
                ).permitAll()
                
                // 后台管理接口需要认证
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .antMatchers("/api/dashboard/**").hasRole("ADMIN")
                
                // 其他请求需要认证
                .anyRequest().authenticated()
            .and()
            
            // 禁用默认登录页
            .httpBasic().disable()
            .formLogin().disable();
            
        return http.build();
    }

    /**
     * CORS配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*")); // 允许所有来源，生产环境应限制
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true); // 允许携带Cookie
        configuration.setMaxAge(3600L); // 预检请求缓存1小时

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
