package com.example.do_not_play_with_me;

import com.google.firebase.firestore.DocumentId;

public class QuestionsModel {

    @DocumentId
    private String questionID;
    private String question, option_a, option_b, option_c, answer;
    private long timer;

    //empty constructor for Firebase
    public QuestionsModel(){


    }

    public QuestionsModel(String questionID, String question, String option_a, String option_b, String option_c, String answer, long timer) {
        this.questionID = questionID;
        this.question = question;
        this.option_a = option_a;
        this.option_b = option_b;
        this.option_c = option_c;
        this.answer = answer;
        this.timer = timer;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption_a() {
        return option_a;
    }

    public void setOption_a(String option_a) {
        this.option_a = option_a;
    }

    public String getOption_b() {
        return option_b;
    }

    public void setOption_b(String option_b) {
        this.option_b = option_b;
    }

    public String getOption_c() {
        return option_c;
    }

    public void setOption_c(String option_c) {
        this.option_c = option_c;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public long getTimer() {
        return timer;
    }

    public void setTimer(long timer) {
        this.timer = timer;
    }
}
