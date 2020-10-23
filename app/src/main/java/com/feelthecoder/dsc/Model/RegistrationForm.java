package com.feelthecoder.dsc.Model;

import androidx.annotation.Keep;

@Keep
public class RegistrationForm {
    String name_,email_,mobile_,course_,year_,branch_,college_,date_,category,tName,code_;

    public RegistrationForm() {
    }

    public RegistrationForm(String name_, String email_, String mobile_, String course_, String year_, String branch_, String college_, String date_,String category, String tName,String code_) {
        this.name_ = name_;
        this.email_ = email_;
        this.mobile_ = mobile_;
        this.course_ = course_;
        this.year_ = year_;
        this.branch_ = branch_;
        this.college_ = college_;
        this.date_ = date_;
        this.category=category;
        this.tName=tName;
        this.code_=code_;
    }

    public String getCode_() {
        return code_;
    }

    public void setCode_(String code_) {
        this.code_ = code_;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String gettName() {
        return tName;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    public String getName_() {
        return name_;
    }

    public void setName_(String name_) {
        this.name_ = name_;
    }

    public String getEmail_() {
        return email_;
    }

    public void setEmail_(String email_) {
        this.email_ = email_;
    }

    public String getMobile_() {
        return mobile_;
    }

    public void setMobile_(String mobile_) {
        this.mobile_ = mobile_;
    }

    public String getCourse_() {
        return course_;
    }

    public void setCourse_(String course_) {
        this.course_ = course_;
    }

    public String getYear_() {
        return year_;
    }

    public void setYear_(String year_) {
        this.year_ = year_;
    }

    public String getBranch_() {
        return branch_;
    }

    public void setBranch_(String branch_) {
        this.branch_ = branch_;
    }

    public String getCollege_() {
        return college_;
    }

    public void setCollege_(String college_) {
        this.college_ = college_;
    }

    public String getDate_() {
        return date_;
    }

    public void setDate_(String date_) {
        this.date_ = date_;
    }
}
