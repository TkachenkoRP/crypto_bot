package ru.tkachenko.cryptobot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tkachenko.cryptobot.bot.CryptoBot;
import ru.tkachenko.cryptobot.model.Subscriber;
import ru.tkachenko.cryptobot.utils.TextUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CryptoAlertService {
    private final CryptoCurrencyService cryptoCurrencyService;
    private final SubscriberService subscriberService;
    private final CryptoBot cryptoBot;

    @Value("${telegram.bot.notify.delay.value}")
    private int notifyDelayValue;

    @Value("${telegram.bot.notify.delay.unit}")
    private String notifyDelayUnit;

    private Map<UUID, LocalDateTime> lastAlertTimeMap = new HashMap<>();

    @Scheduled(cron = "${telegram.bot.notify.check.cron}")
    public void checkBitcoinPrice() {
        Double currentBitcoinPrice;
        try {
            currentBitcoinPrice = cryptoCurrencyService.getBitcoinPrice();
        } catch (IOException e) {
            log.error("Ошибка получения цены биткоина", e);
            return;
        }

        List<Subscriber> subscribers = subscriberService.getSubscriberPriceGreaterThan(currentBitcoinPrice);

        for (Subscriber subscriber : subscribers) {
            if (lastAlertTimeMap.containsKey(subscriber.getId())) {
                LocalDateTime lastAlertTime = lastAlertTimeMap.get(subscriber.getId());
                if (ChronoUnit.valueOf(notifyDelayUnit).between(lastAlertTime, LocalDateTime.now()) >= notifyDelayValue) {
                    sendBitcoinAlert(subscriber, currentBitcoinPrice);
                    lastAlertTimeMap.put(subscriber.getId(), LocalDateTime.now());
                }
            } else {
                sendBitcoinAlert(subscriber, currentBitcoinPrice);
                lastAlertTimeMap.put(subscriber.getId(), LocalDateTime.now());
            }
        }
    }

    private void sendBitcoinAlert(Subscriber subscriber, Double currentBitcoinPrice) {
        SendMessage message = new SendMessage();
        message.setChatId(subscriber.getTelegramId());
        message.setText("Пора покупать, стоимость биткоина " + TextUtil.toString(currentBitcoinPrice) + " USD");

        try {
            cryptoBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения пользователю - " + subscriber.getTelegramId(), e);
        }
    }
}
