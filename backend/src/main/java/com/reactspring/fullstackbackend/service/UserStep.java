package com.reactspring.fullstackbackend.service;


public class UserStep {
    private String Step;
    private String chatId;
    private String lang;

    public UserStep() {
    }


    public UserStep(String step, String chatId, String lang) {
        Step = step;
        this.chatId = chatId;
        this.lang = lang;
    }

    public String getStep() {
        return Step;
    }

    public void setStep(String step) {
        Step = step;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
