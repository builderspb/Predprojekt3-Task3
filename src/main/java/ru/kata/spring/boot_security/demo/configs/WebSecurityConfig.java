package ru.kata.spring.boot_security.demo.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Комплексная настройка безопасности, аутентификация, авторизация, защитой от атак и управлением сессиями.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;

    /**
     * Настраивает безопасность HTTP-запросов.
     *
     * @param http объект HttpSecurity, используемый для настройки безопасности HTTP-запросов
     * @throws Exception если возникает ошибка конфигурации безопасности
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests() // Настройка авторизации запросов
                .antMatchers("/login", "/logout").permitAll() // Разрешить доступ к страницам логина и логаута всем пользователям
                .antMatchers("/api/user").hasAnyAuthority("USER", "ADMIN") // Ограничить доступ к страницам /user пользователям с ролями USER или ADMIN
                .antMatchers("/api/users/**").hasAuthority("ADMIN") // Ограничить доступ к REST API /api/users/** только пользователям с ролью ADMIN
                .anyRequest().authenticated() // Все остальные запросы требуют аутентификации
                .and()
                .httpBasic() // Использовать HTTP Basic Authentication для REST API
                .and()
                .csrf().disable() // Отключить защиту от CSRF атак для REST API
                .sessionManagement() // Настройка управления сессиями
                .sessionFixation().migrateSession() // Создать новую сессию при аутентификации, чтобы предотвратить атаки с фиксацией сессии
                .maximumSessions(1) // Ограничить количество одновременных сессий для одного пользователя до одной
                .expiredUrl("/login?expired=true"); // Указать URL для перенаправления, если сессия пользователя истекла
    }


    /**
     * Настраивает AuthenticationManagerBuilder для использования пользовательского UserDetailsService и BCryptPasswordEncoder.
     * <p>
     * Необходимо для аутентификации пользователей с использованием данных из базы данных и безопасного хранения паролей.
     *
     * @param auth объект AuthenticationManagerBuilder, используемый для настройки аутентификации
     * @throws Exception если возникает ошибка конфигурации аутентификации
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }


    /**
     * Создает и настраивает бин для кодирования паролей с использованием BCryptPasswordEncoder.
     * <p>
     * Метод используется для обеспечения безопасности хранения паролей путем их кодирования перед сохранением в базу данных.
     *
     * @return BCryptPasswordEncoder бин для кодирования паролей
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


//    /**
//     * Создает и настраивает бин для маппинга между моделями и DTO с использованием ModelMapper.
//     * <p>
//     * Метод используется для упрощения преобразования между объектами модели и их представлениями в виде DTO.
//     *
//     * @return ModelMapper бин для маппинга между моделями и DTO
//     */
//    @Bean
//    public ModelMapper modelMapper() {
//        return new ModelMapper();
//    }

    /**
     * Конфигурирует параметры сессии для приложения.
     * <p>
     * Метод создает и настраивает бин для инициализации контекста сервлета.
     * Устанавливает максимальный срок действия сессионных cookie и конфигурирует их как HttpOnly для повышения безопасности.
     *
     * @return ServletContextInitializer бин для инициализации контекста сервлета
     */
    @Bean
    public ServletContextInitializer initializer() {
        return servletContext -> {
            servletContext.getSessionCookieConfig().setMaxAge(1800); // 30 минут
            // Устанавливает cookie как HttpOnly, что предотвращает доступ к нему из JavaScript. Это делает cookie менее уязвимым для атак через XSS.
            servletContext.getSessionCookieConfig().setHttpOnly(true);
        };
    }
}
