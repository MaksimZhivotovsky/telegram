package ru.step.smartcontrol.telegram.mapper;

import ru.step.smartcontrol.telegram.dto.UserRegisterTelegramDto;
import ru.step.smartcontrol.telegram.model.UserRegisterTelegram;

import java.time.LocalDateTime;

public class UserRegisterTelegramMapper {

    private UserRegisterTelegramMapper() {}

    public static UserRegisterTelegram mapToUserRegisterTelegram (UserRegisterTelegramDto userDto) {
        UserRegisterTelegram user = new UserRegisterTelegram();

        user.setFirstName(userDto.getFirstName());
        user.setRegisteredAt(LocalDateTime.now());
        user.setEmail(userDto.getEmail());
        user.setUserName(userDto.getUserName());
        user.setIsActive(userDto.getIsActive());

        return user;
    }

}
