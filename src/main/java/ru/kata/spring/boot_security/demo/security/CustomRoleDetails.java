package ru.kata.spring.boot_security.demo.security;

import org.springframework.security.core.GrantedAuthority;
import ru.kata.spring.boot_security.demo.model.Role;

public record CustomRoleDetails(Role role) implements GrantedAuthority {

    @Override
    public String getAuthority() {
        return role.getName();
    }

}