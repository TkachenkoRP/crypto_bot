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
import ru.tkachenko.cryptobot.utils.TextUtil;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetSubscriptionCommand implements IBotCommand {

    private final SubscriberService subscriberService;

    @Override
    public String getCommandIdentifier() {
        return "get_subscription";
    }

    @Override
    public String getDescription() {
        return "Возвращает текущую подписку";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());

        Subscriber subscriber = subscriberService.getByTelegramId(message.getFrom().getId());

        if (subscriber == null) {
            return;
        }

        answer.setText("Активные подписки отсутствуют");

        if (subscriber.getSubscriptionPrice() != null) {
            answer.setText("Вы подписаны на стоимость биткоина " + TextUtil.toString(subscriber.getSubscriptionPrice()) + " USD");
        }

        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки информации о подписки для " + message.getFrom().getId());
        }
    }
}
