package ru.kata.spring.boot_security.demo.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    /**
     * Запускает цепочку методов. Обрабатывает пароль пользователя.
     * Если пароль не изменился (будет null или пустым), возвращает текущий пароль из existingUser.
     *
     * @param existingUser текущий пользователь
     * @param updatedUser  обновленный пользователь
     * @return String обработанный пароль
     */
    public String passwordProcessing(User existingUser, User updatedUser) {
        return isPasswordChanged(updatedUser) ? encodePassword(updatedUser.getPassword()) : existingUser.getPassword();
    }


    /**
     * Проверяет, был ли пароль изменен.
     * <p>
     * Optional.ofNullable(updatedUser.getPassword()) Optional который может содержать значение пароля или быть пустым, если пароль равен null.
     * .filter(password -> !password.isEmpty()) фильтр проверяет, что пароль не пустой. Если пароль пустой, Optional станет пустым.
     * .isPresent() возвращает true, если Optional содержит значение (т.е. пароль не null и не пустой), и false в противном случае.
     *
     * @param updatedUser обновленный пользователь
     * @return boolean true, если пароль изменен, иначе false
     */
    private boolean isPasswordChanged(User updatedUser) {
        return Optional.ofNullable(updatedUser.getPassword()).filter(password -> !password.isEmpty()).isPresent();
    }


    /**
     * Кодирует пароль.
     *
     * @param password исходный пароль
     * @return String закодированный пароль
     */
    public String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }
}
