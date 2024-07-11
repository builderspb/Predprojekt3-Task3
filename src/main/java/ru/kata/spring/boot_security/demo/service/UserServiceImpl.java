package ru.kata.spring.boot_security.demo.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.exceptionHandling.exception.NoSuchRoleException;
import ru.kata.spring.boot_security.demo.exceptionHandling.exception.NoSuchUserException;
import ru.kata.spring.boot_security.demo.exceptionHandling.exception.UserSaveException;
import ru.kata.spring.boot_security.demo.exceptionHandling.exception.UserUpdateException;
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

    @Override
    public List<UserDTO> getAllUsers() {
        logger.debug("Запрос на получение всех пользователей");
        List<User> users = userRepository.findAll();

        // Проверяем, пуст ли список пользователей
        if (users.isEmpty()) {
            logger.warn(USER_LIST_IS_EMPTY);
            throw new NoSuchUserException(USER_LIST_IS_EMPTY);
        }

        List<UserDTO> userDTO = users.stream()
                .map(userMapperWrapper::convertToUserDTO)
                .sorted(Comparator.comparing(UserDTO::getId)).toList();

        logger.info("Получен список всех пользователей: {}", userDTO);
        return userDTO;
    }


    @Override
    public UserDTO getUserById(long id) {
        logger.debug("Запрос на получение пользователя с ID = {}", id);
        UserDTO userDTO = userRepository.findById(id)
                .map(userMapperWrapper::convertToUserDTO)
                .orElseThrow(() -> {
                    logger.warn(String.format(USER_WITH_THIS_ID_NOT_FOUND, id));
                    return new NoSuchUserException(String.format(USER_WITH_THIS_ID_NOT_FOUND, id));
                });
        logger.info("Пользователь с ID = {} успешно найден: {}", id, userDTO);
        return userDTO;
    }


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
        } catch (NoSuchRoleException e) {
            logger.error("Ошибка при валидации ролей: ", e);
            throw e; // Пробросить исключение, чтобы оно могло быть обработано в контроллере
        } catch (Exception e) {
            logger.error(ERROR_SAVING_USER, e);
            throw new UserSaveException(ERROR_SAVING_USER);
        }
    }


    @Override
    @Transactional
    public UserDTO updateUser(User user) {
        logger.debug("Запрос на обновление пользователя: {}", user);
        try {
            Long userId = user.getId();
            User existingUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> {
                        logger.warn(String.format(USER_WITH_THIS_ID_NOT_FOUND, userId));
                        return new NoSuchUserException(String.format(USER_WITH_THIS_ID_NOT_FOUND, userId));
                    });

            user = roleService.validateRoles(user);
            user.setPassword(passwordService.passwordProcessing(existingUser, user));

            User updatedUser = userRepository.save(user);
            UserDTO userDTO = userMapperWrapper.convertToUserDTO(updatedUser);

            logger.info("Пользователь успешно обновлен: {}", userDTO);
            return userDTO;
        } catch (NoSuchRoleException e) {
            logger.error("Ошибка при валидации ролей: ", e);
            throw e; // Пробросить исключение, чтобы оно могло быть обработано в контроллере
        } catch (Exception e) {
            logger.error(ERROR_UPDATING_USER, e);
            throw new UserUpdateException(ERROR_UPDATING_USER, e);
        }
    }


    @Override
    @Transactional
    public void deleteUser(long id) {
        logger.debug("Запрос на удаление пользователя с ID = {}", id);
        userRepository.findById(id)
                .ifPresentOrElse(
                        user ->
                        {
                            userRepository.deleteById(id);
                            logger.info("Пользователь с ID = {} успешно удален", id);
                        },
                        () -> {
                            logger.warn(String.format(USER_WITH_THIS_ID_NOT_FOUND, id));
                            throw new NoSuchUserException(String.format(USER_WITH_THIS_ID_NOT_FOUND, id));
                        }
                );
    }

}
