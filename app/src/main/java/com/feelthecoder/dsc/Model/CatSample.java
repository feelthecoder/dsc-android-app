package com.feelthecoder.dsc.Model;

import androidx.annotation.Keep;

@Keep
public class CatSample {
    String category,name,image;

    public CatSample() {
    }

    public CatSample(String category, String name, String image) {
        this.category = category;
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
