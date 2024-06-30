package ru.kata.spring.boot_security.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kata.spring.boot_security.demo.dto.NewUserDTO;
import ru.kata.spring.boot_security.demo.dto.UpdateUserDTO;
import ru.kata.spring.boot_security.demo.helper.ServiceHelper;
import ru.kata.spring.boot_security.demo.mapper.UserMapper;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ServiceHelper serviceHelper;

    @InjectMocks
    private UserService userService;

    private User user;
    private User existingUser;
    private User updatedUser;
    private UpdateUserDTO updateUserDTO;
    private NewUserDTO newUserDTO;

    private static final String USER_NOT_FOUND_MESSAGE = "Пользователь не найден: ";
    private static final String FIRST_NAME = "Василий";
    private static final String LAST_NAME = "Мешков";
    private static final String PHONE = "123-45-67";
    private static final String EMAIL = "VM@gmail.com";
    private static final Role ROLE_USER = new Role("ROLE_USER");
    private static final Set<Role> ROLES = Collections.singleton(ROLE_USER);


    /**
     * Метод setUp создает и инициализирует объекты, необходимые для тестовых методов.
     * Вызывается перед каждым тестовым методом для обеспечения изоляции тестов.
     */
    @BeforeEach
    void setUp() {
        user = createUser();
        existingUser = createUser();
        updatedUser = createUser();
        updateUserDTO = createUpdateUserDTO();
        newUserDTO = createNewUserDTO();
    }

    private User createUser() {
        return User.builder()
                .userName(FIRST_NAME)
                .lastName(LAST_NAME)
                .phoneNumber(PHONE)
                .email(EMAIL)
                .roles(ROLES)
                .build();
    }

    private UpdateUserDTO createUpdateUserDTO() {
        return UpdateUserDTO.builder()
                .userName(FIRST_NAME)
                .lastName(LAST_NAME)
                .phoneNumber(PHONE)
                .email(EMAIL)
                .roles(ROLES)
                .build();
    }

    private NewUserDTO createNewUserDTO() {
        return NewUserDTO.builder()
                .userName(FIRST_NAME)
                .lastName(LAST_NAME)
                .phoneNumber(PHONE)
                .email(EMAIL)
                .roles(ROLES)
                .build();
    }

    @Nested
    @DisplayName("Тесты для метода getAllUsers()")
    class GetAllUsersTests {

        /**
         * Mock-объекты для возврата списка пользователей и их конвертации в UpdateUserDTO.
         * Вызывает метод getAllUsers() и проверяет результат.
         */
        @Test
        @DisplayName("Возвращает список пользователей, если они существуют")
        public void getAllUsersTest1() {
            when(userRepository.findAllUsersWithRoles()).thenReturn(List.of(user));
            when(userMapper.convertToUpdateUserDTO(user)).thenReturn(updateUserDTO);

            List<UpdateUserDTO> result = userService.getAllUsers();

            assertAll(
                    () -> assertThat(result).isNotEmpty(),  // Проверка, что результат не пустой
                    () -> assertThat(result).hasSize(1),  // Проверка, что размер списка равен 1
                    () -> assertThat(result.get(0)).usingRecursiveComparison().isEqualTo(updateUserDTO)  // Проверка, что первый элемент списка соответствует ожидаемому DTO
            );
        }

        /**
         * Mock-объект для возврата пустого списка пользователей.
         * Вызывает метод getAllUsers() и проверяет результат.
         */
        @Test
        @DisplayName("Возвращает пустой список, если пользователей нет в базе данных")
        public void getAllUsersTest2() {
            when(userRepository.findAllUsersWithRoles()).thenReturn(Collections.emptyList());

            List<UpdateUserDTO> result = userService.getAllUsers();

            assertThat(result).isEmpty();
        }
    }


    @Nested
    @DisplayName("Тесты для метода getUserById()")
    class GetUserByIdTests {

        /**
         * Mock-объекты для возврата пользователя и его конвертации в UpdateUserDTO.
         * Вызывает метод getUserById() и проверяет результат.
         */
        @ParameterizedTest
        @ValueSource(longs = {1L, 2L})
        @DisplayName("Возвращает пользователя по ID, если он существует")
        public void getUserByIdTest1(long userId) {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userMapper.convertToUpdateUserDTO(user)).thenReturn(updateUserDTO);

            UpdateUserDTO result = userService.getUserById(userId);

            assertThat(result).isNotNull();
            assertThat(result).usingRecursiveComparison().isEqualTo(updateUserDTO);
        }

        /**
         * Mock-объект для возврата пустого Optional при поиске пользователя по ID.
         * Вызывает метод getUserById() и проверяет, что выбрасывается исключение IllegalArgumentException с ожидаемым сообщением.
         * Проверяет, что метод findById() был вызван, а метод convertToUpdateUserDTO() не был вызван.
         */
        @ParameterizedTest
        @ValueSource(longs = {1L, 2L})
        @DisplayName("Выбрасывает исключение, если пользователь с указанным ID не существует")
        public void getUserByIdTest2(long userId) {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(userId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(USER_NOT_FOUND_MESSAGE + userId);

            verify(userRepository).findById(userId);
            verify(userMapper, never()).convertToUpdateUserDTO(any());
        }
    }


    @Nested
    @DisplayName("Тесты для метода updateUser()")
    class UpdateUserTests {

        /**
         * Устанавливает ID и текущий пароль для существующего пользователя.
         * Устанавливает ID updateUserDTO и новый пароль в updateUserDTO, чтобы метод passwordProcessing изменил и закодировал пароль.
         * Mock-объекты для возврата Optional с существующим пользователем, обновленного и конвертированного пользователя, возврата нового пароля.
         * Вызывает метод updateUser.
         * Проверяет, что были вызваны findById, convertToUser, passwordProcessing.
         * Проверяет, что пароль обновленного пользователя равен новому паролю.
         */

        @ParameterizedTest
        @CsvSource({
                "1, currentPassword, newPassword"
        })
        @DisplayName("Обновляет существующего пользователя c обновленным паролем. Устанавливается и кодируется обновленный пароль.")
        void updateUserChangePasswordTest(long userId, String currentPassword, String newPassword) {
            existingUser.setId(userId);
            existingUser.setPassword(currentPassword); // Текущий пароль пользователя

            updateUserDTO.setId(userId);
            updateUserDTO.setPassword(newPassword); // Новый пароль пользователя

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userMapper.convertToUser(updateUserDTO)).thenReturn(updatedUser);
            when(serviceHelper.passwordProcessing(existingUser, updatedUser)).thenReturn(newPassword);

            userService.updateUser(updateUserDTO);

            verify(userRepository).findById(userId);
            verify(userMapper).convertToUser(updateUserDTO);
            verify(serviceHelper).passwordProcessing(existingUser, updatedUser);
            verify(userRepository).save(updatedUser);

            assertEquals(newPassword, updatedUser.getPassword()); // Проверяем, что пароль был закодирован и установлен правильно
        }

        /**
         * Устанавливает ID и текущий пароль для существующего пользователя.
         * Устанавливает ID updateUserDTO. И не устанавливает новый пароль в updateUserDTO, чтобы метод passwordProcessing вернул текущий пароль.
         * Mock-объекты для возврата Optional с существующим пользователем, обновленного и конвертированного пользователя, возврата текущего пароля.
         * Вызывает метод updateUser.
         * Проверяет, что были вызваны findById, convertToUser, passwordProcessing.
         * Проверяет, что пароль обновленного пользователя равен текущему паролю.
         */
        @ParameterizedTest
        @CsvSource({
                "1, currentPassword"
        })
        @DisplayName("Обновляет существующего пользователя без обновления пароля. Устанавливается прежний пароль")
        void updateUserNotChangePasswordTest(long userId, String currentPassword) {
            existingUser.setId(userId);
            existingUser.setPassword(currentPassword); // Текущий пароль пользователя

            updateUserDTO.setId(userId);
            updateUserDTO.setPassword(null); // Явно показываем, что новый пароль не установлен

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userMapper.convertToUser(updateUserDTO)).thenReturn(updatedUser);
            when(serviceHelper.passwordProcessing(existingUser, updatedUser)).thenReturn(currentPassword);

            userService.updateUser(updateUserDTO);

            verify(userRepository).findById(userId);
            verify(userMapper).convertToUser(updateUserDTO);
            verify(serviceHelper).passwordProcessing(existingUser, updatedUser);
            verify(userRepository).save(updatedUser);

            assertEquals(currentPassword, updatedUser.getPassword());
        }


        /**
         * Mock-объект для возврата пустого Optional при поиске пользователя по ID.
         * Устанавливает ID пользователя.
         * Вызывает метод updateUser() и проверяет, что выбрасывается исключение IllegalArgumentException с ожидаемым сообщением.
         * Проверяет, что метод findById() был вызван.
         * Проверяет, что метод save() не был вызван.
         */
        @ParameterizedTest
        @ValueSource(longs = {1L, 2L})
        @DisplayName("Выбрасывает исключение при обновлении несуществующего пользователя")
        public void updateUserErrorTest(long userId) {
            updateUserDTO.setId(userId);

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(updateUserDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(USER_NOT_FOUND_MESSAGE + userId);

            verify(userRepository).findById(userId);
            verify(userRepository, never()).save(any(User.class));
        }
    }


    @Nested
    @DisplayName("Тесты для метода saveUser()")
    class SaveUserTests {

        /**
         * Устанавливает пароль.
         * Mock-объекты для конвертации NewUserDTO в User и кодирования пароля.
         * Вызывает метод saveUser().
         * Проверяет, что пользователь был сохранен.
         * Проверяет, что пароль пользователя был корректно закодирован.
         */
        @ParameterizedTest
        @CsvSource({
                "1, $2a$10$J/DOTcrDepfrWmJGqROBfuN3c8Y1xuAoXgan7HatBlhaq1eg2ghHG",
                "2, $2a$10$hY3SdviJio/PPUBmZN7M2.UUliSrVBKkXO7krDqwmCuaK33jLrjQ."
        })
        @DisplayName("Сохраняет пользователя и проверяет кодирование пароля")
        public void saveUserTest(String password, String encodedPassword) {
            newUserDTO.setPassword(password);
            user.setPassword(password);

            when(userMapper.convertToUser(newUserDTO)).thenReturn(user);
            when(serviceHelper.passwordEncoding(password)).thenReturn(encodedPassword);

            userService.saveUser(newUserDTO);

            verify(userRepository).save(user);
            assertThat(user.getPassword()).isEqualTo(encodedPassword);
        }
    }


    @Nested
    @DisplayName("Тесты для метода deleteUser()")
    class DeleteUserTests {

        /**
         * Mock-объект для возврата true при проверке существования пользователя по ID.
         * Вызывает метод deleteUser().
         * Проверяет, что метод deleteById() репозитория был вызван с правильным ID.
         */
        @ParameterizedTest
        @ValueSource(longs = {1L, 2L})
        @DisplayName("Удаляет существующего пользователя")
        public void deleteUserTest(long userId) {
            when(userRepository.existsById(userId)).thenReturn(true);

            userService.deleteUser(userId);

            verify(userRepository).deleteById(userId);
        }

        /**
         * Mock-объект для возврата false при проверке существования пользователя по ID.
         * Вызывает метод deleteUser() и проверяет, что выбрасывается исключение IllegalArgumentException с ожидаемым сообщением.
         * Проверяет, что метод deleteById() репозитория не был вызван.
         */
        @ParameterizedTest
        @ValueSource(longs = {1L, 2L})
        @DisplayName("Выбрасывает исключение при удалении несуществующего пользователя")
        public void deleteUserErrorTest(long userId) {
            when(userRepository.existsById(userId)).thenReturn(false);

            assertThatThrownBy(() -> userService.deleteUser(userId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(USER_NOT_FOUND_MESSAGE + userId);

            verify(userRepository, never()).deleteById(userId);
        }
    }
}
