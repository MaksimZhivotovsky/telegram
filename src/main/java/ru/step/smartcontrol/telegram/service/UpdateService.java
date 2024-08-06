package ru.step.smartcontrol.telegram.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.step.smartcontrol.telegram.model.ServiceCommand;
import ru.step.smartcontrol.telegram.model.UserRegisterTelegram;
import ru.step.smartcontrol.telegram.repository.UserRegisterTelegramRepository;
import ru.step.smartcontrol.telegram.utils.MessageUtils;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;

import static ru.step.smartcontrol.telegram.model.ServiceCommand.CANCEL;
import static ru.step.smartcontrol.telegram.model.ServiceCommand.*;
import static ru.step.smartcontrol.telegram.model.UserState.BASIC_STATE;
import static ru.step.smartcontrol.telegram.model.UserState.WAIT_FOR_EMAIL_STATE;

@Slf4j
@Component
public class UpdateService {

    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;

    private final UserRegisterTelegramRepository userRegisterTelegramRepository;

    private final AppUserService appUserService;


    public UpdateService(
            MessageUtils messageUtils,
            UserRegisterTelegramRepository userRegisterTelegramRepository,
            AppUserService appUserService) {
        this.messageUtils = messageUtils;
        this.userRegisterTelegramRepository = userRegisterTelegramRepository;
        this.appUserService = appUserService;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Полученное обновление равно нулю");
            return;
        }

        if (update.getMessage() != null) {
            distributeMessagesByType(update);
        }
        else {
            log.error("Получен неподдерживаемый тип сообщения: {}", update);
        }
    }

    private void distributeMessagesByType(Update update) {
        log.info("UpdateController distributeMessagesByType update {}", update);
        var message = update.getMessage();
        if (message.getText() != null) {
            processTextMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Неподдерживаемый тип сообщения!");
        setView(sendMessage);
    }

    private void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void processTextMessage(Update update) {

        var appUser = findOrSaveUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        var serviceCommand = ServiceCommand.fromValue(text);
        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            output = appUserService.setEmail(appUser, text);
        } else {
            log.error("Unknown user state: {}", userState);
            output = "Неизвестная ошибка! Введите /help и попробуйте снова!";
        }

        var chatId = update.getMessage().getChatId();

        sendAnswer(output, chatId);
    }

    private UserRegisterTelegram findOrSaveUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        var optional =
                userRegisterTelegramRepository.findByTelegramChatId(telegramUser.getId());
        if (optional.isEmpty()) {
            UserRegisterTelegram transientUser = UserRegisterTelegram.builder()
                    .telegramChatId(telegramUser.getId())
                    .firstName(telegramUser.getFirstName())
                    .userName(telegramUser.getUserName())
                    .registeredAt(LocalDateTime.now())
                    .isActive(false)
                    .lastName(telegramUser.getLastName())
                    .state(BASIC_STATE)
                    .build();
            return userRegisterTelegramRepository.save(transientUser);
        }
        return optional.get();
    }

    private String processServiceCommand(UserRegisterTelegram appUser, String cmd) {
        var serviceCommand = ServiceCommand.fromValue(cmd);
        if (REGISTRATION.equals(serviceCommand)) {
            return appUserService.registerUser(appUser);
        } else if (HELP.equals(serviceCommand)) {
            return help();
        } else if (START.equals(serviceCommand)) {
            if (Boolean.FALSE.equals(appUser.getIsActive())) {
                return "Приветствую! Вам нужна регистрация /registration";
            }
            return "Приветствую! Чтобы посмотреть список доступных команд введите /help";
        } else {
            return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
    }

    private String help() {
        return "Список доступных команд:\n"
                + "/start - начало работы;\n"
                + "/cancel - отмените текущую команду;\n"
                + "/registration - регистрация пользователя.";
    }

    private String cancelProcess(UserRegisterTelegram appUser) {
        appUser.setState(BASIC_STATE);
        appUser.setEmail(null);
        appUser.setIsActive(false);
        userRegisterTelegramRepository.save(appUser);
        return "Команда отмены подписки на уведомления!";
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(output);
        telegramBot.sendAnswerMessage(sendMessage);

    }

}
