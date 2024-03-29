package com.example.attendease;

import java.util.UUID;

public class Msg {
    private String title;
    private String message;

    private String unique_id;

    private String event;

    public Msg(String title, String message, String event) {
        this.title = title;
        this.message = message;
        unique_id= UUID.randomUUID().toString();
        this.event=event;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getEvent() {
        return event;
    }
}
