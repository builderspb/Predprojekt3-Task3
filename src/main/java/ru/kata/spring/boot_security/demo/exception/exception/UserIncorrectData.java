package ru.kata.spring.boot_security.demo.exception.exception;

/**
 * Класс, используемый для отправки информации об ошибке в HTTP ответе в формате JSON.
 * <p>
 * Предназначен для обработки исключений и передачи информации об ошибке клиенту.
 */
public class UserIncorrectData {
    private String info;

    public UserIncorrectData() {
        // Конструктор по умолчанию необходим для сериализации/десериализации JSON
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}

