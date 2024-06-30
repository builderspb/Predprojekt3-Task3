package ru.kata.spring.boot_security.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;


import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Используется Spring Security для загрузки пользовательских данных при аутентификации.
 */

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUserNameWithRoles(userName);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь с именем " + userName + " не найден.");
        }

        var user = userOptional.get();
        Set<CustomRoleDetails> authorities = user.getRoles().stream()
                .map(CustomRoleDetails::new)
                .collect(Collectors.toSet());

        return new CustomUserDetails(user, authorities);
    }
}