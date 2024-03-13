package ru.tkachenko.cryptobot.bot.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tkachenko.cryptobot.model.Subscriber;
import ru.tkachenko.cryptobot.service.SubscriberService;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class StartCommand implements IBotCommand {

    private final SubscriberService subscriberService;

    @Override
    public String getCommandIdentifier() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Запускает бота";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {

        addSubscriber(message.getFrom());

        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());

        answer.setText("""
                Привет! Данный бот помогает отслеживать стоимость биткоина.
                Поддерживаемые команды:
                 /subscribe [число] - подписаться на стоимость биткоина в USD
                 /get_price - получить стоимость биткоина
                 /get_subscription - получить текущую подписку
                """);
        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Error occurred in /start command", e);
        }
    }

    private void addSubscriber(User user) {
        Subscriber subscriber = new Subscriber();
        subscriber.setId(UUID.randomUUID());
        subscriber.setTelegramId(user.getId());

        if (subscriberService.isSubscriberExists(subscriber.getTelegramId())) {
            return;
        }

        subscriberService.save(subscriber);
        log.info("New subscriber added - " + subscriber.getTelegramId());
    }
}
