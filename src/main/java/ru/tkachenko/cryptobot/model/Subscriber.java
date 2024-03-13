package ru.tkachenko.cryptobot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "subscribers")
public class Subscriber {
    @Id
    private UUID id;
    @Column(name = "telegram_id")
    private Long telegramId;
    @Column(name = "subscription_price")
    private Double subscriptionPrice;
}
