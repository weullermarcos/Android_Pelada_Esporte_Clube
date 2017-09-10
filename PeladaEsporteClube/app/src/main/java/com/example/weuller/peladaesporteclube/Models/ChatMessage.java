package com.example.weuller.peladaesporteclube.Models;

/**
 * Created by ismael on 08/09/17.
 */

public class ChatMessage {

    private String date;
    private String message;
    private String user;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {

        return user + "\n" + message;
    }
}
