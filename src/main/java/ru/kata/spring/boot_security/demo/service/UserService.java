package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.exceptionHandling.exception.NoSuchRoleException;
import ru.kata.spring.boot_security.demo.exceptionHandling.exception.NoSuchUserException;
import ru.kata.spring.boot_security.demo.exceptionHandling.exception.UserSaveException;
import ru.kata.spring.boot_security.demo.exceptionHandling.exception.UserUpdateException;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

/**
 * Интерфейс для управления пользователями.
 * <p>
 * Определяет методы для выполнения CRUD операций над пользователями.
 */
public interface UserService {

    /**
     * Получает всех пользователей вместе с их ролями из базы данных.
     * <p>
     * Метод findAll() из UserRepository возвращает список пользователей с их ролями.
     * Каждый пользователь преобразуется в объект UserDTO с помощью метода convertToUserDTO() из UserMapper.
     * Результат сортируется по ID и собирается в список.
     *
     * @return список всех пользователей в виде UserDTO
     * @throws NoSuchUserException с сообщением "Cписок пользователей пуст"
     */
    List<UserDTO> getAllUsers();


    /**
     * Получает пользователя с его ролями по ID.
     * <p>
     * Метод findById() из UserRepository возвращает Optional, содержащий пользователя, если он существует.
     * Пользователь преобразуется в объект UserDTO с помощью метода convertToUserDTO() из UserMapper.
     *
     * @param id идентификатор пользователя
     * @return пользователь в виде UserDTO
     * @throws NoSuchUserException с сообщением "Пользователь с ID %d не найден", если пользователь с указанным ID не найден
     */
    UserDTO getUserById(long id);


    /**
     * Сохраняет нового пользователя в базе данных.
     * <p>
     * Валидация ролей и установка их пользователю.
     * Кодирует и устанавливает пароль.
     * Сохраняет пользователя.
     * Выполняет конвертацию в UserDTO.
     * Пробрасывает возможные исключения пойманные при валидации ролей, сохранении и конвертации пользователя.
     *
     * @param user объект User, содержащий данные нового пользователя
     * @return сохраненный пользователь в виде UserDTO
     * @throws NoSuchRoleException если роль не найдена после попытки создать ее
     * @throws UserSaveException   с сообщением "Ошибка при сохранении пользователя"
     */
    UserDTO saveUser(User user);


    /**
     * Обновляет существующего пользователя в базе данных.
     * <p>
     * Поиск пользователя по его ID с помощью метода findById() из UserRepository.
     * Валидация ролей и установка их пользователю.
     * Обрабатывает пароль с помощью метода passwordProcessing.
     * Сохраняет обновленного пользователя.
     * Выполняет конвертацию в UserDTO.
     * Пробрасывает возможные исключения, пойманные при поиске пользователя по ID, валидации ролей, обновлении и конвертации пользователя.
     *
     * @param user объект User, содержащий обновленные данные пользователя
     * @return обновленный пользователь в виде UserDTO
     * @throws NoSuchUserException с сообщением "Пользователь с ID %d не найден", если пользователь с указанным ID не найден
     * @throws NoSuchRoleException если роль не найдена после попытки создать ее
     * @throws UserUpdateException с сообщением "Ошибка при обновлении пользователя"
     */
    UserDTO updateUser(User user);


    /**
     * Удаляет пользователя из базы данных по его ID.
     * <p>
     * Поиск пользователя с помощью метода findById() из UserRepository, сразу с его ролями. Если пользователь существует,
     * он каскадно вместе с ролями удаляется из БД с помощью метода deleteById().
     *
     * @param id идентификатор пользователя, который должен быть удален
     * @throws NoSuchUserException с сообщением "Пользователь с ID %d не найден", если пользователь с указанным ID не найден
     */
    void deleteUser(long id);

}

