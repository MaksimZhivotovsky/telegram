package ru.step.smartcontrol.telegram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.step.smartcontrol.telegram.service.UpdateService;

@RestController
@RequiredArgsConstructor
public class WebHookController {

    private final UpdateService updateService;

    @RequestMapping(value = "/callback/update", method = RequestMethod.POST)
    public ResponseEntity<?> onUpdateReceived(@RequestBody Update update) {
        updateService.processUpdate(update);
        return ResponseEntity.ok().build();
    }
}
