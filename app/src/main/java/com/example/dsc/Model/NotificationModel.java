package com.example.dsc.Model;

public class NotificationModel {
    String title,message,activity;

    public NotificationModel() {
    }

    public NotificationModel(String title, String message, String activity) {
        this.title = title;
        this.message = message;
        this.activity = activity;
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

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
}
