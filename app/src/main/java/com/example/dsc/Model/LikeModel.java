package com.example.dsc.Model;

public class LikeModel {
    String Like,Image,Name;

    public LikeModel(String Like, String Image, String Name) {
        this.Like = Like;
        this.Image = Image;
        this.Name = Name;
    }

    public LikeModel() {
    }

    public String getLike() {
        return Like;
    }

    public void setLike(String like) {
        Like = like;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
