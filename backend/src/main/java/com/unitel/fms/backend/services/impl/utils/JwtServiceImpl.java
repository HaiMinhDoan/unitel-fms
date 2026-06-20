package com.unitel.fms.backend.services.impl.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.unitel.fms.backend.constants.ConstantVariables;
import com.unitel.fms.backend.dtos.AuthInfo;
import com.unitel.fms.backend.dtos.AuthUserRoleDto;
import com.unitel.fms.backend.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtServiceImpl implements JwtService {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String buildScope(Set<String> roles) {
        StringJoiner scopeJoiner = new StringJoiner(" ");
        roles.forEach(scopeJoiner::add);
        return scopeJoiner.toString();
    }

    @Override
    public String generateToken(AuthInfo authInfo, String userAgent) {
        Set<String> roles = authInfo
                .getAuthUserRoles().stream()
                .map(AuthUserRoleDto::getRoleCode)
                .collect(Collectors.toSet());
        try {
            JWSHeader jwtHeader = new JWSHeader(JWSAlgorithm.HS256);
            String authUserRolesJson = objectMapper.writeValueAsString(authInfo.getAuthUserRoles());
            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                    .subject(authInfo.getEmail())
                    .issuer("UnitelFms")
                    .issueTime(new Date())
                    .expirationTime(new Date(new Date().getTime() + 24 * 60 * 60 * 1000))
                    .claim("id", authInfo.getId())
                    .claim("email", authInfo.getEmail())
                    .claim("username", authInfo.getUsername())
                    .claim("phone", authInfo.getPhone())
                    .claim("scope", buildScope(roles))
                    .claim("authUserRoles", authUserRolesJson)
                    .claim("userAgent", userAgent)
                    .build();

            Payload jwtPayload = new Payload(jwtClaimsSet.toJSONObject());
            JWSObject jwsObject = new JWSObject(jwtHeader, jwtPayload);
            jwsObject.sign(new MACSigner(ConstantVariables.SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException | JsonProcessingException e) {
            throw new RuntimeException("Error generating token: " + e.getMessage(), e);
        }

    }

    @Override
    public UUID getUserId(String token) {
        return (UUID) getClaimsFromToken(token).getClaims().get("id");
    }

    @Override
    public JWTClaimsSet getClaimsFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getTokenFromAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    @Override
    public AuthInfo getAuthInfoFromToken(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            JWSVerifier verifier = new MACVerifier(ConstantVariables.SIGNER_KEY.getBytes());

            if (!jwsObject.verify(verifier)) {
                throw new RuntimeException("Invalid token signature");
            }

            JWTClaimsSet claims = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());

            String authUserRolesJson = claims.getStringClaim("authUserRoles");
            List<AuthUserRoleDto> authUserRoles = objectMapper.readValue(
                    authUserRolesJson,
                    new TypeReference<>() {
                    }
            );
            return AuthInfo.builder()
                    .id(getUserId(token))
                    .email(claims.getStringClaim("email"))
                    .username(claims.getStringClaim("username"))
                    .phone(claims.getStringClaim("phone"))
                    .authUserRoles(authUserRoles)
                    .build();

        } catch (JOSEException | ParseException | JsonProcessingException e) {

        }
        return null;
    }

}
