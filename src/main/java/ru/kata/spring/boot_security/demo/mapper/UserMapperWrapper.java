package ru.kata.spring.boot_security.demo.mapper;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.model.User;


/**
 * Класс-обертка для маппинга сущностей User в DTO и наоборот.
 * <p>
 * Этот класс используется для преобразования объектов User в объекты UserDTO.
 * Логирует процесс преобразования, если уровень логирования установлен на DEBUG.
 */
@Component
@RequiredArgsConstructor
public class UserMapperWrapper {
    private static final Logger logger = LoggerFactory.getLogger(UserMapperWrapper.class);
    private final UserMapper userMapper;


    /**
     * Преобразует объект User в объект UserDTO.
     * <p>
     * Метод логирует входной объект User и результат преобразования в UserDTO,
     * если уровень логирования установлен на DEBUG.
     *
     * @param user объект User для преобразования
     * @return объект UserDTO, полученный в результате преобразования
     */
    public UserDTO convertToUserDTO(User user) {
        if (logger.isDebugEnabled()) {
            logger.debug("Преобразование User в UserDTO: {}", user);
        }
        UserDTO userDTO = userMapper.convertToUserDTO(user);
        if (logger.isDebugEnabled()) {
            logger.debug("Преобразованный UserDTO: {}", userDTO);
        }
        return userDTO;
    }


}
