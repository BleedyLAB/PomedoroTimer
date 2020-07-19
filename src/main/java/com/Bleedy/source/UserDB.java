package com.Bleedy.source;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="TomatoUser")
public class UserDB {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Long chatId;
    private String userName;
    private Long userID;
    @ElementCollection
    private List<String> challengeDone = new ArrayList<String>();
    public UserDB(){}
    public UserDB(Long chatId,String userName,Long userID){
        this.chatId = chatId;
        this.userName = userName;
        this.userID = userID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void addInChallengeDone(String str){
        challengeDone.add(str);
    }

    public List<String> getChallengeDone() {
        return challengeDone;
    }

    public void setChallengeDone(List<String> challengeDone) {
        this.challengeDone = challengeDone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
