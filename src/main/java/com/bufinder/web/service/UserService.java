package com.bufinder.web.service;

import com.bufinder.web.dto.UserProfileDto;
import com.bufinder.web.dto.UserRegistrationDto;
import com.bufinder.web.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    User registerUser(UserRegistrationDto registrationDto);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    User updateProfile(Long userId, UserProfileDto profileDto);
    boolean changePassword(Long userId, String currentPassword, String newPassword);
    List<User> findAllUsers();
    long getTotalUserCount();
}
