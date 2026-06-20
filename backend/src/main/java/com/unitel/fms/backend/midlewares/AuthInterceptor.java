package com.unitel.fms.backend.midlewares;

import com.unitel.fms.backend.contexts.SecurityContextHolder;
import com.unitel.fms.backend.dtos.AuthInfo;
import com.unitel.fms.backend.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtService jwtService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        SecurityContextHolder.setPath(request.getRequestURI());

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            String token = jwtService.getTokenFromAuthHeader(authHeader);
            AuthInfo authInfo = jwtService.getAuthInfoFromToken(token);
            SecurityContextHolder.setAuthInfo(authInfo);
        } else {
            SecurityContextHolder.setAuthInfo(null);
        }

        String orgIdString = request.getHeader("orgId");
        if (orgIdString != null) {
            UUID orgId = UUID.fromString(orgIdString);
            SecurityContextHolder.setOrgId(orgId);
        } else {
            SecurityContextHolder.setOrgId(null);
        }

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        SecurityContextHolder.clear();
    }
}
