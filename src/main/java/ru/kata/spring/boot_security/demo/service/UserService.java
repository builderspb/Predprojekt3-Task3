package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

/**
 * Интерфейс для управления пользователями.
 * <p>
 * Определяет методы для выполнения CRUD операций над пользователями.
 */
public interface UserService {

    List<UserDTO> getAllUsers();

    UserDTO getUserById(long id);

    UserDTO saveUser(User user);

    UserDTO updateUser(User user);

    String deleteUser(long id);

}

