package ru.tkachenko.cryptobot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tkachenko.cryptobot.model.Subscriber;
import ru.tkachenko.cryptobot.repository.SubscriberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;

    @Transactional
    public void save(Subscriber subscriber) {
        subscriberRepository.save(subscriber);
    }

    public boolean isSubscriberExists(Long telegramId) {
        return subscriberRepository.existsByTelegramId(telegramId);
    }

    public Subscriber getByTelegramId(Long telegramId) {
        return subscriberRepository.getSubscriberByTelegramId(telegramId);
    }

    @Transactional
    public void changeSubscriptionPrice(Long telegramId, Double price) {
        Subscriber subscriber = getByTelegramId(telegramId);

        if (subscriber == null) {
            return;
        }

        subscriber.setSubscriptionPrice(price);

        subscriberRepository.save(subscriber);
    }
}
