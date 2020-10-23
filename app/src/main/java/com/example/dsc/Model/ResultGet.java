package com.example.dsc.Model;

import androidx.annotation.Keep;

@Keep
public class ResultGet {
    String name,link;

    public ResultGet(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public ResultGet() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
