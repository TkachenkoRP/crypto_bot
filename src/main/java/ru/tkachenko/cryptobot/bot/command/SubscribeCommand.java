package ru.tkachenko.cryptobot.bot.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tkachenko.cryptobot.service.CryptoCurrencyService;
import ru.tkachenko.cryptobot.service.SubscriberService;
import ru.tkachenko.cryptobot.utils.TextUtil;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscribeCommand implements IBotCommand {

    private final SubscriberService subscriberService;
    private final CryptoCurrencyService cryptoCurrencyService;

    @Override
    public String getCommandIdentifier() {
        return "subscribe";
    }

    @Override
    public String getDescription() {
        return "Подписывает пользователя на стоимость биткоина";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {

        String incorrectMessage = "Пожалуйста, введите корректное число после команды /subscribe";

        SendMessage bitcoinPrice = createBitcoinPriceMessage(message.getChatId());

        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());

        if (strings.length == 1) {
            try {
                double price = Double.parseDouble(strings[0]);
                subscriberService.changeSubscriptionPrice(message.getFrom().getId(), price);

                answer.setText("Новая подписка создана на стоимость " + TextUtil.toString(price) + " USD");
            } catch (NumberFormatException e) {
                answer.setText(incorrectMessage);
            }
        } else {
            answer.setText(incorrectMessage);
        }

        try {
            absSender.execute(bitcoinPrice);
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения в subscribe", e);
        }
    }

    private SendMessage createBitcoinPriceMessage(Long chatId) {
        SendMessage bitcoinPrice = new SendMessage();
        bitcoinPrice.setChatId(chatId);
        try {
            bitcoinPrice.setText(cryptoCurrencyService.getBitcoinPriceText());
        } catch (IOException e) {
            log.error("Ошибка получения цены биткоина", e);
            bitcoinPrice.setText("Не удалось получить цену биткоина");
        }
        return bitcoinPrice;
    }
}
