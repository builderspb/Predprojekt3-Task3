package ru.kata.spring.boot_security.demo.exceptionHandling.exception;

/**
 * Исключение UserSaveException наследуется от RuntimeException.
 * <p>
 * Служит для обработки ситуаций, когда возникает ошибка при сохранении пользователя.
 */
public class UserSaveException extends RuntimeException {

    /**
     * Конструктор, принимающий сообщение об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public UserSaveException(String message) {
        super(message);
    }

    /**
     * Конструктор, принимающий сообщение об ошибке и причину.
     *
     * @param message сообщение об ошибке
     * @param cause   причина ошибки (исходное исключение, которое привело к этому исключению)
     */
    public UserSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}