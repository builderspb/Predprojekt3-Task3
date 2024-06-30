package ru.kata.spring.boot_security.demo.configs;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final SuccessUserHandler loginUserHandler;
    private final UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests() // Настройка авторизации запросов
                .antMatchers("/login", "/logout").permitAll() // Разрешить доступ к страницам логина и логаута всем пользователям
                .antMatchers("/admin/**").hasAuthority("ADMIN") // Ограничить доступ к страницам /admin/** только пользователям с ролью ADMIN
                .antMatchers("/user/**").hasAnyAuthority("USER", "ADMIN") // Ограничить доступ к страницам /user/** пользователям с ролями USER или ADMIN
                .anyRequest().authenticated() // Все остальные запросы требуют аутентификации
                .and()
                .formLogin() // Настройка формы логина
                .loginPage("/login") // Указать URL страницы логина
                .failureUrl("/login?error=true") // Указать URL для перенаправления при неудачной авторизации
                .successHandler(loginUserHandler) // Указать обработчик успешного логина
                .permitAll() // Разрешить доступ к форме логина всем пользователям
                .and()
                .logout() // Настройка логаута
                .logoutUrl("/logout") // Указать URL для логаута
                .logoutSuccessUrl("/login?logout") // Указать URL для перенаправления после успешного логаута
                .invalidateHttpSession(true) // Инвалидировать текущую сессию при логауте
                .deleteCookies("JSESSIONID") // Удалить куки JSESSIONID при логауте
                .permitAll() // Разрешить доступ к логауту всем пользователям
                .and()
                .csrf() // Включить защиту от CSRF атак
                .and()
                .sessionManagement() // Настройка управления сессиями
                .sessionFixation().migrateSession() // Создать новую сессию при аутентификации, чтобы предотвратить атаки с фиксацией сессии
                .maximumSessions(1) // Ограничить количество одновременных сессий для одного пользователя до одной
                .expiredUrl("/login?expired=true"); // Указать URL для перенаправления, если сессия пользователя истекла
    }


    /**
     * Настраивает AuthenticationManagerBuilder для использования пользовательского UserDetailsService и BCryptPasswordEncoder.
     * Необходимо для аутентификации пользователей с использованием данных из базы данных и безопасного хранения паролей.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }


    /**
     *Кодирование паролей
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * Маппер для модель/ДТО
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }


    /**
     *Конфигурация сессии
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
