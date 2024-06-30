package ru.kata.spring.boot_security.demo.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.model.Role;

import java.beans.PropertyEditorSupport;

/**
 * Преобразовывать строковые значения из формы в объекты Role при отправке формы.
 */

@Configuration
@ControllerAdvice
@RequiredArgsConstructor
public class WebDataBinderConfig {
    private final RoleRepository roleRepository;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Role.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                var role = roleRepository.findByName(text)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + text));
                setValue(role);
            }
        });
    }
}