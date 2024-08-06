package ru.step.smartcontrol.telegram.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MessageBodyInTelegram {

    @NotNull
    private Long addresseeUserId;
    private String senderServiceCode;
    private Long clientId;
    private String theme;
    private String message;

}
