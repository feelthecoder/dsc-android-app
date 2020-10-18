package com.example.dsc.Model;

public class LeaderSimple implements Comparable {
    String quizwon,skills,name,image;
    String DP;

    public LeaderSimple() {
    }

    public LeaderSimple(String DP, String quizwon, String skills, String name, String image) {
        this.DP = DP;
        this.quizwon = quizwon;
        this.skills = skills;
        this.name = name;
        this.image = image;
    }

    public String getDP() {
        return DP;
    }

    public void setDP(String DP) {
        this.DP = DP;
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
        int points= Integer.parseInt(getDP());
        return Integer.parseInt(this.DP)-points;
    }
}