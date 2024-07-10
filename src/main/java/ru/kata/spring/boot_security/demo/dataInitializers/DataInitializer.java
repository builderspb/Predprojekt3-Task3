package ru.kata.spring.boot_security.demo.dataInitializers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

/**
 * Инициализирует данные для приложения при запуске. Чтобы гарантировать наличие необходимых ролей и пользователей в БД
 */

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private static final String ROLE_NOT_FOUND = "Роль не найдена.";
    private static UserData ADMIN;
    private static UserData USER;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    /**
     * Запускает всю цепочку методов для инициализации базы данных.
     * <p>
     * Метод выполняется в транзакции и запускает следующие шаги инициализации:
     * 1. Создание ролей, если они еще не существуют.
     * 2. Создание данных для пользователей (администратора и обычного пользователя).
     * 3. Создание пользователей в базе данных, используя созданные данные.
     *
     * @param args аргументы командной строки (не используются)
     * @throws Exception если возникает ошибка во время выполнения методов инициализации
     */
    @Transactional
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeUserData();
        initializeUsers();
    }


    /**
     * Инициализирует роли, если они еще не существуют в базе данных.
     * <p>
     * Метод создает роли "ADMIN" и "USER", если они еще не существуют, вызывая метод createRoleIfNotFound.
     */
    private void initializeRoles() {
        createRoleIfNotFound("ADMIN");
        createRoleIfNotFound("USER");
    }


    /**
     * Проверяет существование роли и создает её при необходимости.
     * <p>
     * Метод проверяет, существует ли роль с указанным именем в базе данных.
     * Если роль не существует, создается и сохраняется новая роль.
     *
     * @param roleName имя роли, которую необходимо проверить и создать при необходимости
     */
    private void createRoleIfNotFound(String roleName) {
        if (!roleRepository.existsByName(roleName)) {
            roleRepository.save(new Role(roleName));
        }
    }


    /**
     * Создает данные для двух пользователей (администратора и обычного пользователя), используя роли, созданные на предыдущем шаге.
     * <p>
     * Метод перезагружает роли после их создания и создает объекты UserData для администратора и обычного пользователя,
     * включая их имена, фамилии, телефонные номера, электронные почты, пароли и наборы ролей.
     */
    private void initializeUserData() {
        // Перезагружаем роли после их создания
        Role adminRole = roleRepository.findRoleByName("ADMIN").orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND));
        Role userRole = roleRepository.findRoleByName("USER").orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND));

        ADMIN = new UserData(
                "Василий", "Иванов", "123-45-67", "admin@abc.com",
                bCryptPasswordEncoder.encode("1"), Set.of(adminRole, userRole));

        USER = new UserData(
                "Николай", "Севастьянов", "321-65-98", "user@abc.com",
                bCryptPasswordEncoder.encode("2"), Set.of(userRole));
    }


    /**
     * Создает пользователей в базе данных, используя данные, созданные на предыдущем шаге.
     * <p>
     * Метод инициализирует базу данных пользователями, вызывая метод createUserIfNotFound с предварительно определенными данными для администратора и пользователя.
     */
    private void initializeUsers() {
        createUserIfNotFound(ADMIN);
        createUserIfNotFound(USER);
    }


    /**
     * Проверяет существование пользователей и создает их при необходимости.
     * <p>
     * Метод ищет пользователей по имени пользователя (userName). Если пользователи с таким именем не найдены, создается и сохраняется новый пользователь.
     *
     * @param userData объект UserData, содержащий данные для создания нового пользователя
     */
    private void createUserIfNotFound(UserData userData) {
        List<User> users = userRepository.findByUserName(userData.getUserName());
        if (users.isEmpty()) {
            User user = User.builder()
                    .userName(userData.getUserName())
                    .lastName(userData.getLastName())
                    .phoneNumber(userData.getPhone())
                    .email(userData.getEmail())
                    .password(userData.getPassword())
                    .roles(userData.getRoles())
                    .build();
            userRepository.save(user);
        }
    }

}

