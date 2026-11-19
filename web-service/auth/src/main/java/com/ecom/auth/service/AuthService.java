package com.ecom.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecom.auth.dto.LoginRequest;
import com.ecom.auth.dto.SignupRequest;
import com.ecom.auth.entity.UserAccount;
import com.ecom.auth.exception.InvalidCredentialsException;

@Service
public class AuthService {

    private static final String DUMMY_BCRYPT_HASH = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserAccount signup(SignupRequest request) {
        return userService.createUser(request.username(), request.password());
    }

    public String login(LoginRequest request) {
        UserAccount account = userService.findByUsername(request.username()).orElse(null);
        if (account == null) {
            passwordEncoder.matches(request.password(), DUMMY_BCRYPT_HASH);
            throw new InvalidCredentialsException();
        }
        if (!passwordEncoder.matches(request.password(), account.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return jwtService.generateToken(account);
    }
}
