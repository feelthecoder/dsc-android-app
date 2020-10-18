package com.example.dsc.Model;

public class CommentModel {
    String  comment;
    String name ;
    String pic;
    String id;


    public CommentModel(String comment, String name, String pic, String id) {
        this.comment = comment;
        this.name = name;
        this.pic = pic;
        this.id = id;
    }

    public CommentModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}