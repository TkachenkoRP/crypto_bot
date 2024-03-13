package ru.tkachenko.cryptobot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tkachenko.cryptobot.model.Subscriber;

import java.util.UUID;

public interface SubscriberRepository extends JpaRepository<Subscriber, UUID> {
    boolean existsByTelegramId(Long telegramId);
}
