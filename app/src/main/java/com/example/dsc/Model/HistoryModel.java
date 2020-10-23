package com.example.dsc.Model;

import androidx.annotation.Keep;

@Keep
public class HistoryModel {
    String correct,date,quiz,victory;

    public HistoryModel() {
    }

    public HistoryModel(String correct, String date, String quiz, String victory) {
        this.correct = correct;
        this.date = date;
        this.quiz = quiz;
        this.victory = victory;
    }




    public String getCorrect() {
        return correct;
    }

    public String getQuiz() {
        return quiz;
    }

    public void setQuiz(String quiz) {
        this.quiz = quiz;
    }

    public String getVictory() {
        return victory;
    }

    public void setVictory(String victory) {
        this.victory = victory;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
