package ru.step.smartcontrol.telegram.repository;

import ru.step.smartcontrol.telegram.dto.UserSC;

public interface UserSCRepository {

    @SuppressWarnings("UnusedReturnValue")
    UserSC findByEmail(String email);

}
