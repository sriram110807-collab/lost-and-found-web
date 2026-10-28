package com.bufinder.web.service;

import com.bufinder.web.dto.UserRegistrationDto;
import com.bufinder.web.model.Role;
import com.bufinder.web.model.User;
import com.bufinder.web.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationDto regDto;

    @BeforeEach
    void setUp() {
        regDto = new UserRegistrationDto();
        regDto.setFullName("Test User");
        regDto.setEmail("test@bufinder.com");
        regDto.setPassword("Password123");
        regDto.setConfirmPassword("Password123");
        regDto.setPhone("+91 9999988888");
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.existsByEmail("test@bufinder.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("hashedPassword");

        User savedUser = new User("Test User", "test@bufinder.com", "hashedPassword", "+91 9999988888", Role.ROLE_USER);
        savedUser.setId(10L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(regDto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("test@bufinder.com", result.getEmail());
        assertEquals(Role.ROLE_USER, result.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_DuplicateEmailThrowsException() {
        when(userRepository.existsByEmail("test@bufinder.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(regDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_PasswordMismatchThrowsException() {
        regDto.setConfirmPassword("DifferentPassword");

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(regDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testFindByEmail() {
        User user = new User("Test", "test@bufinder.com", "hash", null, Role.ROLE_USER);
        when(userRepository.findByEmail("test@bufinder.com")).thenReturn(Optional.of(user));

        Optional<User> found = userService.findByEmail("test@bufinder.com");
        assertTrue(found.isPresent());
        assertEquals("test@bufinder.com", found.get().getEmail());
    }
}
