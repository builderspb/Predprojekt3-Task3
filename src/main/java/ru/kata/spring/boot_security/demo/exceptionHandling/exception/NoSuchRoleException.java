package ru.kata.spring.boot_security.demo.exceptionHandling.exception;

/**
 * Исключение NoSuchRoleException наследуется от RuntimeException.
 * <p>
 * Служит для обработки ситуации, когда роль отсутствует в базе данных.
 */
public class NoSuchRoleException extends RuntimeException {

    /**
     * Конструктор, принимающий сообщение об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public NoSuchRoleException(String message) {
        super(message);
    }

}
