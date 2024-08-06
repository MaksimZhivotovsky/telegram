package ru.step.smartcontrol.telegram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.step.smartcontrol.telegram.dto.MailParams;
import ru.step.smartcontrol.telegram.service.MailSenderService;

@RestController
@RequestMapping(value = "/mail")
@RequiredArgsConstructor
@CrossOrigin
public class MailController {

    private final MailSenderService mailSenderService;

    @PostMapping(value = "/send")
    public ResponseEntity<ResponseEntity.BodyBuilder> sentActivatorMail(@RequestBody MailParams mailParams) {
        mailSenderService.send(mailParams);
        return ResponseEntity.ok().build();
    }
}
