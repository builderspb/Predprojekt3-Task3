package ru.kata.spring.boot_security.demo.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.exceptionHandling.exception.NoSuchRoleException;
import ru.kata.spring.boot_security.demo.exceptionHandling.exception.NoSuchUserException;
import ru.kata.spring.boot_security.demo.exceptionHandling.exception.UserSaveException;
import ru.kata.spring.boot_security.demo.exceptionHandling.exception.UserUpdateException;
import ru.kata.spring.boot_security.demo.helper.PasswordService;
import ru.kata.spring.boot_security.demo.helper.RoleService;
import ru.kata.spring.boot_security.demo.mapper.UserMapper;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final PasswordService passwordService;

    private static final String USER_WITH_THIS_ID_NOT_FOUND = "Пользователь с ID %d не найден";
    private static final String USER_LIST_IS_EMPTY = "Cписок пользователей пуст";
    private static final String ERROR_SAVING_USER = "Ошибка при сохранении пользователя";
    private static final String ERROR_UPDATING_USER = "Ошибка при обновлении пользователя";


    /**
     * Получает всех пользователей вместе с их ролями из базы данных.
     * <p>
     * Метод findAll() из UserRepository возвращает список пользователей с их ролями.
     * Каждый пользователь преобразуется в объект UserDTO с помощью метода convertToUserDTO() из UserMapper.
     * Результат сортируется по ID и собирается в список.
     *
     * @return List<UserDTO>
     * @throws NoSuchUserException с сообщением "Cписок пользователей пуст"
     */
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        // Проверяем, пуст ли список пользователей
        if (users.isEmpty()) {
            throw new NoSuchUserException(USER_LIST_IS_EMPTY);
        }

        List<UserDTO> userDTO = users.stream()
                .map(userMapper::convertToUserDTO)
                .sorted(Comparator.comparing(UserDTO::getId)).toList();

        return userDTO;
    }


    /**
     * Получает пользователя с его ролями по ID.
     * <p>
     * Метод findById() из UserRepository возвращает Optional, содержащий пользователя, если он существует.
     * Пользователь преобразуется в объект UserDTO с помощью метода convertToUserDTO() из UserMapper.
     *
     * @param id ID пользователя
     * @return UserDTO
     * @throws NoSuchUserException с сообщением "Пользователь с ID %d не найден", если пользователь с указанным ID не найден
     */
    @Override
    public UserDTO getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchUserException(String.format(USER_WITH_THIS_ID_NOT_FOUND, id)));
        return userMapper.convertToUserDTO(user);

    }


    /**
     * Сохраняет нового пользователя в базе данных.
     * <p>
     * Валидация ролей и установка их пользователю.
     * Кодирует и устанавливает пароль.
     * Сохраняет пользователя.
     * Выполняет конвертацию в UserDTO.
     * Пробрасывает возможные исключения пойманные при валидации ролей, сохранении и конвертации пользователя.
     *
     * @param user с данными нового пользователя
     * @return UserDTO
     * @throws NoSuchRoleException если роль не найдена после попытки создать ее
     * @throws NoSuchUserException с сообщением "Ошибка при сохранении пользователя"
     */
    @Override
    @Transactional
    public UserDTO saveUser(User user) {
        try {
            user = roleService.validateRoles(user);
            user.setPassword(passwordService.encodePassword(user.getPassword()));

            User savedUser = userRepository.save(user);

            return userMapper.convertToUserDTO(savedUser);
        } catch (NoSuchRoleException e) {
            throw e; // Пробросить исключение, чтобы оно могло быть обработано в контроллере
        } catch (Exception e) {
            throw new UserSaveException(ERROR_SAVING_USER);
        }
    }


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
     * @param user DTO с обновленными данными пользователя
     * @return UserDTO Обновленный пользователь в виде UserDTO
     * @throws NoSuchUserException с сообщением "Пользователь с ID %d не найден", если пользователь с указанным ID не найден
     * @throws NoSuchRoleException если роль не найдена после попытки создать ее
     * @throws UserUpdateException с сообщением "Ошибка при обновлении пользователя"
     */
    @Override
    @Transactional
    public UserDTO updateUser(User user) {
        try {
            Long userId = user.getId();
            User existingUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new NoSuchUserException(String.format(USER_WITH_THIS_ID_NOT_FOUND, userId)));

            user = roleService.validateRoles(user);
            user.setPassword(passwordService.passwordProcessing(existingUser, user));

            User updatedUser = userRepository.save(user);

            return userMapper.convertToUserDTO(updatedUser);
        } catch (NoSuchRoleException e) {
            throw e; // Пробросить исключение, чтобы оно могло быть обработано в контроллере
        } catch (Exception e) {
            throw new UserUpdateException(ERROR_UPDATING_USER, e);
        }
    }


    /**
     * Удаляет пользователя из базы данных по его ID.
     * <p>
     * Поиск пользователя с помощью метода findById() из UserRepository, сразу с его ролями. Если пользователь существует,
     * он каскадно вместе с ролями удаляется из БД с помощью метода deleteById().
     *
     * @param id ID пользователя, который должен быть удален
     * @throws NoSuchUserException с сообщением "Пользователь с ID %d не найден", если пользователь с указанным ID не найден
     */
    @Override
    @Transactional
    public void deleteUser(long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new NoSuchUserException(String.format(USER_WITH_THIS_ID_NOT_FOUND, id)));

        userRepository.deleteById(id);
    }

}
