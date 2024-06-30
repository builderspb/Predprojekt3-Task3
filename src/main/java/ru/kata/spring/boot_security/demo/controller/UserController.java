package ru.kata.spring.boot_security.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.kata.spring.boot_security.demo.dto.NewUserDTO;
import ru.kata.spring.boot_security.demo.dto.UpdateUserDTO;
import ru.kata.spring.boot_security.demo.helper.ControllerHelper;
import ru.kata.spring.boot_security.demo.mapper.UserMapper;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.security.CustomUserDetails;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final ControllerHelper controllerHelper;


    /**
     * Загружает список всех пользователей и отображает страницу user-info.html.
     * Кнопка "Добавить нового пользователя" перенаправляет на контроллер /addNewUser.
     * Кнопка "Edit" перенаправляет на редактирование пользователя /editUser/{id}.
     */
    @GetMapping(path = "/admin", produces = "text/plain;charset=UTF-8")
    public String showAllUsers(Model model) {
        List<UpdateUserDTO> userDTO = controllerHelper.sortUsersById(userService.getAllUsers());
        List<User> users = userDTO.stream()
                .map(userMapper::convertToUser)
                .toList();
        model.addAttribute("users", users);
        return "user-info";
    }


    /**
     * Отображает форму для добавления нового пользователя на странице addNewUser.html.
     */
    @GetMapping(path = "/addNewUser", produces = "text/plain;charset=UTF-8")
    public String showAddUserForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new NewUserDTO());
        }
        return "addNewUser";
    }


    /**
     * Сохраняет нового пользователя и перенаправляет на главную страницу.
     */
    @PostMapping(path = "/saveUser", produces = "text/plain;charset=UTF-8")
    public String createUser(@Valid @ModelAttribute("user") NewUserDTO newUserDTO,
                             @RequestParam(value = "roles", required = false) List<String> roles,
                             RedirectAttributes redirectAttributes) {
        return controllerHelper.handleUserSaveOrUpdate(newUserDTO, roles, redirectAttributes, true); // Используем вынесенный метод для преобразования ролей и обработки ошибок.
    }


    /**
     * Принимает GET-запрос на URL /editUser/{id} и возвращает страницу с формой редактирования пользователя.
     */
    @GetMapping(path = "/editUser/{id}", produces = "text/plain;charset=UTF-8")
    public String showEditUserForm(@PathVariable long id, Model model) {
        UpdateUserDTO updateUserDTO = userService.getUserById(id);
        model.addAttribute("user", updateUserDTO);
        return "updateUser";
    }


    /**
     * Обновляет данные пользователя и перенаправляет на главную страницу.
     */
    @PostMapping(path = "/updateUser", produces = "text/plain;charset=UTF-8")
    public String updateUser(@Valid @ModelAttribute("user") UpdateUserDTO updateUserDTO,
                             @RequestParam(value = "roles", required = false) List<String> roles,
                             RedirectAttributes redirectAttributes) {
        return controllerHelper.handleUserSaveOrUpdate(updateUserDTO, roles, redirectAttributes, false); // Используем вынесенный метод для преобразования ролей и обработки ошибок.
    }


    /**
     * Удаляет пользователя и перенаправляет на главную страницу.
     */
    @DeleteMapping(path = "/deleteUser", produces = "text/plain;charset=UTF-8")
    public String removeUser(@RequestParam long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("message", "Пользователь успешно удален!");
        return "redirect:/admin";
    }


    /**
     * Отображает домашнюю страницу пользователя.
     */
    @GetMapping(path = "/user", produces = "text/plain;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String showUserHomePage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        UpdateUserDTO userDTO = userMapper.convertToUpdateUserDTO(customUserDetails.getUser());
        model.addAttribute("user", userDTO);
        return "user-home";
    }
}


