package ru.tkachenko.cryptobot.bot.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tkachenko.cryptobot.model.Subscriber;
import ru.tkachenko.cryptobot.service.SubscriberService;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnsubscribeCommand implements IBotCommand {

    private final SubscriberService subscriberService;

    @Override
    public String getCommandIdentifier() {
        return "unsubscribe";
    }

    @Override
    public String getDescription() {
        return "Отменяет подписку пользователя";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());

        Subscriber subscriber = subscriberService.getByTelegramId(message.getFrom().getId());

        if (subscriber == null) {
            return;
        }

        answer.setText("Активная подписка отсутствует");

        if (subscriber.getSubscriptionPrice() != null) {
            answer.setText("Подписка отменена");
            subscriber.setSubscriptionPrice(null);
            subscriberService.save(subscriber);
        }

        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения в unsubscribe", e);
        }
    }
}
