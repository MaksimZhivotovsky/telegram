package ru.step.smartcontrol.telegram.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MailParams {

    private String id;
    private String emailTo;
}
