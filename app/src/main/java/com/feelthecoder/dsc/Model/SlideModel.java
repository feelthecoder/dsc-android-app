package com.feelthecoder.dsc.Model;

import androidx.annotation.Keep;

@Keep
public class SlideModel {
    private String imageUrl;

    public SlideModel() {

    }

    public SlideModel(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {

        return imageUrl;
    }


}