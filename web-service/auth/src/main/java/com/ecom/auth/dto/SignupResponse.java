package com.ecom.auth.dto;

import java.time.Instant;

import com.ecom.auth.entity.UserAccount;
import com.ecom.auth.entity.UserRole;

public record SignupResponse(Long id, String username, UserRole role, Instant createdAt) {

    public static SignupResponse from(UserAccount account) {
        return new SignupResponse(account.getId(), account.getUsername(), account.getRole(), account.getCreatedAt());
    }
}
