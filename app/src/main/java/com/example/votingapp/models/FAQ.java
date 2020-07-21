package com.example.votingapp.models;

public class FAQ {
    String question;
    String url;
    String answer;

    public FAQ(String question, String answer, String url) {
        this.question = question;
        this.url = url;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getUrl() {
        return url;
    }

    public String getAnswer() {
        return answer;
    }
}
