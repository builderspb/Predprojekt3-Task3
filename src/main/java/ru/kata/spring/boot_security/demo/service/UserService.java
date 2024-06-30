package ru.kata.spring.boot_security.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dto.NewUserDTO;
import ru.kata.spring.boot_security.demo.dto.UpdateUserDTO;
import ru.kata.spring.boot_security.demo.helper.ServiceHelper;
import ru.kata.spring.boot_security.demo.mapper.UserMapper;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private static final String USER_IS_NOT_FOUND = "Пользователь не найден: ";
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ServiceHelper serviceHelper;


    /**
     * 1. Получает всех пользователей вместе с их ролями из базы данных.
     * 2. Метод findAllUsersWithRoles() из UserRepository возвращает список пользователей с их ролями.
     * 3. Каждый пользователь преобразуется в объект UpdateUserDTO с помощью метода convertToUpdateUserDTO() из UserMapper.
     * 4. Результат собирается в изменяемый список с использованием Collectors.toList().
     */
    public List<UpdateUserDTO> getAllUsers() {
        return userRepository.findAllUsersWithRoles().stream()
                .map(userMapper::convertToUpdateUserDTO)
                .collect(Collectors.toList());
    }


    /**
     * 1. Получает пользователя по его ID.
     * 2. Метод findById() из UserRepository возвращает Optional, содержащий пользователя, если он существует.
     * 3. Если пользователь не найден, выбрасывается исключение IllegalArgumentException с сообщением "Пользователь не найден".
     * 4. Пользователь преобразуется в объект UpdateUserDTO с помощью метода convertToUpdateUserDTO() из UserMapper.
     */
    public UpdateUserDTO getUserById(long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(USER_IS_NOT_FOUND + id));
        return userMapper.convertToUpdateUserDTO(user);
    }


    /**
     * 1. Сохраняет нового пользователя в БД.
     * 2. newUserDTO преобразуется в объект User с помощью метода convertToUser() из UserMapper.
     * 3. Пароль пользователя кодируется с использованием метода passwordEncoding() из ServiceHelper.
     * 4. Пользователь сохраняется в базе данных с помощью метода save() из UserRepository.
     */
    @Transactional
    public void saveUser(NewUserDTO newUserDTO) {
        var user = userMapper.convertToUser(newUserDTO);
        user.setPassword(serviceHelper.passwordEncoding(user.getPassword()));
        userRepository.save(user);
    }


    /**
     * 1. Обновляет существующего пользователя в БД.
     * 2. Ищется существующий пользователь по его ID с помощью метода findById() из UserRepository.
     * 3. Если пользователь не найден, выбрасывается исключение IllegalArgumentException с сообщением "Пользователь не найден".
     * 4. Затем updateUserDTO преобразуется в объект User с помощью метода convertToUser() из UserMapper.
     * 5. Пароль пользователя обрабатывается с использованием метода passwordProcessing() из ServiceHelper, который включает в себя проверку и кодировку пароля.
     * 6. Роли пользователя устанавливаются из updateUserDTO.
     * 7. Обновленный пользователь сохраняется в базе данных с помощью метода save() из UserRepository.
     */
    @Transactional
    public void updateUser(UpdateUserDTO updateUserDTO) {
        var existingUser = userRepository.findById(updateUserDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException(USER_IS_NOT_FOUND + updateUserDTO.getId()));

        var user = userMapper.convertToUser(updateUserDTO);
        user.setPassword(serviceHelper.passwordProcessing(existingUser, user)); // Используем вынесенный метод для обработки пароля. Метод содержит в себе два метода для кодировки и проверки пароля.
        user.setRoles(updateUserDTO.getRoles());
        userRepository.save(user);
    }


    /**
     * 1. Удаляет пользователя из базы данных по его ID.
     * 2. Проверяется существование пользователя с данным ID с помощью метода existsById() из UserRepository.
     * 3. Если пользователь не найден, выбрасывается исключение IllegalArgumentException с сообщением "Пользователь не найден".
     * 4. Пользователь удаляется из БД с помощью метода deleteById() из UserRepository.
     */
    @Transactional
    public void deleteUser(long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException(USER_IS_NOT_FOUND + id);
        }
        userRepository.deleteById(id);
    }

}
