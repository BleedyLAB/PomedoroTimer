package com.Bleedy.Telegram;

import com.Bleedy.repos.UserRepo;
import com.Bleedy.source.UserDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
public class TelegramBot extends TelegramLongPollingBot {
    boolean countForRegister = false;
    @Autowired
    private UserRepo userRepo;


    public void onUpdateReceived(Update update) {
        // register new user
        if (update.getMessage().getText().contentEquals("/register")) {
            Iterable<UserDB> users = userRepo.findAll();
            for (UserDB user : users) {
                if (user.getUserID().equals(update.getMessage().getChat().getId())) {
                    countForRegister = true;
                }
            }
            if (countForRegister) {
                SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                        .setChatId(update.getMessage().getChatId())
                        .setText("You are registered");
                try {
                    execute(message); // Call method to send the message
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                userRepo.save(new UserDB(update.getMessage().getChatId(), update.getMessage().getChat().getUserName(), update.getMessage().getChat().getId()));
                SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                        .setChatId(update.getMessage().getChatId())
                        .setText("Done :)" + "Now you can get push by timer in your chat!");

                countForRegister = false;
                try {
                    execute(message); // Call method to send the message
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }


        // sent user "help" message
        if (update.hasMessage() && update.getMessage().isCommand() && update.getMessage().getText().contentEquals("/help")) {
            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId())
                    .setText("/addtag - add tag in blacklist (one at a time)\n" +
                            "/addchannel - add channel in blacklist (one at a time)");
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }

    }


    @Override
    public String getBotUsername() {
        return "First23_Bot";
    }

    @Override
    public String getBotToken() {
        return "1187356229:AAEW5BtrdvO8IqtG0jY_oD5sq3KdjZQ4kqU";
    }
}
