package com.unitel.fms.backend.controllers;

import com.unitel.fms.backend.dtos.AuthInfo;
import com.unitel.fms.backend.dtos.request.LoginRequest;
import com.unitel.fms.backend.dtos.request.TokenRefreshRequest;
import com.unitel.fms.backend.dtos.response.LoginResponse;
import com.unitel.fms.backend.dtos.response.ResponseData;
import com.unitel.fms.backend.entities.User;
import com.unitel.fms.backend.repositories.UserRepository;
import com.unitel.fms.backend.services.JwtService;
import com.unitel.fms.backend.services.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RedisService redisService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ResponseData<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        
        if (userOpt.isEmpty()) {
            return ResponseData.<LoginResponse>builder().status(401).message("Sai tên đăng nhập hoặc mật khẩu").build();
        }

        User user = userOpt.get();
        if (!"active".equals(user.getStatus())) {
            return ResponseData.<LoginResponse>builder().status(403).message("Tài khoản đang bị khóa").build();
        }

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseData.<LoginResponse>builder().status(401).message("Sai tên đăng nhập hoặc mật khẩu").build();
        }

        AuthInfo authInfo = AuthInfo.fromEntity(user);
        String userAgent = httpRequest.getHeader("User-Agent");

        String accessToken = jwtService.generateToken(authInfo, userAgent);
        String refreshToken = jwtService.generateRefreshToken(authInfo, userAgent);

        // Store refresh token in redis for validation (e.g. 7 days TTL)
        String rtKey = redisService.buildKey("refresh-token", refreshToken);
        redisService.set(rtKey, user.getId().toString(), 7 * 24 * 3600);

        LoginResponse response = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(authInfo.getRoles())
                .orgId(user.getOrg() != null ? user.getOrg().getId() : null)
                .build();

        return ResponseData.<LoginResponse>builder().status(200).message("Đăng nhập thành công").data(response).build();
    }

    @PostMapping("/refresh")
    public ResponseData<LoginResponse> refresh(@Valid @RequestBody TokenRefreshRequest request, HttpServletRequest httpRequest) {
        String refreshToken = request.getRefreshToken();
        
        if (jwtService.isTokenExpired(refreshToken)) {
            return ResponseData.<LoginResponse>builder().status(401).message("Refresh token đã hết hạn").build();
        }

        String rtKey = redisService.buildKey("refresh-token", refreshToken);
        if (!redisService.exists(rtKey)) {
            return ResponseData.<LoginResponse>builder().status(401).message("Refresh token không hợp lệ hoặc đã bị thu hồi").build();
        }

        AuthInfo authInfo = jwtService.getAuthInfoFromToken(refreshToken);
        if (authInfo == null) {
            return ResponseData.<LoginResponse>builder().status(401).message("Token không hợp lệ").build();
        }

        // Generate new tokens
        String userAgent = httpRequest.getHeader("User-Agent");
        String newAccessToken = jwtService.generateToken(authInfo, userAgent);
        String newRefreshToken = jwtService.generateRefreshToken(authInfo, userAgent);

        // Revoke old refresh token, store new one
        redisService.delete(rtKey);
        String newRtKey = redisService.buildKey("refresh-token", newRefreshToken);
        redisService.set(newRtKey, authInfo.getId().toString(), 7 * 24 * 3600);

        LoginResponse response = LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .userId(authInfo.getId())
                .username(authInfo.getUsername())
                .email(authInfo.getEmail())
                .roles(authInfo.getRoles())
                .orgId(authInfo.getOrgId())
                .build();

        return ResponseData.<LoginResponse>builder().status(200).message("Refresh thành công").data(response).build();
    }

    @PostMapping("/logout")
    public ResponseData<Void> logout(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        String token = jwtService.getTokenFromAuthHeader(authHeader);
        
        if (token != null) {
            // Add access token to blacklist (e.g. 1 day TTL to cover its remaining lifespan)
            String blacklistKey = redisService.buildKey("token-blacklist", token);
            redisService.set(blacklistKey, "revoked", 24 * 3600);
        }
        
        return ResponseData.<Void>builder().status(200).message("Đăng xuất thành công").build();
    }
}
