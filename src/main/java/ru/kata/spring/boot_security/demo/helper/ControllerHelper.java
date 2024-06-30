package ru.kata.spring.boot_security.demo.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.dto.NewUserDTO;
import ru.kata.spring.boot_security.demo.dto.UpdateUserDTO;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Вспомогательные методы для работы с контроллерами, упрощают операции сохранения, обновления пользователей и сортировки пользователей по ID.
 */
@Component
@RequiredArgsConstructor
public class ControllerHelper {
    private final UserService userService;
    private final RoleRepository roleRepository;


    /**
     * Запускает цепочку методов.
     */
    public String handleUserSaveOrUpdate(Object userDTO,
                                         List<String> roles,
                                         RedirectAttributes redirectAttributes,
                                         boolean isNewUser) {
        try {
            Set<Role> roleSet = getRolesFromNames(roles); // Преобразование списка имен ролей в Set<Role>

            if (isNewUser) {
                handleNewUser((NewUserDTO) userDTO, roleSet, redirectAttributes); // Сохранение нового пользователя
            } else {
                handleUpdateUser((UpdateUserDTO) userDTO, roleSet, redirectAttributes); // Обновление существующего пользователя
            }

        } catch (RuntimeException e) {
            return handleException(e, userDTO, redirectAttributes, isNewUser);
        }
        return "redirect:/admin";
    }


    /**
     * Проверяет, существуют ли указанные роли в базе данных.
     * Преобразует список имен ролей в объект Role, складывает Set<Role>, что гарантирует отсутствие дубликатов.
     */
    private Set<Role> getRolesFromNames(List<String> roles) {
        return roles.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Роль не найдена: " + roleName)))
                .collect(Collectors.toSet());
    }


    /**
     * Сохраняет нового пользователя и добавляет сообщение об успешном добавлении.
     */
    private void handleNewUser(NewUserDTO newUserDTO, Set<Role> roleSet, RedirectAttributes redirectAttributes) {
        newUserDTO.setRoles(roleSet);
        userService.saveUser(newUserDTO);
        redirectAttributes.addFlashAttribute("message", "Пользователь успешно добавлен!");
        redirectAttributes.addFlashAttribute("addedUserId", newUserDTO.getId());
    }


    /**
     * Обновляет существующего пользователя и добавляет сообщение об успешном обновлении.
     */
    private void handleUpdateUser(UpdateUserDTO updateUserDTO, Set<Role> roleSet, RedirectAttributes redirectAttributes) {
        updateUserDTO.setRoles(roleSet);
        userService.updateUser(updateUserDTO);
        redirectAttributes.addFlashAttribute("message", "Пользователь обновлен успешно!");
        redirectAttributes.addFlashAttribute("updatedUserId", updateUserDTO.getId());
    }


    /**
     * Возвращает URL для перенаправления в зависимости от типа операции (создание или обновление).
     */
    private String handleException(RuntimeException e, Object userDTO, RedirectAttributes redirectAttributes, boolean isNewUser) {
        if (e instanceof IllegalArgumentException) {
            redirectAttributes.addFlashAttribute("message", "Неверные данные пользователя");
        } else {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return getRedirectUrl(userDTO, isNewUser);
    }


    /**
     * Обрабатывает исключения и возвращает URL для перенаправления.
     */
    private String getRedirectUrl(Object userDTO, boolean isNewUser) {
        return isNewUser ? "redirect:/addNewUser" : "redirect:/editUser/" + ((UpdateUserDTO) userDTO).getId();
    }


    /**
     * Сортирует список пользователей по их ID в порядке возрастания.
     */
    public List<UpdateUserDTO> sortUsersById(List<UpdateUserDTO> users) {
        users.sort(Comparator.comparingLong(UpdateUserDTO::getId));
        return users;
    }

}

