package ru.step.smartcontrol.telegram.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.step.smartcontrol.telegram.repository.UserRegisterTelegramRepository;
import ru.step.smartcontrol.telegram.service.UserActivationService;
import ru.step.smartcontrol.telegram.utils.CryptoTool;

@Service
@RequiredArgsConstructor
public class UserActivationServiceImpl implements UserActivationService {
    private final UserRegisterTelegramRepository userRegisterTelegramRepository;
    private final CryptoTool cryptoTool;


    @Override
    @Transactional
    public boolean activation(String cryptoUserId) {
        var userId = cryptoTool.idOf(cryptoUserId);
        var optional = userRegisterTelegramRepository.findById(userId);
        if (optional.isPresent()) {
            var user = optional.get();
            user.setIsActive(true);
            userRegisterTelegramRepository.save(user);
            return true;
        }
        return false;
    }
}
