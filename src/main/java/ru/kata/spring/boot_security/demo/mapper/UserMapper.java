package ru.kata.spring.boot_security.demo.mapper;

import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.model.User;
import org.mapstruct.Mapper;

/**
 * Интерфейс для маппинга между сущностями User и UserDTO.
 * <p>
 * Этот интерфейс использует библиотеку MapStruct для автоматической генерации
 * реализации методов маппинга.
 */
@Mapper(componentModel = "spring") //  указывает, что сгенерированный маппер будет зарегистрирован как Spring Bean.
public interface UserMapper {

    /**
     * Преобразует объект User в объект UserDTO.
     *
     * @param user объект User, который нужно преобразовать
     * @return объект UserDTO, полученный в результате преобразования
     */
    UserDTO convertToUserDTO(User user);

}



