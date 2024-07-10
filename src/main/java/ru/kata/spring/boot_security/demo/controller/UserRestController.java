package ru.kata.spring.boot_security.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.mapper.UserMapper;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.security.CustomUserDetails;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.util.userValidation.ValidationGroups;


import java.util.List;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;
    private final UserMapper userMapper;
    private static final String USER_DELETED = "Пользователь с ID = %d удален";


    /**
     * Получает всех пользователей через API.
     * <p>
     * Метод проверен через Postman.
     *
     * @return ResponseEntity<List < UserDTO>> Список пользователей в виде UserDTO
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> userDTO = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }


    /**
     * Получает пользователя по ID через API.
     * <p>
     * Метод проверен через Postman.
     *
     * @param id ID пользователя
     * @return ResponseEntity<UserDTO> Пользователь в виде UserDTO
     */
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable long id) {
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }


    /**
     * Создает нового пользователя через API.
     * <p>
     * Метод проверен через Postman.
     * При сохранении пользователя с невалидными данными (пустыми полями) исключения обрабатываются методом handleValidationExceptions.
     *
     * @param user объект User, содержащий данные нового пользователя
     * @return ResponseEntity<UserDTO> Созданный пользователь в виде UserDTO
     */
    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@Validated(ValidationGroups.Create.class) @RequestBody User user) {
        UserDTO userDTO = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }


    /**
     * Обновляет пользователя по ID через API.
     * <p>
     * Метод проверен через Postman.
     * При обновлении пользователя с невалидными данными (пустыми полями) исключения обрабатываются методом handleValidationExceptions.
     * Настроена валидация для групп
     *
     * @param id ID пользователя, устанавливаемый в объект User
     * @param user объект User, содержащий обновленные данные пользователя
     * @return ResponseEntity<UserDTO> Обновленный пользователь в виде UserDTO
     */
    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable long id,@Validated(ValidationGroups.Update.class) @RequestBody User user) {
        user.setId(id);
        UserDTO userDTO = userService.updateUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }


    /**
     * Удаляет пользователя по ID через API.
     * <p>
     * Метод проверен через Postman.
     * При удалении существующего пользователя из БД, контроллер возвращает сообщение "Пользователь с ID = {id} удален".
     * При отсутствии пользователя в БД выбрасывает исключение NoSuchUserException с сообщением "Пользователь с ID %d не найден".
     *
     * @param id ID пользователя, который должен быть удален
     * @return ResponseEntity<String> Сообщение о статусе удаления пользователя
     */
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        String message = String.format(USER_DELETED, id);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }


    /**
     * Возвращает данные пользователя, который прошел аутентификацию.
     * <p>
     * Метод проверен через Postman.
     * Возвращает данные непосредственно того пользователя, который прошел аутентификацию.
     *
     * @return ResponseEntity<UserDTO> Аутентифицированный пользователь в виде UserDTO
     */
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserDTO> showUserHomePage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        UserDTO userDTO = userMapper.convertToUserDTO(customUserDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }
}

