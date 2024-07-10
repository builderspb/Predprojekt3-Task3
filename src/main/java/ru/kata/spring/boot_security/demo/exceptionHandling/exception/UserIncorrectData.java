package ru.kata.spring.boot_security.demo.exceptionHandling.exception;

/**
 * Класс, используемый для отправки информации об ошибке в HTTP ответе в формате JSON.
 * <p>
 * Предназначен для обработки исключений и передачи информации об ошибке клиенту.
 */
public class UserIncorrectData {
    private String info;

    public UserIncorrectData() {
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}

