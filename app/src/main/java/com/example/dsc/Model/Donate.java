package com.example.dsc.Model;

import androidx.annotation.Keep;

@Keep
public class Donate {
    String approval,status,payment,tID,rID,date,mail_sent;

    public Donate() {
    }

    public Donate(String approval, String status, String payment, String tID, String rID, String date, String mail_sent) {
        this.approval = approval;
        this.status = status;
        this.payment = payment;
        this.tID = tID;
        this.rID = rID;
        this.date = date;
        this.mail_sent=mail_sent;
    }

    public void setMail_sent(String mail_sent) {
        this.mail_sent = mail_sent;
    }

    public String getMail_sent() {
        return mail_sent;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String gettID() {
        return tID;
    }

    public void settID(String tID) {
        this.tID = tID;
    }

    public String getrID() {
        return rID;
    }

    public void setrID(String rID) {
        this.rID = rID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}