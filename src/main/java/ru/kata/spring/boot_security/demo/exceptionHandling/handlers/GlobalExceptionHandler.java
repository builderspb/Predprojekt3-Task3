package ru.kata.spring.boot_security.demo.exceptionHandling.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.kata.spring.boot_security.demo.exceptionHandling.exception.UserIncorrectData;

/**
 * Класс помеченный аннотацией @ControllerAdvice отвечает за глобальную поимку исключений, выброшенных контроллерами.
 * Методы класса, отлавливают и обрабатывают конкретные исключения, которые создаются и пробрасываются в методах сервисов с переданным
 * в них сообщениями. Возвращает в Http ответе ResponseEntity в теле которого содержатся сообщения парсированные в формат Json.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Обрабатывает любые другие исключения типа Exception. Может быть выброшено в различных ситуациях (например, если вместо ID ввести буквы).
     * Возвращает ResponseEntity, параметризованный типом UserIncorrectData
     *
     * @param exception исключение типа Exception,
     * @return ResponseEntity, содержащий объект UserIncorrectData и статус HTTP ответа INTERNAL_SERVER_ERROR (500).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<UserIncorrectData> handleException(Exception exception) {
        logger.error("Произошла необработанная ошибка: ", exception);
        UserIncorrectData data = new UserIncorrectData();
        data.setInfo(exception.getMessage());

        return new ResponseEntity<>(data, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
