package com.example.dsc.Model;

public class TechSpandan {
    String title;
    String pic;
    String description;
    String shortdes;
    String date;

    public TechSpandan(String title, String pic, String description, String shortdes, String date) {
        this.title = title;
        this.pic = pic;
        this.description = description;
        this.shortdes = shortdes;
        this.date = date;
    }

    public TechSpandan() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortdes() {
        return shortdes;
    }

    public void setShortdes(String shortdes) {
        this.shortdes = shortdes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
