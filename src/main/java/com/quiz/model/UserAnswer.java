package com.quiz.model;

/**
 * Model class cho bảng UserAnswers
 */
public class UserAnswer {
    private int resultId;
    private int questionId;
    private String selectedOptions; // Ví dụ: 'A' hoặc 'BD'
    
    // Related objects
    private ExamResult examResult;
    private Question question;

    public UserAnswer() {}

    public UserAnswer(int resultId, int questionId, String selectedOptions) {
        this.resultId = resultId;
        this.questionId = questionId;
        this.selectedOptions = selectedOptions;
    }

    // Getters and Setters
    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(String selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    public ExamResult getExamResult() {
        return examResult;
    }

    public void setExamResult(ExamResult examResult) {
        this.examResult = examResult;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return String.format("Câu %d: %s", questionId, selectedOptions);
    }
}
