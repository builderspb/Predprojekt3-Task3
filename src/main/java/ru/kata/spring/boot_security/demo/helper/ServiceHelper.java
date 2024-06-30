package ru.kata.spring.boot_security.demo.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.User;

/**
 * Обработка паролей
 */
@Component
@RequiredArgsConstructor
public class ServiceHelper {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    /**
     * Запускает цепочку методов. Общая обработка пароля
     * Если пароль не поступит (будет null или пустым), возвращается текущий пароль из existingUser.
     */
    public String passwordProcessing(User existingUser, User updatedUser) {
        if (isPasswordChanged(updatedUser)) {
            return passwordEncoding(updatedUser.getPassword());
        } else {
            return existingUser.getPassword();
        }
    }


    /**
     * Проверка был ли пароль изменен
     */
    public boolean isPasswordChanged(User updatedUser) {
        return updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty();
    }


    /**
     * Кодировка паролей
     */
    public String passwordEncoding(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

}
