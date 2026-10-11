package com.academic.performance.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academic.performance.dto.AuthResponse;
import com.academic.performance.dto.LoginRequest;
import com.academic.performance.dto.RegisterRequest;
import com.academic.performance.dto.UserResponse;
import com.academic.performance.entity.Role;
import com.academic.performance.entity.User;
import com.academic.performance.exception.DuplicateResourceException;
import com.academic.performance.exception.ResourceNotFoundException;
import com.academic.performance.mapper.UserMapper;
import com.academic.performance.repository.UserRepository;
import com.academic.performance.security.JwtService;
import com.academic.performance.security.UserPrincipal;

/**
 * All authentication business logic lives here, keeping the controller thin.
 *
 * <p>Registration: validate uniqueness, BCrypt-hash the password, save the user.
 * Login: delegate to Spring Security's {@link AuthenticationManager} (which uses
 * our {@code AppUserDetailsService} + BCrypt), then mint a JWT.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    /**
     * Creates a new STUDENT account. Public self-registration can never create an
     * ADMIN/FACULTY account, which prevents privilege escalation.
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username", username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email", email);
        }

        User user = new User(username, email, passwordEncoder.encode(request.getPassword()), Role.STUDENT);
        user.setEnabled(true);

        return userMapper.toResponse(userRepository.save(user));
    }

    /**
     * Authenticates the email/password pair and returns a signed JWT.
     *
     * <p>{@code authenticationManager.authenticate(...)} throws
     * {@code BadCredentialsException} (a subclass of {@code AuthenticationException})
     * when the email is unknown or the password is wrong. The
     * {@link com.academic.performance.exception.GlobalExceptionHandler} turns that
     * into a generic 401, so we never reveal which part was wrong.
     */
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword()));

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                userMapper.toResponse(principal.getUser()));
    }

    /** Returns the profile of the currently authenticated user. */
    @Transactional(readOnly = true)
    public UserResponse currentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return userMapper.toResponse(user);
    }
}
