package ru.kata.spring.boot_security.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import ru.kata.spring.boot_security.demo.mapper.UserMapperWrapper;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.security.CustomUserDetails;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.util.userValidation.ValidationGroups;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "API для управления пользователями")
// Используется для группировки и описания контроллера.
public class UserRestController {
    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);
    private final UserService userService;
    private final UserMapperWrapper userMapperWrapper;


    /**
     * Получает всех пользователей через API.
     * <p>
     * Метод проверен через Postman.
     *
     * @return ResponseEntity<List < UserDTO>> Список пользователей в виде UserDTO
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        logger.info("Вызов метода getAllUsers");
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
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить пользователя по ID", description = "Возвращает пользователя по его ID")
    public ResponseEntity<UserDTO> getUserById(@PathVariable long id) {
        logger.info("Вызов метода getUserById с параметром id = {}", id);
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
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать нового пользователя", description = "Создает нового пользователя")
    public ResponseEntity<UserDTO> createUser(@Validated(ValidationGroups.Create.class) @RequestBody User user) {
        logger.info("Вызов метода createUser с параметром user = {}", user);
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
     * @param id   ID пользователя, устанавливаемый в объект User
     * @param user объект User, содержащий обновленные данные пользователя
     * @return ResponseEntity<UserDTO> Обновленный пользователь в виде UserDTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить пользователя", description = "Обновляет данные существующего пользователя")
    public ResponseEntity<UserDTO> updateUser(@PathVariable long id, @Validated(ValidationGroups.Update.class) @RequestBody User user) {
        logger.info("Вызов метода updateUser с параметрами id = {}, user = {}", id, user);
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
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по его ID")
    public ResponseEntity<String> deleteUser(@PathVariable long id) {
        logger.info("Вызов метода deleteUser с параметром id = {}", id);
        String resultMessage = userService.deleteUser(id);
        return ResponseEntity.ok(resultMessage);
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
    @Operation(summary = "Получить данные аутентифицированного пользователя", description = "Возвращает данные текущего аутентифицированного пользователя")
    public ResponseEntity<UserDTO> showUserHomePage() {
        logger.info("Вызов метода showUserHomePage");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        UserDTO userDTO = userMapperWrapper.convertToUserDTO(customUserDetails.getUser());
        logger.info("Получены данные аутентифицированного пользователя через API: {}", userDTO);

        return ResponseEntity.status(HttpStatus.OK).body(userDTO);

    }
}

