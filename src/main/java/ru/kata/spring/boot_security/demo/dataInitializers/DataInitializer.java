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
     * Запускает всю цепочку методов.
     */
    @Transactional
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeUserData();
        initializeUsers();
    }


    /**
     * Отвечает за создание ролей, если они еще не существуют.
     */
    private void initializeRoles() {
        createRoleIfNotFound("ADMIN");
        createRoleIfNotFound("USER");
    }


    /**
     * Проверяет существование ролей и создает их при необходимости.
     */
    private void createRoleIfNotFound(String roleName) {
        if (!roleRepository.existsByName(roleName)) {
            roleRepository.save(new Role(roleName));
        }
    }


    /**
     * Создает данные для двух пользователей (админа и обычного пользователя), используя роли, созданные на предыдущем шаге.
     */
    private void initializeUserData() {
        // Перезагружаем роли после их создания
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND));
        Role userRole = roleRepository.findByName("USER").orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND));

        ADMIN = new UserData(
                "Василий", "Иванов", "123-45-67", "admin@abc.com",
                bCryptPasswordEncoder.encode("1"), Set.of(adminRole, userRole));

        USER = new UserData(
                "Николай", "Севастьянов", "321-65-98", "user@abc.com",
                bCryptPasswordEncoder.encode("2"), Set.of(userRole));
    }


    /**
     * Создает пользователей в базе данных, используя данные, созданные на предыдущем шаге.
     */
    private void initializeUsers() {
        createUserIfNotFound(ADMIN);
        createUserIfNotFound(USER);
    }


    /**
     * Проверяет существование пользователей и создает их при необходимости.
     */
    private void createUserIfNotFound(UserData userData) {
        List<User> users = userRepository.findByUserName(userData.userName());
        if (users.isEmpty()) {
            User user = User.builder()
                    .userName(userData.userName())
                    .lastName(userData.lastName())
                    .phoneNumber(userData.phone())
                    .email(userData.email())
                    .password(userData.password())
                    .roles(userData.roles())
                    .build();
            userRepository.save(user);
        }
    }

}

