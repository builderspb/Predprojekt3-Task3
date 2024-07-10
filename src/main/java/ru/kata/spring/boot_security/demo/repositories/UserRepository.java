package ru.kata.spring.boot_security.demo.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Выбирает пользователя с заданным именем.
     *
     * @param userName имя пользователя
     * @return List<User> список пользователей с заданным именем
     */
    List<User> findByUserName(String userName);


    /**
     * Находит пользователя по ID вместе с его ролями.
     *
     * Первый способ --@Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")-- с аннотацией @Query и JPQL запросом, не имеет смысла,
     * т.к. @EntityGraph и стандартный метод JPA вполне справляется с этой задачей.
     * Второй способ с использованием @EntityGraph(attributePaths = "roles").
     *
     * @param id ID пользователя
     * @return Optional<User> пользователь с его ролями, если найден
     */
    @EntityGraph(attributePaths = "roles")
    @NonNull
    Optional<User> findById(@NonNull Long id);


    /**
     * Получает всех пользователей вместе с их ролями, удаляя дубликаты.
     *
     * Если пользователю назначено несколько ролей, тогда при использовании только @EntityGraph, в результате запроса такие пользователи будут включены несколько раз.
     * Добавляя аннотацию @Query с JPQL запросом "SELECT DISTINCT u FROM User u", мы говорим, что мы удаляем дубликаты из результата.
     *
     * @return List<User> список пользователей с их ролями
     */
    @EntityGraph(attributePaths = "roles")
    @Query("SELECT DISTINCT u FROM User u")
    @NonNull
    List<User> findAll();


    /**
     * Выбирает пользователя с заданным именем, вместе с его ролями.
     *
     * @param userName имя пользователя
     * @return Optional<User> пользователь с его ролями, если найден
     */
    @EntityGraph(attributePaths = "roles")
    Optional<User> findUserWithRolesByUserName(String userName);
}