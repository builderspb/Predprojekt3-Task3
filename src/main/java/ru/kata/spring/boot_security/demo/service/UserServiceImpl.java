package ru.kata.spring.boot_security.demo.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.exception.exception.NoSuchUserException;
import ru.kata.spring.boot_security.demo.exception.exception.RoleCreationException;
import ru.kata.spring.boot_security.demo.exception.exception.UserSaveException;
import ru.kata.spring.boot_security.demo.exception.exception.UserUpdateException;
import ru.kata.spring.boot_security.demo.helper.PasswordService;
import ru.kata.spring.boot_security.demo.helper.RoleService;
import ru.kata.spring.boot_security.demo.mapper.UserMapperWrapper;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final UserMapperWrapper userMapperWrapper;
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
     * @return список всех пользователей в виде UserDTO
     * @throws NoSuchUserException с сообщением "Cписок пользователей пуст"
     */
    @Override
    public List<UserDTO> getAllUsers() {
        logger.debug("Запрос на получение всех пользователей");

        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new NoSuchUserException(USER_LIST_IS_EMPTY);
        }

        users.forEach(user -> {
            logger.info("Проверка перед маппингом User: {}", user);
            System.out.println("Проверка перед маппингом User: " + user);
        });

        List<UserDTO> userDTO = users.stream()
                .sorted(Comparator.comparing(User::getId, Comparator.nullsLast(Long::compareTo)))
                .map(userMapperWrapper::convertToUserDTO)
                .toList();

        logger.info("Получен список всех пользователей: {}", userDTO);
        return userDTO;
    }

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
    @Override
    public UserDTO getUserById(long id) {
        logger.debug("Запрос на получение пользователя с ID = {}", id);

        UserDTO userDTO = userRepository.findById(id)
                .map(userMapperWrapper::convertToUserDTO)
                .orElseThrow(() ->
                        new NoSuchUserException(String.format(USER_WITH_THIS_ID_NOT_FOUND, id)));

        logger.info("Пользователь с ID = {} успешно найден: {}", id, userDTO);
        return userDTO;
    }

    /**
     * Сохраняет нового пользователя в базе данных.
     * <p>
     * Валидация ролей и установка их пользователю.
     * Кодирует и устанавливает пароль.
     * Сохраняет пользователя.
     * Выполняет конвертацию в UserDTO.
     * Пробрасывает возможные исключения пойманные при валидации ролей, кодирования пароля, сохранении и конвертации пользователя.
     *
     * @param user объект User, содержащий данные нового пользователя
     * @return сохраненный пользователь в виде UserDTO
     * @throws RoleCreationException если роль не найдена после попытки создать ее
     * @throws UserSaveException   с сообщением "Ошибка при сохранении пользователя"
     */
    @Override
    @Transactional
    public UserDTO saveUser(User user) {
        logger.debug("Запрос на сохранение пользователя: {}", user);

        try {
            user = roleService.validateRoles(user);
            user.setPassword(passwordService.encodePassword(user.getPassword()));

            User savedUser = userRepository.save(user);
            UserDTO userDTO = userMapperWrapper.convertToUserDTO(savedUser);

            logger.info("Пользователь успешно сохранен: {}", userDTO);
            return userDTO;

        } catch (RoleCreationException e) {
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
     * Пробрасывает возможные исключения, пойманные при поиске пользователя по ID, валидации ролей, кодирования пароля, обновлении и конвертации пользователя.
     *
     * @param user объект User, содержащий обновленные данные пользователя
     * @return обновленный пользователь в виде UserDTO
     * @throws NoSuchUserException с сообщением "Пользователь с ID %d не найден"
     * @throws RoleCreationException "Ошибка при создании роли: "
     * @throws UserUpdateException с сообщением "Ошибка при обновлении пользователя"
     */
    @Override
    @Transactional
    public UserDTO updateUser(User user) {
        logger.debug("Запрос на обновление пользователя: {}", user);

        Long userId = user.getId();
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() ->
                        new NoSuchUserException(String.format(USER_WITH_THIS_ID_NOT_FOUND, userId)));

        try {
            user = roleService.validateRoles(user);
            user.setPassword(passwordService.passwordProcessing(existingUser, user));

            User updatedUser = userRepository.save(user);
            UserDTO userDTO = userMapperWrapper.convertToUserDTO(updatedUser);

            logger.info("Пользователь успешно обновлен: {}", userDTO);
            return userDTO;

        } catch (RoleCreationException e) {
            throw e; // Пробросить исключение, чтобы оно могло быть обработано в контроллере
        } catch (Exception e) {
            throw new UserUpdateException(ERROR_UPDATING_USER, e);
        }
    }
    /**
     * Удаляет пользователя из базы данных по его ID.
     * <p>
     * Метод выполняет поиск пользователя с помощью метода findById() из UserRepository. Если пользователь найден,
     * он каскадно удаляется из базы данных вместе с его ролями с помощью метода deleteById().
     * Если пользователь не найден, выбрасывается исключение NoSuchUserException с соответствующим сообщением.
     *
     * @param id идентификатор пользователя, который должен быть удален
     * @return сообщение об успешном удалении пользователя
     * @throws NoSuchUserException если пользователь с указанным ID не найден, выбрасывается исключение с сообщением "Пользователь с ID %d не найден"
     */
    @Override
    @Transactional
    public String deleteUser(long id) {
        logger.debug("Запрос на удаление пользователя: {}", id);

        return userRepository.findById(id)
                .map(user -> {
                    userRepository.deleteById(id);
                    String successMessage = String.format("Пользователь с ID = %d успешно удален", id);
                    logger.info(successMessage);
                    return successMessage;
                })
                .orElseThrow(() -> {
                    String errorMessage = String.format(USER_WITH_THIS_ID_NOT_FOUND, id);
                    logger.error(errorMessage);
                    return new NoSuchUserException(errorMessage);
                });
    }
}
