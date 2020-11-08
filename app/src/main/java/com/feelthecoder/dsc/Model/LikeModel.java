package com.feelthecoder.dsc.Model;

import androidx.annotation.Keep;

@Keep
public class LikeModel {
    String like,image,name;

    public LikeModel(String like, String image, String name) {
        this.like = like;
        this.image = image;
        this.name = name;
    }

    public LikeModel() {
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
