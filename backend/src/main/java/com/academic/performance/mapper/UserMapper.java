package com.academic.performance.mapper;

import org.springframework.stereotype.Component;

import com.academic.performance.dto.UserResponse;
import com.academic.performance.entity.User;

/**
 * Converts a {@link User} entity into a safe {@link UserResponse} DTO.
 *
 * <p>Keeping the conversion in one place guarantees that the BCrypt hash (and
 * any future sensitive field) is never accidentally serialised to a client.
 */
@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled());
    }
}
