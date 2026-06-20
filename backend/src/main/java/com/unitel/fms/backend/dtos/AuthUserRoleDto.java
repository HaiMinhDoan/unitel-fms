package com.unitel.fms.backend.dtos;

import com.unitel.fms.backend.entities.UserRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link com.unitel.fms.backend.entities.UserRole}
 */

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthUserRoleDto implements Serializable {
    UUID id;
    UUID userId;
    UUID roleId;
    String roleCode;
    String roleName;
    String roleDescription;
    UUID orgId;
    String orgCode;
    String orgNameEn;
    String orgNameVi;
    String orgNameLo;
    String orgOrgType;
    String orgRegion;
    UUID grantedById;
    String grantedByFullName;
    @NotNull
    @Size(max = 50)
    String status;

    public static AuthUserRoleDto fromEntity(UserRole userRole) {
        UUID orgId = userRole.getOrg() != null ? userRole.getOrg().getId() : null;
        return AuthUserRoleDto.builder()
                .id(userRole.getId())
                .userId(userRole.getUser().getId())
                .roleId(userRole.getRole().getId())
                .roleCode(userRole.getRole().getCode())
                .roleName(userRole.getRole().getName())
                .roleDescription(userRole.getRole().getDescription())
                .orgId(orgId)
                .orgCode(userRole.getOrg().getCode())
                .orgNameEn(userRole.getOrg().getNameEn())
                .orgNameVi(userRole.getOrg().getNameVi())
                .orgNameLo(userRole.getOrg().getNameLo())
                .orgOrgType(userRole.getOrg().getOrgType())
                .orgRegion(userRole.getOrg().getRegion())
                .grantedById(userRole.getGrantedBy().getId())
                .grantedByFullName(userRole.getGrantedBy().getFullName())
                .status(userRole.getStatus())
                .build();
    }
}