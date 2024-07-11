package ru.kata.spring.boot_security.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Column;
import java.util.HashSet;
import java.util.Set;

/**
 * Представляет роль пользователя в системе
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@ToString(exclude = "users") // Исключить поле users из метода toString
@EqualsAndHashCode(exclude = "users") // Исключить поле users из методов equals и hashCode
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false, unique = true)
    @NonNull
    private String name;

    @JsonIgnore // предотвратит рекурсивную сериализацию
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();


}
