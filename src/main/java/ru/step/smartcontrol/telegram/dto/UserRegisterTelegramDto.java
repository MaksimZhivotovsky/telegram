package ru.step.smartcontrol.telegram.dto;

import lombok.*;
import ru.step.smartcontrol.telegram.model.UserState;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserRegisterTelegramDto {

    private Long telegramChatId;
    private String firstName;
    private String lastName;
    private String userName;
    private LocalDateTime registeredAt;
    private Boolean isActive;
    private String email;
    @Enumerated(EnumType.STRING)
    private UserState state;
}
