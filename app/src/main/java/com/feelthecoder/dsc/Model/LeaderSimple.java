package com.feelthecoder.dsc.Model;

public class LeaderSimple implements Comparable {
    String quizwon,skills,name,image;
    String dp;

    public LeaderSimple() {
    }


    public LeaderSimple(String dp, String quizwon, String skills, String name, String image) {
        this.dp = dp;
        this.quizwon = quizwon;
        this.skills = skills;
        this.name = name;
        this.image = image;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getQuizwon() {
        return quizwon;
    }

    public void setQuizwon(String quizwon) {
        this.quizwon = quizwon;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
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

    @Override
    public int compareTo(Object o) {
        int points= Integer.parseInt(getDp());
        return Integer.parseInt(this.dp)-points;
    }
}