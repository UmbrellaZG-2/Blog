package com.website.backend.common.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.website.backend.common.security.JwtTokenProvider;
import com.website.backend.user.entity.User;
import com.website.backend.user.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.website.backend.common.config.SecurityPathConfig;
import org.springframework.util.AntPathMatcher;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        log.info("JWT过滤器处理请求: {} {}", request.getMethod(), requestURI);
        
        // 获取JWT令牌
        String token = getTokenFromRequest(request);
        
        if (token != null) {
            log.info("找到JWT令牌，开始验证");
            try {
                // 验证JWT令牌
                String username = jwtTokenProvider.getUserNameFromJwtToken(token);
                log.info("从JWT令牌中提取用户名: {}", username);
                
                // 检查用户是否存在于数据库中
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (userDetails != null) {
                    log.info("在数据库中找到用户: {}", username);
                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("设置用户认证信息: {}", username);
                } else {
                    log.warn("在数据库中未找到用户: {}", username);
                }
            } catch (ExpiredJwtException e) {
                log.warn("JWT令牌已过期: {}", e.getMessage());
            } catch (MalformedJwtException e) {
                log.warn("JWT令牌格式错误: {}", e.getMessage());
            } catch (SignatureException e) {
                log.warn("JWT签名验证失败: {}", e.getMessage());
            } catch (UnsupportedJwtException e) {
                log.warn("不支持的JWT令牌: {}", e.getMessage());
            } catch (Exception e) {
                log.error("处理JWT令牌时发生未知错误: {}", e.getMessage(), e);
            }
        } else {
            log.info("未找到JWT令牌");
            
            // 对于公开路径，设置一个有效的认证以避免AuthorizationFilter拒绝访问
            if (isPublicPath(request)) {
                log.info("为公开路径设置有效认证");
                UsernamePasswordAuthenticationToken publicAuth = new UsernamePasswordAuthenticationToken(
                    "publicUser", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(publicAuth);
                log.info("已为公开路径设置认证，继续执行过滤器链");
            }
        }
        
        // 无论是否找到JWT令牌，都继续执行过滤器链
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    private boolean isPublicPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        log.info("检查路径是否为公开路径: {}", path);
        
        // 检查是否匹配公开路径
        for (String publicPath : SecurityPathConfig.PUBLIC_PATHS) {
            if (pathMatcher.match(publicPath, path)) {
                log.info("路径 {} 匹配公开路径 {}", path, publicPath);
                return true;
            }
        }
        
        // 如果完整路径不匹配，尝试去掉上下文路径再匹配
        String contextPath = request.getContextPath();
        if (!contextPath.isEmpty() && path.startsWith(contextPath)) {
            String relativePath = path.substring(contextPath.length());
            log.info("尝试相对路径匹配: {}", relativePath);
            for (String publicPath : SecurityPathConfig.PUBLIC_PATHS) {
                if (pathMatcher.match(publicPath, relativePath)) {
                    log.info("相对路径 {} 匹配公开路径 {}", relativePath, publicPath);
                    return true;
                }
            }
        }
        
        log.info("路径 {} 不是公开路径", path);
        return false;
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 对于公开路径，我们仍然需要执行过滤器来设置认证信息
        // 所以永远不要跳过过滤器执行
        return false;
    }
}