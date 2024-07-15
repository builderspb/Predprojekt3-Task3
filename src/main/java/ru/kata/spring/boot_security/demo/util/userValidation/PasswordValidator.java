package ru.kata.spring.boot_security.demo.util.userValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Валидатор для аннотации @PasswordConstraint.
 * <p>
 * Этот класс реализует интерфейс ConstraintValidator и обеспечивает логику проверки для аннотации @PasswordConstraint.
 * Проверяет, что значение строки не является null и не пустым.
 */
public class PasswordValidator implements ConstraintValidator<PasswordConstraint, String> {

    /**
     * Инициализация валидатора.
     * <p>
     * Этот метод вызывается при инициализации валидатора. В данном случае он не выполняет никаких действий.
     * Инициализация в методе initialize требуется, если аннотация имеет параметры например как в аннотации CheckEmail
     *
     * @param password экземпляр аннотации @PasswordConstraint
     */
    @Override
    public void initialize(PasswordConstraint password) {
        // Инициализация не требуется
    }

    /**
     * Логика проверки.
     * <p>
     * Этот метод вызывается для проверки значения. Он проверяет, что значение пароля не является null и не пустым.
     *
     * @param password значение, которое необходимо проверить
     * @param context  контекст для предоставления информации и операций для создания сообщения об ошибке
     * @return true, если значение пароля не null и не пустое, иначе false
     */
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return password != null && !password.trim().isEmpty();
    }
}