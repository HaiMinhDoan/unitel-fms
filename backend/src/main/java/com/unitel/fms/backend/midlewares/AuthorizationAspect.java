package com.unitel.fms.backend.midlewares;

import com.unitel.fms.backend.constants.enums.RoleType;
import com.unitel.fms.backend.contexts.SecurityContextHolder;
import com.unitel.fms.backend.customizeanotations.RequireAuth;
import com.unitel.fms.backend.dtos.AuthInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Aspect
@Component
public class AuthorizationAspect {

    @Around("@annotation(requireAuth)")
    public Object checkAuthorization(ProceedingJoinPoint joinPoint, RequireAuth requireAuth) throws Throwable {
        AuthInfo authInfo = SecurityContextHolder.getAuthInfo();
        if(authInfo == null) {
            throw new AccessDeniedException("Access denied");
        }
        if(authInfo.hasAnyRole(RoleType.SYSTEM_ADMIN)){
            return joinPoint.proceed();
        }

        UUID orgId = SecurityContextHolder.getOrgId();
        if(requireAuth.inWorkspace() && orgId == null) {
            throw new AccessDeniedException("Access denied");
        }
        String[] requiredRoles = requireAuth.roles();
        if (!requireAuth.inWorkspace() && (requiredRoles == null || requiredRoles.length == 0)) {
            return joinPoint.proceed();
        }

        if(Arrays.stream(requiredRoles).toList().contains(RoleType.ALL)){
            if(requireAuth.inWorkspace()) {
                if(authInfo.inWorkspace(orgId)) return joinPoint.proceed();
                else throw new AccessDeniedException("Access denied");
            }
            return joinPoint.proceed();
        }

        if (requiredRoles.length > 0) {
            boolean hasAccess = requireAuth.rolesLogic() == RequireAuth.LogicType.OR
                    ? authInfo.hasAnyRole(requiredRoles) : authInfo.hasAllRoles(requiredRoles);
            if (!hasAccess) {
                throw new AccessDeniedException("Vai trò không đủ");
            }
        }

        if(requireAuth.inWorkspace()){
            boolean hasAccess = requireAuth.rolesLogic() == RequireAuth.LogicType.OR
                    ? authInfo.hasAnyRoleInWorkspace(orgId) : authInfo.hasAllRolesInWorkspace(orgId);
            if (!hasAccess) {
                throw new AccessDeniedException("Vai trò không đủ");
            }
        }
        return joinPoint.proceed();
    }

}
