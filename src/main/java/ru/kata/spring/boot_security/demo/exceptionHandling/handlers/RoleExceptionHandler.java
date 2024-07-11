package ru.kata.spring.boot_security.demo.exceptionHandling.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.kata.spring.boot_security.demo.exceptionHandling.exception.NoSuchRoleException;
import ru.kata.spring.boot_security.demo.exceptionHandling.exception.UserIncorrectData;

/**
 * Класс помеченный аннотацией @ControllerAdvice отвечает за глобальную поимку исключений, выброшенных контроллерами.
 * Методы класса, отлавливают и обрабатывают конкретные исключения, которые создаются и пробрасываются в методах сервисов с переданным
 * в них сообщениями. Возвращает в Http ответе ResponseEntity в теле которого содержатся сообщения парсированные в формат Json.
 */
@ControllerAdvice
public class RoleExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RoleExceptionHandler.class);

    /**
     * Обрабатывает исключение NoSuchRoleException, возникающее, когда роль не найдена в базе данных.
     *
     * @param exception исключение типа RoleCreationException.
     * @return ResponseEntity, содержащий объект UserIncorrectData и статус HTTP ответа NOT_FOUND (404).
     */
    @ExceptionHandler
    public ResponseEntity<UserIncorrectData> handleException(NoSuchRoleException exception) {
        logger.error("Произошла ошибка: роль не найдена: ", exception);
        UserIncorrectData data = new UserIncorrectData();
        data.setInfo(exception.getMessage());

        return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
    }
}
