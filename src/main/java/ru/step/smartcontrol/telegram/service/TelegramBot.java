package ru.step.smartcontrol.telegram.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.step.smartcontrol.telegram.config.BotConfig;
import ru.step.smartcontrol.telegram.dto.MessageBodyInTelegram;
import ru.step.smartcontrol.telegram.exception.UserException;
import ru.step.smartcontrol.telegram.repository.UserRegisterTelegramRepository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component

public class TelegramBot extends TelegramWebhookBot {

    private final BotConfig botConfig;
    private final UpdateService updateService;
    private final UserRegisterTelegramRepository userRegisterTelegramRepository;

    public TelegramBot(
            BotConfig botConfig,
            UpdateService updateService,
            UserRegisterTelegramRepository userRegisterTelegramRepository) {
        this.botConfig = botConfig;
        this.updateService = updateService;
        this.userRegisterTelegramRepository = userRegisterTelegramRepository;

        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Начало работы"));
        listOfCommands.add(new BotCommand("/registration", "Регистрация"));
        listOfCommands.add(new BotCommand("/help", "Помощь"));
        listOfCommands.add(new BotCommand("/cancel", "Команда отмены подписки на уведомления!"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: {}", e.getMessage());
        }
    }

    @PostConstruct
    public void init() {
        updateService.registerBot(this);
        try {
            var setWebhook = SetWebhook.builder()
                    .url(botConfig.getBotUri())
                    .build();
            this.setWebhook(setWebhook);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }


    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    public void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void send(MessageBodyInTelegram messageBodyInTelegram) {
        log.info("TelegramBot send sendMessageTelegram {}", messageBodyInTelegram);
        Long chatId = userRegisterTelegramRepository.findChatIdByUserId(messageBodyInTelegram.getAddresseeUserId());

        String message = "Отправитель : " + messageBodyInTelegram.getSenderServiceCode() + " \n" +
                "Тема сообщения : " + messageBodyInTelegram.getTheme() + " \n" +
                "Сообщение : " + messageBodyInTelegram.getMessage();

        if (chatId != null) {
            sendMessage(chatId, message);
        } else {
            throw new UserException("Пользователя нет с таким email");
        }
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

    @Override
    public String getBotPath() {
        return "/update";
    }
}
