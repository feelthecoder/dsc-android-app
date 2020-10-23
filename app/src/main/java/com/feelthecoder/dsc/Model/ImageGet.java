package com.feelthecoder.dsc.Model;

import androidx.annotation.Keep;

@Keep
public class ImageGet {
    String title,pic;

    public ImageGet(String title, String pic) {
        this.title = title;
        this.pic = pic;
    }

    public ImageGet() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
