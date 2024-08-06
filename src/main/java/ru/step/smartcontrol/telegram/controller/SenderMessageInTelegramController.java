package ru.step.smartcontrol.telegram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.step.smartcontrol.telegram.dto.MessageBodyInTelegram;
import ru.step.smartcontrol.telegram.service.TelegramBot;

import javax.validation.Valid;

@RequestMapping("/send-message/telegram")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class SenderMessageInTelegramController {

    private final TelegramBot telegramBot;

    @PostMapping
    public ResponseEntity<ResponseEntity.BodyBuilder> sentActivatorMail(
            @RequestBody @Valid MessageBodyInTelegram messageBodyInTelegram) {
        telegramBot.send(messageBodyInTelegram);
        return ResponseEntity.ok().build();
    }
}
