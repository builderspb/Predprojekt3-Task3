package ru.kata.spring.boot_security.demo.exception.exception;

/**
 * Исключение NoSuchRoleException наследуется от RuntimeException.
 * <p>
 * Служит для обработки ситуации, когда роль отсутствует в базе данных.
 */
public class RoleCreationException extends RuntimeException {

    /**
     * Конструктор, принимающий сообщение об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public RoleCreationException(String message) {
        super(message);
    }

}
