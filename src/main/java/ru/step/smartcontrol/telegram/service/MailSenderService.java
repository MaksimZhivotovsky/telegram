package ru.step.smartcontrol.telegram.service;

import ru.step.smartcontrol.telegram.dto.MailParams;

public interface MailSenderService {

    void send(MailParams mailParams);

}
