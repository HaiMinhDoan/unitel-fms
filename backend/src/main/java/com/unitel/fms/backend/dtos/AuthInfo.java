package com.unitel.fms.backend.dtos;

import com.unitel.fms.backend.entities.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.*;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthInfo {
    UUID id;
    UUID orgId;
    String email;
    String username;
    String phone;
    List<AuthUserRoleDto> authUserRoles = new ArrayList<>();

    public static AuthInfo fromEntity(User user) {
        UUID orgId = user.getOrg() != null ? user.getOrg().getId() : null;
        return AuthInfo.builder()
                .id(user.getId())
                .orgId(orgId)
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .authUserRoles(
                        user.getUserRoles().stream().map(AuthUserRoleDto::fromEntity).toList())
                .build();
    }

    public Set<String> getRoles() {
        return this.authUserRoles.stream().map(AuthUserRoleDto::getRoleCode).collect(Collectors.toSet());
    }

    public boolean hasAnyRole(String... roles) {
        Set<String> rolesOfUser = getRoles();
        for (String role : roles) {
            if (rolesOfUser.contains(role)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAllRoles(String... roles) {
        Set<String> rolesOfUser = getRoles();
        for (String role : roles) {
            if (!rolesOfUser.contains(role)) {
                return false;
            }
        }
        return true;
    }

    public boolean inWorkspace(UUID orgId) {
        if (this.orgId.equals(orgId))
            return true;
        for (AuthUserRoleDto userRole : getAuthUserRoles()) {
            if (userRole.getOrgId().equals(orgId)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyRoleInWorkspace(UUID orgId, String... roles) {
        List<String> listRole = Arrays.stream(roles).toList();
        for (AuthUserRoleDto userRole : getAuthUserRoles()) {
            if (userRole.getOrgId().equals(orgId)) {
                if (listRole.contains(userRole.getRoleCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasAllRolesInWorkspace(UUID orgId, String... roles) {
        List<String> listRole = Arrays.stream(roles).toList();
        boolean inWorkspace = false;
        for (AuthUserRoleDto userRole : getAuthUserRoles()) {
            if (userRole.getOrgId().equals(orgId)) {
                inWorkspace = true;
                if (!listRole.contains(userRole.getRoleCode())) {
                    return false;
                }
            }
        }
        return inWorkspace;
    }
}
