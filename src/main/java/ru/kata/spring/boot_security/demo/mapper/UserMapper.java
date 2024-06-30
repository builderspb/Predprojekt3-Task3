package ru.kata.spring.boot_security.demo.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dto.NewUserDTO;
import ru.kata.spring.boot_security.demo.dto.UpdateUserDTO;
import ru.kata.spring.boot_security.demo.model.User;

/**
 * Преобразование между объектами User, UpdateUserDTO и NewUserDTO с использованием ModelMapper.
 */
@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ModelMapper modelMapper;

    public User convertToUser(UpdateUserDTO updateUserDTO) {
        return modelMapper.map(updateUserDTO, User.class);
    }

    public UpdateUserDTO convertToUpdateUserDTO(User user) {
        return modelMapper.map(user, UpdateUserDTO.class);
    }

    public User convertToUser(NewUserDTO newUserDTO) {
        return modelMapper.map(newUserDTO, User.class);
    }

}



