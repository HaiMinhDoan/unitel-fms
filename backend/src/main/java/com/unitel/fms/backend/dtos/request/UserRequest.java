package com.unitel.fms.backend.dtos.request;

import lombok.Data;
import java.util.UUID;

@Data
public class UserRequest {
    private String username;
    private String passwordHash;
    private String email;
    private String fullName;
    private String phone;
    private String ssoSubject;
    private String locale;
    private UUID orgId;
    private String status;
}
