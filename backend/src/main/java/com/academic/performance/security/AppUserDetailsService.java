package com.academic.performance.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academic.performance.entity.User;
import com.academic.performance.repository.UserRepository;

/**
 * Tells Spring Security how to load a user from our {@code users} table.
 *
 * <p>People log in with their <strong>email</strong>, but a JWT is normally keyed
 * by username. To support both, this service accepts either a username or an
 * email as the "identifier". Once found, the {@link UserPrincipal} returns the
 * canonical username, which becomes the token subject.
 *
 * <p>When no user exists we throw {@link UsernameNotFoundException}; the login
 * endpoint converts every authentication failure into the same generic
 * "Invalid email or password" message so attackers cannot discover which emails
 * are registered.
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("No account found for the given credentials."));
        return new UserPrincipal(user);
    }
}
