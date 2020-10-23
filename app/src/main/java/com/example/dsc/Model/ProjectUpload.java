package com.example.dsc.Model;

import androidx.annotation.Keep;

@Keep
public class ProjectUpload {

        public String name;
        public String url;
        public String status;

        public ProjectUpload() {
        }

        public ProjectUpload(String name, String url,String status) {
            this.name = name;
            this.url = url;
            this.status=status;
        }

        public String getName() {
            return name;
        }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
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
    }

