package com.quiz.model;

/**
 * Thống kê số lần làm theo đề thi
 */
public class ExamAttemptStat {
    private int examId;
    private String examTitle;
    private int attempts;

    public ExamAttemptStat() {}

    public ExamAttemptStat(int examId, String examTitle, int attempts) {
        this.examId = examId;
        this.examTitle = examTitle;
        this.attempts = attempts;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }
}


