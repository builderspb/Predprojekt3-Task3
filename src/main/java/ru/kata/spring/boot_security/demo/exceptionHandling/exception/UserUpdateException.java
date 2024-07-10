package ru.kata.spring.boot_security.demo.exceptionHandling.exception;

/**
 * Исключение UserUpdateException наследуется от RuntimeException.
 * <p>
 * Служит для обработки ситуаций, когда возникает ошибка при обновлении пользователя.
 */
public class UserUpdateException extends RuntimeException {

    /**
     * Конструктор, принимающий сообщение об ошибке и причину.
     *
     * @param message сообщение об ошибке
     * @param cause   причина ошибки (исходное исключение, которое привело к этому исключению)
     */
    public UserUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}