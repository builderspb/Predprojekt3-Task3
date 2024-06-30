package ru.kata.spring.boot_security.demo.dataInitializers;

import ru.kata.spring.boot_security.demo.model.Role;

import java.util.Set;

/**
 * Используется для создания объектов пользователя
 */
public record UserData(String userName, String lastName, String phone, String email, String password, Set<Role> roles) {

}