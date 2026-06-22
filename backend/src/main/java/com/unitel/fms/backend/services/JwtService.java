package com.unitel.fms.backend.services;

import com.nimbusds.jwt.JWTClaimsSet;
import com.unitel.fms.backend.dtos.AuthInfo;

import java.util.Set;
import java.util.UUID;

public interface JwtService {
    String buildScope(Set<String> roles);

    String generateToken(AuthInfo authInfo, String userAgent);

    UUID getUserId(String token);

    JWTClaimsSet getClaimsFromToken(String token);

    String getTokenFromAuthHeader(String authHeader);

    AuthInfo getAuthInfoFromToken(String token);

    String generateRefreshToken(AuthInfo authInfo, String userAgent);

    boolean isTokenExpired(String token);
}
