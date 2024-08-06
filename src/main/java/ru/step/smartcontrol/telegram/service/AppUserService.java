package ru.step.smartcontrol.telegram.service;

import ru.step.smartcontrol.telegram.model.UserRegisterTelegram;

public interface AppUserService {

    String registerUser(UserRegisterTelegram userRegisterTelegram);
    String setEmail(UserRegisterTelegram userRegisterTelegram, String email);

}
