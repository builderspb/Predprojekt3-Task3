package ru.kata.spring.boot_security.demo.mapper;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.model.User;

/**
 * Преобразование между объектами User, UpdateUserDTO и NewUserDTO с использованием ModelMapper.
 */
@Component
@AllArgsConstructor
public class UserMapper {
    private final ModelMapper modelMapper;


    /**
     * Преобразует объект в UserDTO.
     *
     * @param <T> тип исходного объекта
     * @param User исходный объект
     * @return UserDTO
     */
    public <T> UserDTO convertToUserDTO(T User) {
        return modelMapper.map(User, UserDTO.class);
    }

    /**
     * Преобразует объект в User.
     *
     * @param <T> тип исходного объекта
     * @param userDTO исходный объект
     * @return User
     */
    public <T>User convertToUser(T userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

}




