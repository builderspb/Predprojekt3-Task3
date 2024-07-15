package ru.kata.spring.boot_security.demo.util.userValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Аннотация для валидации пароля.
 * <p>
 * Аннотация @PasswordConstraint используется для проверки того, что значение пароля не пустое.
 * Валидация выполняется с помощью класса PasswordValidator.
 * <p>
 * Аннотация может применяться к полям и другим аннотациям.
 */
@Documented // Аннотация будет включена в Javadoc.
@Constraint(validatedBy = PasswordValidator.class) // Ограничение и что валидация будет выполнена с помощью класса PasswordValidator.
@Target({ElementType.FIELD, ElementType.PARAMETER}) // Указывает, что аннотация может применяться к полям и параметрам методов
@Retention(RetentionPolicy.RUNTIME) // Указывает, что аннотация будет доступна во время выполнения через рефлексию
public @interface PasswordConstraint {
    /**
     * Сообщение об ошибке, которое будет показано, если значение не соответствует критериям валидации.
     * @return Сообщение об ошибке
     */
    String message() default "Пароль не может быть пустым";

    /**
     * Группы валидации, к которым относится это ограничение.
     * Используется для группировки ограничений в разные наборы и применения разных наборов ограничений в разных контекстах.
     * Параметр заполняется в месте где повесили аннотацию @Validated(ValidationGroups.Update.class)
     *
     * @return Массив классов групп валидации
     */
    Class<?>[] groups() default {};

    /**
     * Дополнительные данные о нарушении ограничения.
     * Используется для передачи метаданных о нарушении ограничения.
     * @return Массив классов полезной нагрузки
     */
    Class<? extends Payload>[] payload() default {};
}