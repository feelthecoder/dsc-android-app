package com.example.dsc.Model;

import androidx.annotation.Keep;

@Keep
public class ResUpload {
    String name,status,url;

    public ResUpload() {
    }

    public ResUpload(String name, String status, String url) {
        this.name = name;
        this.status = status;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
