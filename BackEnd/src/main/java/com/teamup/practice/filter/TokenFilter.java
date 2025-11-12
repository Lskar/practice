package com.teamup.practice.filter;



import com.teamup.practice.config.AuthProperties;
import com.teamup.practice.utils.JwtTool;
import com.teamup.practice.utils.UserContext;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
@WebFilter(urlPatterns = "/*")
public class TokenFilter implements Filter {

    private final JwtTool jwtTool;
    private final AuthProperties authProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();

        // 移除上下文路径前缀以正确匹配
        if (contextPath != null && !contextPath.equals("/") && requestURI.startsWith(contextPath)) {
            requestURI = requestURI.substring(contextPath.length());
        }

        // 检查是否为排除路径
        if (isExclude(requestURI)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 从请求头获取token
        String token = extractToken(request);

        try {
            // 解析token获取用户ID
            Long userId = jwtTool.parseToken(token);
            // 设置用户上下文
            UserContext.setUser(userId);
        } catch (RuntimeException e) {
            log.warn("Token验证失败: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (Exception e) {
            log.error("Token解析异常", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            // 清理用户上下文
            UserContext.removeUser();
        }
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }

    private boolean isExclude(String requestURI) {
        if (authProperties.getExcludePaths() != null) {
            for (String pattern : authProperties.getExcludePaths()) {
                if (antPathMatcher.match(pattern, requestURI)) {
                    return true;
                }
            }
        }
        return false;
    }
}
