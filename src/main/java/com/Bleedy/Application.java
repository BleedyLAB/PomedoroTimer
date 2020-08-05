package com.Bleedy;

import com.Bleedy.Telegram.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories
@ComponentScan
public class Application {
    public static void main(String[] args) {
        ApiContextInitializer.init();

        ConfigurableApplicationContext context = SpringApplication.run(Application.class);

        TelegramBotsApi botsApi  = new TelegramBotsApi();
        TelegramBot myTelegramBot = (TelegramBot) context.getBean("telegramBot");

        try {
            botsApi.registerBot(myTelegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
