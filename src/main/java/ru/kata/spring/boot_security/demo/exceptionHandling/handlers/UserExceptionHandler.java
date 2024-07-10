package ru.kata.spring.boot_security.demo.exceptionHandling.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.kata.spring.boot_security.demo.exceptionHandling.exception.NoSuchUserException;
import ru.kata.spring.boot_security.demo.exceptionHandling.exception.UserIncorrectData;
import ru.kata.spring.boot_security.demo.exceptionHandling.exception.UserSaveException;
import ru.kata.spring.boot_security.demo.exceptionHandling.exception.UserUpdateException;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс помеченный аннотацией @ControllerAdvice отвечает за глобальную поимку исключений, выброшенных контроллерами.
 * Методы класса, отлавливают и обрабатывают конкретные исключения, которые создаются и пробрасываются в методах сервисов с переданным
 * в них сообщениями. Возвращает в Http ответе ResponseEntity в теле которого содержатся сообщения парсированные в формат Json.
 */
@ControllerAdvice
public class UserExceptionHandler {


    /**
     * Обрабатывает исключения валидации, возникающие при передаче некорректных данных в контроллер.
     *
     * @param ex исключение типа MethodArgumentNotValidException.
     * @return ResponseEntity, содержащий UserIncorrectData и статус HTTP ответа BAD_REQUEST (400). (Карта ошибок, где ключом является имя поля, а значением - сообщение об ошибке)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    /**
     * Обрабатывает исключения NoSuchUserException.
     *
     * Возвращает ResponseEntity, параметризованный типом UserIncorrectData (это класс который предназначен для парсирования
     * в Json, чтобы в Http ответе отправить сообщение об ошибке)
     *
     * @param exception исключение типа NoSuchUserException.
     * @return ResponseEntity, содержащий объект UserIncorrectData и статус HTTP ответа NOT_FOUND (404).
     */

    @ExceptionHandler(NoSuchUserException.class)
    public ResponseEntity<UserIncorrectData> handleException(NoSuchUserException exception) {
        // создается объект класса, чтобы передать ему сообщение об ошибке(далее он будет преобразован в Json для отправки в Http ответе)
        UserIncorrectData data = new UserIncorrectData();
        // Объекту класса передается сообщение
        data.setInfo(exception.getMessage());

        // возвращается ResponseEntity, в параметры которого передается объект класса (преобразуется в Json, и статус Http ответа)
        return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
    }



    /**
     * Обрабатывает исключения UserSaveException, возникающие при ошибках сохранения пользователя.
     *
     * @param exception исключение типа UserSaveException.
     * @return ResponseEntity, содержащий объект UserIncorrectData и статус HTTP ответа INTERNAL_SERVER_ERROR (500).
     */
    @ExceptionHandler(UserSaveException.class)
    public ResponseEntity<UserIncorrectData> handleUserSaveException(UserSaveException exception) {
        UserIncorrectData data = new UserIncorrectData();
        data.setInfo(exception.getMessage());
        return new ResponseEntity<>(data, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Обрабатывает исключения UserUpdateException, возникающие при ошибках обновления пользователя.
     *
     * @param exception исключение типа UserUpdateException.
     * @return ResponseEntity, содержащий объект UserIncorrectData и статус HTTP ответа INTERNAL_SERVER_ERROR (500).
     */
    @ExceptionHandler(UserUpdateException.class)
    public ResponseEntity<UserIncorrectData> handleUserUpdateException(UserUpdateException exception) {
        UserIncorrectData data = new UserIncorrectData();
        data.setInfo(exception.getMessage());
        return new ResponseEntity<>(data, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
