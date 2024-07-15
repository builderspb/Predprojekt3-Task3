package ru.kata.spring.boot_security.demo.exception.exception;

/**
 * Исключение NoSuchUserException наследуется от RuntimeException.
 * <p>
 * Служит для обработки ситуации, когда пользователь отсутствует в базе данных.
 */
public class NoSuchUserException extends RuntimeException {

    /**
     * Конструктор, принимающий сообщение об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public NoSuchUserException(String message) {
        super(message);

    }

}