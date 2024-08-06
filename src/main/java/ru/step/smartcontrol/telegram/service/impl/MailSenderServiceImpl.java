package ru.step.smartcontrol.telegram.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.step.smartcontrol.telegram.dto.MailParams;
import ru.step.smartcontrol.telegram.service.MailSenderService;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailSenderServiceImpl implements MailSenderService {

    private final JavaMailSender javaMailSender;

    @SuppressWarnings({"UnusedDeclaration"})
    @Value("${spring.mail.username}")
    private String emailFrom;

    @SuppressWarnings({"UnusedDeclaration"})
    @Value("${service.activation.uri}")
    private String activationServiceUri;

    @Override
    @Transactional
    public void send(MailParams mailParams) {

        var subject = "Активация учетной записи";
        var messageBody = getActivationMailBody(mailParams.getId());
        var emailTo = mailParams.getEmailTo();
        log.info(String.format("Sending email for mail=[%s]", emailTo));

        var mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailFrom);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(messageBody);

        javaMailSender.send(mailMessage);
    }

    private String getActivationMailBody(String id) {
        var msg = "Для завершения регистрации перейдите по ссылке:\n" + activationServiceUri;
        return msg.replace("{id}", id);
    }
}
