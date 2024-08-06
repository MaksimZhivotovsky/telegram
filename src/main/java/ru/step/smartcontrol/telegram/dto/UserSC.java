package ru.step.smartcontrol.telegram.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(of = {"firstName", "lastName", "middleName", "email"})
@EqualsAndHashCode(of = {"id"})
public class UserSC implements Serializable {

    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;

}
