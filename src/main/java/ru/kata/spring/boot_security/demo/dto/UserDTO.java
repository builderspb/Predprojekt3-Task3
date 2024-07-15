package ru.kata.spring.boot_security.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kata.spring.boot_security.demo.model.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.Set;

/**
 * Используется для представления данных, когда пользователь запрашивает информацию (например, GET-запросы).
 * <p>
 * Этот класс содержит только те поля, которые определены контрактом и предназначены для отправки клиенту.
 * Поле "password" исключено для обеспечения безопасности.
 */
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    private String userName;

    @NotBlank(message = "Фамилия не может быть пустой")
    private String lastName;

    @NotBlank(message = "Номер телефона не может быть пустым")
    @Pattern(regexp = "\\d{3}-\\d{2}-\\d{2}", message = "please use pattern XXX-XX-XX")
    private String phoneNumber;

    @NotBlank(message = "Электронная почта не может быть пустой")
    private String email;

    @JsonIgnore
    private String password;

    @NotEmpty(message = "Необходимо выбрать хотя бы одну роль")
    private Set<Role> roles;

}