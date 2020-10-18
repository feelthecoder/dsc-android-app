package com.example.dsc.Model;

public class TechData {
    String tTitle,tDescription,urlImage;

    public TechData() {
    }

    public TechData(String tTitle, String tDescription, String urlImage) {
        this.tTitle = tTitle;
        this.tDescription = tDescription;
        this.urlImage = urlImage;
    }

    public String gettTitle() {
        return tTitle;
    }

    public void settTitle(String tTitle) {
        this.tTitle = tTitle;
    }

    public String gettDescription() {
        return tDescription;
    }

    public void settDescription(String tDescription) {
        this.tDescription = tDescription;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}
