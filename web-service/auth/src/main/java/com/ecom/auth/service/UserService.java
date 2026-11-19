package com.ecom.auth.service;

import java.util.Locale;
import java.util.Optional;
import java.nio.charset.StandardCharsets;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.auth.entity.UserAccount;
import com.ecom.auth.entity.UserRole;
import com.ecom.auth.exception.UsernameAlreadyExistsException;
import com.ecom.auth.exception.InvalidSignupException;
import com.ecom.auth.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserAccount createUser(String username, String rawPassword) {
        if (rawPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new InvalidSignupException();
        }
        String canonicalUsername = normalizeUsername(username);
        if (userRepository.existsByUsername(canonicalUsername)) {
            throw new UsernameAlreadyExistsException();
        }

        UserAccount account = new UserAccount(
                canonicalUsername,
                passwordEncoder.encode(rawPassword),
                UserRole.USER);
        try {
            return userRepository.saveAndFlush(account);
        } catch (DataIntegrityViolationException exception) {
            // Handles concurrent signup requests that race past the existence check.
            throw new UsernameAlreadyExistsException();
        }
    }

    @Transactional(readOnly = true)
    public Optional<UserAccount> findByUsername(String username) {
        return userRepository.findByUsername(normalizeUsername(username));
    }

    private String normalizeUsername(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }
}
