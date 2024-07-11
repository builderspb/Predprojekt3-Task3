package ru.kata.spring.boot_security.demo.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.exceptionHandling.exception.NoSuchRoleException;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private static final String THE_ROLE_MUST_EXIST = "Роль %s должна существовать после перехвата исключения DataIntegrityViolationException";


    /**
     * Проверяет существование ролей в базе данных и создает новые роли, если они не существуют.
     * <p>
     * Использует стрим для обработки каждой роли с помощью метода findOrCreateRole и собирает результат в набор.
     *
     * @param user набор ролей
     * @return Set<Role> набор проверенных ролей
     */
    public User validateRoles(User user) {
        Set<Role> updatedRoles = user.getRoles().stream()
                .map(this::findOrCreateRole)
                .collect(Collectors.toSet());
        user.setRoles(updatedRoles);
        return user;
    }


    /**
     * Получает существующую роль или создает новую, если она не существует.
     * <p>
     * Ищет роль по имени в базе данных, с помощью метода findRoleByName из UserRepository
     * Если роль не найдена, пытается сохранить новую роль, с помощью метода save
     * В случае возникновения DataIntegrityViolationException (ошибка уникальности), повторно пытается найти роль по имени.
     * DataIntegrityViolationException может возникнуть при добавлении новой роли в момент когда другая транзакция уже добавила такую же роль.
     * Если роль не найдена после повторного поиска, выбрасывает исключение NoSuchRoleException с сообщением
     * "Роль %s должна существовать после перехвата исключения DataIntegrityViolationException", что говорит о неполадках в других частях кода
     *
     * @param role роль, которую необходимо найти или создать
     * @return Role найденная или созданная роль
     * @throws NoSuchRoleException если роль не найдена после попытки создать ее
     */
    private Role findOrCreateRole(Role role) {
        return roleRepository.findRoleByName(role.getName())
                .orElseGet(() -> {
                    try {
                        return roleRepository.save(role);
                    } catch (DataIntegrityViolationException e) {
                        // Если происходит ошибка уникальности, повторно найти роль
                        return roleRepository.findRoleByName(role.getName())
                                .orElseThrow(() -> new NoSuchRoleException(String.format(THE_ROLE_MUST_EXIST, role.getName())));
                    }
                });
    }

}