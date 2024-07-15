package ru.kata.spring.boot_security.demo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.kata.spring.boot_security.demo.util.userValidation.PasswordConstraint;
import ru.kata.spring.boot_security.demo.util.userValidation.ValidationGroups;

/**
 * Представляет пользователя в системе
 */
@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "roles") // Исключить поле users из метода toString
@EqualsAndHashCode(exclude = "roles") // Исключить поле users из методов equals и hashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Column(name = "user_name")
    private String userName;

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Column(name = "lastname")
    private String lastName;

    @Column(name = "phone_number")
    @Pattern(regexp = "\\d{3}-\\d{2}-\\d{2}", message = "please use pattern XXX-XX-XX", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @PasswordConstraint(groups = ValidationGroups.Create.class)
    @Column(name = "password")
    private String password;

    @NotEmpty(message = "Необходимо выбрать хотя бы одну роль.", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

}
