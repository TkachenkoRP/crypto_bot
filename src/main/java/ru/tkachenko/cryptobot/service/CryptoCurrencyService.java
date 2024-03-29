package ru.tkachenko.cryptobot.service;

import org.springframework.stereotype.Service;
import ru.tkachenko.cryptobot.client.BinanceClient;
import ru.tkachenko.cryptobot.utils.TextUtil;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CryptoCurrencyService {
    private final AtomicReference<Double> price = new AtomicReference<>();
    private final BinanceClient client;

    public CryptoCurrencyService(BinanceClient client) {
        this.client = client;
    }

    public double getBitcoinPrice() throws IOException {
        price.set(client.getBitcoinPrice());
        return price.get();
    }

    public String getBitcoinPriceText() throws IOException {
        return "Текущая цена биткоина " + TextUtil.toString(getBitcoinPrice()) + " USD";
    }
}
