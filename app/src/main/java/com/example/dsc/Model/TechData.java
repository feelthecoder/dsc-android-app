package com.example.dsc.Model;

import androidx.annotation.Keep;

@Keep
public class TechData {
    public String tCaption, tDescription, tImg,tLike, tShare, tComment;

    public TechData() {
    }

    public TechData(String tCaption, String tDescription, String tImg, String tLike, String tShare, String tComment) {
        this.tCaption = tCaption;
        this.tDescription = tDescription;
        this.tImg = tImg;
        this.tLike = tLike;
        this.tShare = tShare;
        this.tComment = tComment;
    }

    public String gettCaption() {
        return tCaption;
    }

    public void settCaption(String tCaption) {
        this.tCaption = tCaption;
    }

    public String gettDescription() {
        return tDescription;
    }

    public void settDescription(String tDescription) {
        this.tDescription = tDescription;
    }

    public String gettImg() {
        return tImg;
    }

    public void settImg(String tImg) {
        this.tImg = tImg;
    }

    public String gettLike() {
        return tLike;
    }

    public void settLike(String tLike) {
        this.tLike = tLike;
    }

    public String gettShare() {
        return tShare;
    }

    public void settShare(String tShare) {
        this.tShare = tShare;
    }

    public String gettComment() {
        return tComment;
    }

    public void settComment(String tComment) {
        this.tComment = tComment;
    }
}