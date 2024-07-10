package ru.kata.spring.boot_security.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Проверяет, существует ли роль с заданным именем.
     *
     * @param name имя роли
     * @return boolean true, если роль существует, иначе false
     */
    boolean existsByName(String name);


    /**
     * Находит роль по заданному имени.
     *
     * @param name имя роли
     * @return Optional<Role> роль, если найдена
     */
    Optional<Role> findRoleByName(String name);

}