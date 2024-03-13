package ru.tkachenko.cryptobot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tkachenko.cryptobot.model.Subscriber;
import ru.tkachenko.cryptobot.repository.SubscriberRepository;

@Service
@RequiredArgsConstructor
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;

    public void save(Subscriber subscriber) {
        subscriberRepository.save(subscriber);
    }

    public boolean isSubscriberExists(Long telegramId) {
        return subscriberRepository.existsByTelegramId(telegramId);
    }
}
