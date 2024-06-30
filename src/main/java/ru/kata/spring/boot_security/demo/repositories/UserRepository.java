package ru.kata.spring.boot_security.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUserName(String userName);


    /**
     * DISTINCT означает, что мы удаляем дубликаты из результата. Это полезно, когда у пользователя может быть несколько ролей,
     * чтобы один и тот же пользователь не появлялся в результате несколько раз.
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles")
    List<User> findAllUsersWithRoles();


    /**
     * Выбирает пользователя с заданным именем пользователя вместе с его ролями.
     * LEFT JOIN FETCH используется для загрузки ролей пользователя в рамках одного запроса, чтобы избежать проблемы N+1.
     * WHERE u.userName = :userName ограничивает результат пользователем с указанным именем пользователя.
     * Результат оборачивается в Optional, чтобы учесть возможность отсутствия пользователя с таким именем.
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.userName = :userName")
    Optional<User> findByUserNameWithRoles(@Param("userName") String userName);
}