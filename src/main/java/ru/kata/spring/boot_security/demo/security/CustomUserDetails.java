package ru.kata.spring.boot_security.demo.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.kata.spring.boot_security.demo.model.User;

import java.io.Serial;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 * Позволяет Spring Security работать с объектами для аутентификации и авторизации.
 */
public final class CustomUserDetails implements UserDetails {

    @Serial
    private static final long serialVersionUID = 0L;
    private final User user;
    private final Set<CustomRoleDetails> authorities;

    /**
     *
     */
    public CustomUserDetails(User user, Set<CustomRoleDetails> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User user() {
        return user;
    }

    public Set<CustomRoleDetails> authorities() {
        return authorities;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CustomUserDetails) obj;
        return Objects.equals(this.user, that.user) &&
                Objects.equals(this.authorities, that.authorities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, authorities);
    }

    @Override
    public String toString() {
        return "CustomUserDetails[" +
                "user=" + user + ", " +
                "authorities=" + authorities + ']';
    }

}