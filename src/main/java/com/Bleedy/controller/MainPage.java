package com.Bleedy.controller;

import com.Bleedy.Telegram.TelegramBot;
import com.Bleedy.repos.UserRepo;
import com.Bleedy.source.UserDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


@Controller
public class MainPage {

    @Autowired
    TelegramBot telegramBot;
    @Autowired
    UserRepo userRepo;


    @GetMapping("/greeting")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World")String name, Model model) {
        model.addAttribute("name",name);
        return "greeting";
    }


    @PostMapping("start")
    public String startTimer(@RequestParam(value="nickname", required=false, defaultValue="")String nickname,Model model) {
        if (!nickname.isEmpty()) {
            Iterable<UserDB> users = userRepo.findAll();
            for (UserDB user : users) {
                if (user.getUserName().equals(nickname)) {
                    int a = 0;
                    if(getThreadByName(nickname) == null){Thread.currentThread().setName(nickname);}
                    newMessage(user.getChatId(),
                            "Ok, lets start. We will tell you when you need to get rest ;)", telegramBot);
                    for (int i = 0; i < 3&& !Thread.currentThread().isInterrupted(); i++) {
                            timerForTask(1);
                        newMessage(user.getChatId(),
                                "Get rest", telegramBot);
                            timerForTask(1);
                        newMessage(user.getChatId(),
                                "Ok, lets start.", telegramBot);
                        a++;
                    }
                    if(a==3){
                        timerForTask(1);
                        newMessage(user.getChatId(),
                                "We finished that challenge!", telegramBot);
                        user.addInChallengeDone(LocalDateTime.now().toString());
                    }
                }
                Thread.currentThread().setName("Waiting");
            }
        }
        return "greeting";
    }

    @PostMapping("finish")
    public String stopTimer(@RequestParam(value="nicknameToStop", required=false, defaultValue="")String nickname) {
        if (!nickname.isEmpty()) {
        newMessage(274334414L,
                "Ok, lets finish", telegramBot);
        getThreadByName(nickname).interrupt();
        }
        return "greeting";
    }

    public void timerForTask(int timeInMinutes){
        try {
            TimeUnit.MINUTES.sleep(timeInMinutes);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt(); }
    }

    public void newMessage(Long chatId, String text, TelegramBot telegramBot){
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
        }return null;
    }
}
