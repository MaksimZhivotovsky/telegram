package ru.step.smartcontrol.telegram.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.step.smartcontrol.telegram.dto.MailParams;
import ru.step.smartcontrol.telegram.exception.UserException;
import ru.step.smartcontrol.telegram.model.UserRegisterTelegram;
import ru.step.smartcontrol.telegram.model.UserState;
import ru.step.smartcontrol.telegram.repository.UserRegisterTelegramRepository;
import ru.step.smartcontrol.telegram.repository.UserSCRepository;
import ru.step.smartcontrol.telegram.service.AppUserService;
import ru.step.smartcontrol.telegram.utils.CryptoTool;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final UserRegisterTelegramRepository userRegisterTelegramRepository;
    private final UserSCRepository userSCRepository;

    private final CryptoTool cryptoTool;

    @SuppressWarnings({"UnusedDeclaration"})
    @Value("${service.mail.uri}")
    private String mailServiceUri;

    @Override
    @Transactional
    public String registerUser(UserRegisterTelegram userRegisterTelegram) {

        log.info("AppUserServiceImpl registerUser userRegisterTelegram {}", userRegisterTelegram);

        if (Boolean.TRUE.equals(userRegisterTelegram.getIsActive())) {
            return "Вы уже зарегистрированы!";
        } else if (userRegisterTelegram.getEmail() != null && !userRegisterTelegram.getEmail().isEmpty()) {
            return "Вам на почту уже было отправлено письмо. "
                    + "Перейдите по ссылке в письме для подтверждения регистрации.";
        }
        userRegisterTelegram.setState(UserState.WAIT_FOR_EMAIL_STATE);
        userRegisterTelegramRepository.save(userRegisterTelegram);
        return "Введите, пожалуйста, ваш email:";
    }

    @Override
    @Transactional
    public String setEmail(UserRegisterTelegram userRegisterTelegram, String email) {

        log.info("AppUserServiceImpl setEmail userRegisterTelegram {} email {}", userRegisterTelegram, email);

        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException e) {
            return "Введите, пожалуйста, корректный email.";
        }

        try {
            userSCRepository.findByEmail(email);
        } catch (UserException e) {
            return "Нет прав у данного пользователя. Введите, пожалуйста другой Email";
        }
        var optional = userRegisterTelegramRepository.findByEmail(email);
        if (optional.isEmpty()) {
            userRegisterTelegram.setEmail(email);
            userRegisterTelegram.setState(UserState.BASIC_STATE);
            userRegisterTelegram = userRegisterTelegramRepository.save(userRegisterTelegram);

            var cryptoUserId = cryptoTool.hashOf(userRegisterTelegram.getId());
            var response = sendRequestToMailService(cryptoUserId, email);
            if (response.getStatusCode() != HttpStatus.OK) {
                var msg = String.format("Отправка эл. письма на почту %s не удалась.", email);
                log.error(msg);
                userRegisterTelegram.setEmail(null);
                userRegisterTelegramRepository.save(userRegisterTelegram);
                return msg;
            }
            return "Вам на почту было отправлено письмо."
                    + "Перейдите по ссылке в письме для подтверждения регистрации.";
        } else {
            return "Этот email уже используется. Введите корректный email."
                    + " Для отмены команды введите /cancel";
        }
    }

    private ResponseEntity<String> sendRequestToMailService(String cryptoUserId, String email) {
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        var request = new HttpEntity<>(mailParams, headers);
        return restTemplate.exchange(mailServiceUri,
                HttpMethod.POST,
                request,
                String.class);
    }
}