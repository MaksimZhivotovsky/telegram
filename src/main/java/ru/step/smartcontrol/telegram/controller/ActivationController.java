package ru.step.smartcontrol.telegram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.step.smartcontrol.telegram.service.UserActivationService;

@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class ActivationController {
    private final UserActivationService userActivationService;

    @GetMapping(value = "/activation")
    public ResponseEntity<String> activation(@RequestParam("id") String id) {
        var res = userActivationService.activation(id);
        if (res) {
            return ResponseEntity.ok().body("Регистрация успешно завершена!");
        }
        return ResponseEntity.internalServerError().build();
    }
}
