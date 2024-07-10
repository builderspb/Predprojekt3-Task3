package ru.kata.spring.boot_security.demo.dataInitializers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.kata.spring.boot_security.demo.model.Role;

import java.util.Set;

/**
 * Используется для создания объектов пользователя
 */
@Data
@Builder
@AllArgsConstructor
@ToString(exclude = "roles") // Исключить поле users из метода toString
@EqualsAndHashCode(exclude = "roles") // Исключить поле users из методов equals и hashCode
public final class UserData {
    private final String userName;
    private final String lastName;
    private final String phone;
    private final String email;
    private final String password;
    private final Set<Role> roles;

}