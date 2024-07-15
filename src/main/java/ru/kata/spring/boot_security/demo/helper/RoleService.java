package ru.kata.spring.boot_security.demo.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.exception.exception.RoleCreationException;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private static final String ERROR_CREATING_ROLE = "Ошибка при создании роли: %s";


    /**
     * Проверяет существование ролей в базе данных и создает новые роли, если они не существуют.
     * <p>
     * Использует стрим для обработки каждой роли с помощью метода findOrCreateRole и собирает результат в набор.
     *
     * @param user набор ролей
     * @return Set<Role> набор проверенных ролей
     */
    public User validateRoles(User user) throws RoleCreationException  {
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
     * Если роль не найдена, сохраняет новую роль, с помощью метода save
     *
     * @param role роль, которую необходимо найти или создать
     * @return Role найденная или созданная роль
     * @throws RoleCreationException если роль не может быть создана
     */
    private Role findOrCreateRole(Role role) throws RoleCreationException {
        try {
            return roleRepository.findRoleByName(role.getName())
                    .orElseGet(() -> roleRepository.save(role));
        } catch (DataIntegrityViolationException e) {
            throw new RoleCreationException(String.format(ERROR_CREATING_ROLE, role));

        }
    }
}