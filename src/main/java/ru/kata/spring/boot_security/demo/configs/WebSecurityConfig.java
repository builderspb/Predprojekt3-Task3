package ru.kata.spring.boot_security.demo.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;


/**
 * Комплексная настройка безопасности, аутентификация, авторизация, защитой от атак и управлением сессиями.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final UserDetailsService userDetailsService;


    /**
     * Настраивает безопасность HTTP-запросов.
     *
     * @param http объект HttpSecurity, используемый для настройки безопасности HTTP-запросов
     * @throws Exception если возникает ошибка конфигурации безопасности
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Разрешить доступ к Swagger UI и OpenAPI документации
                                .requestMatchers("/api/v1/user").hasAnyAuthority("USER", "ADMIN")
                                .requestMatchers("/api/v1/users/**").hasAuthority("ADMIN")
                                .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::migrateSession)
                                .maximumSessions(1)
                                .sessionRegistry(new SessionRegistryImpl())
                                .expiredUrl("/login?expired=true")
                );
        return http.build();
    }


    /**
     * Настраивает AuthenticationManager для использования пользовательского UserDetailsService и BCryptPasswordEncoder.
     * <p>
     * Необходимо для аутентификации пользователей с использованием данных из базы данных и безопасного хранения паролей.
     *
     * @param authConfig объект AuthenticationConfiguration, используемый для настройки аутентификации
     * @throws Exception если возникает ошибка конфигурации аутентификации
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
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