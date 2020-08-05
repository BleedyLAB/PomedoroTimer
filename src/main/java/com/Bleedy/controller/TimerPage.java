package com.Bleedy.controller;

import com.Bleedy.Telegram.TelegramBot;
import com.Bleedy.repos.UserRepo;
import com.Bleedy.source.UserDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;


@Controller
public class TimerPage {

    @Autowired
    TelegramBot telegramBot;
    @Autowired
    UserRepo userRepo;

    @GetMapping("/timer")
    public String timer(@RequestParam(value = "nickname") String nickname, Model model) {
        if (nickname.isEmpty() || userRepo.findByUserName(nickname).isEmpty()) return "greeting";
        Iterable<UserDB> users = userRepo.findAll();
        for (UserDB user : users) {
            model.addAttribute("filter", user.getChallengeDone());
        }
        return "timer";
    }

    @PostMapping("/start")
    public String startTimer(@RequestParam(value = "nickname", required = true) String nickname, Model model) {
        if (!nickname.isEmpty()) {
            Iterable<UserDB> users = userRepo.findAll();
            for (UserDB user : users) {
                if (user.getUserName().equals(nickname)) {
                    int a = 0;
                    if (getThreadByName(nickname) == null) {
                        Thread.currentThread().setName(nickname);
                    }
                    newMessage(user.getChatId(),
                            "Ok, lets start. We will tell you when you need to get rest ;)", telegramBot);
                    for (int i = 0; i < 3 && !Thread.currentThread().isInterrupted(); i++) {
                        timerForTask(25);
                        if (!Thread.currentThread().isInterrupted()) {
                            newMessage(user.getChatId(),
                                    "Get rest", telegramBot);
                        }
                        timerForTask(5);
                        if (!Thread.currentThread().isInterrupted()) {
                            newMessage(user.getChatId(),
                                    "Ok, lets start.", telegramBot);
                        }
                        a++;
                    }
                    if (a == 3) {
                        timerForTask(1);
                        newMessage(user.getChatId(),
                                "We finished that challenge!", telegramBot);
                        user.addInChallengeDone(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        userRepo.save(user);
                    }
                }
                model.addAttribute("filter", user.getChallengeDone());
                Thread.currentThread().setName("Waiting");
            }
        }

        return "timer";
    }

    @PostMapping("finish")
    public String stopTimer(@RequestParam(value = "nickname") String nickname, Model model) {
        if (!nickname.isEmpty()) {
            Iterable<UserDB> users = userRepo.findAll();
            for (UserDB user : users) {
                if (user.getUserName().equals(nickname) && getThreadByName(nickname) != null) {
                    newMessage(user.getChatId(),
                            "Ok, lets finish", telegramBot);
                    try {
                        getThreadByName(nickname).interrupt();
                    } catch (Exception ignored) {
                    }
                }
                model.addAttribute("filter", user.getChallengeDone());
            }
        }
        return "timer";
    }

    @PostMapping("customTimer")
    public String customTimer(@RequestParam(value = "timeForTimer", required = false, defaultValue = "-1") int time,
                              @RequestParam(value = "nickname", required = true) String nickname,
                              Model model) {
        if (!nickname.isEmpty() && time < 60) {
            Iterable<UserDB> users = userRepo.findAll();
            for (UserDB user : users) {
                newMessage(user.getChatId(),
                        "We start " + time + " minute timer", telegramBot);
                timerForTask(time);
                newMessage(user.getChatId(),
                        "Ok, lets finish.", telegramBot);
                model.addAttribute("filter", user.getChallengeDone());
            }
        }
        return "timer";
    }


    public void timerForTask(int timeInMinutes) {
        try {
            TimeUnit.MINUTES.sleep(timeInMinutes);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    public void newMessage(Long chatId, String text, TelegramBot telegramBot) {
        SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId(chatId)
                .setText(text);
        try {
            telegramBot.execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public Thread getThreadByName(String threadName) {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals(threadName)) return t;
        }
        return null;
    }
}
