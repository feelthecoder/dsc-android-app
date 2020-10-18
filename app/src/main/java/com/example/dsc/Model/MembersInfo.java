package com.example.dsc.Model;

public class MembersInfo {
    String name,post,skills,specia,git,fb,linkdin,hackerrank,insta,bio,dp,projects;

    public MembersInfo(String name, String post, String skills, String specia, String git, String fb, String linkdin, String hackerrank, String insta, String bio, String dp, String projects) {
        this.name = name;
        this.post = post;
        this.skills = skills;
        this.specia = specia;
        this.git = git;
        this.fb = fb;
        this.linkdin = linkdin;
        this.hackerrank = hackerrank;
        this.insta = insta;
        this.bio = bio;
        this.dp = dp;
        this.projects = projects;
    }

    public MembersInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getSpecia() {
        return specia;
    }

    public void setSpecia(String specia) {
        this.specia = specia;
    }

    public String getGit() {
        return git;
    }

    public void setGit(String git) {
        this.git = git;
    }

    public String getFb() {
        return fb;
    }

    public void setFb(String fb) {
        this.fb = fb;
    }

    public String getLinkdin() {
        return linkdin;
    }

    public void setLinkdin(String linkdin) {
        this.linkdin = linkdin;
    }

    public String getHackerrank() {
        return hackerrank;
    }

    public void setHackerrank(String hackerrank) {
        this.hackerrank = hackerrank;
    }

    public String getInsta() {
        return insta;
    }

    public void setInsta(String insta) {
        this.insta = insta;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getProjects() {
        return projects;
    }

    public void setProjects(String projects) {
        this.projects = projects;
    }
}
