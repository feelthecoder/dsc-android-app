package com.example.dsc.Model;

import androidx.annotation.Keep;

@Keep
public class PDFModel {
    String name,uri,thumb;

    public PDFModel() {
    }

    public PDFModel(String name, String uri, String thumb) {
        this.name = name;
        this.uri = uri;
        this.thumb = thumb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
