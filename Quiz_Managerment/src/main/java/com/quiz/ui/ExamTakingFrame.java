package com.quiz.ui;

import com.quiz.dao.ExamResultDAO;
import com.quiz.dao.QuestionDAO;
import com.quiz.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.KeyStroke;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Giao diện thi trắc nghiệm
 */
public class ExamTakingFrame extends JFrame {
    private Exam exam;
    private List<Question> questions;
    private List<UserAnswer> userAnswers;
    private int currentQuestionIndex = 0;
    private LocalDateTime startTime;
    private Timer timer;
    private int timeRemaining; // in seconds
    private QuestionDAO questionDAO;
    private User currentUser;
    
    // UI Components
    private JLabel questionNumberLabel, timeLabel, questionContentLabel;
    private JRadioButton optionA, optionB, optionC, optionD;
    private ButtonGroup optionGroup;
    private JButton previousButton, nextButton, submitButton;
    private JPanel questionPanel, buttonPanel;
    private JProgressBar progressBar;

    public ExamTakingFrame(Exam exam) {
        this(exam, null);
    }

    public ExamTakingFrame(Exam exam, User currentUser) {
        this.exam = exam;
        this.currentUser = currentUser;
        this.questions = exam.getQuestions();
        this.userAnswers = new ArrayList<>();
        this.startTime = LocalDateTime.now();
        this.timeRemaining = exam.getDuration() * 60; // Convert minutes to seconds
        this.questionDAO = new QuestionDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
        startTimer();
        showQuestion(0);
    }

    private void initializeComponents() {
        // Labels
        questionNumberLabel = new JLabel();
        timeLabel = new JLabel();
        questionContentLabel = new JLabel();
        
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);
        questionNumberLabel.setFont(labelFont);
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        timeLabel.setForeground(Color.RED);
        questionContentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        // Radio buttons
        optionA = new JRadioButton();
        optionB = new JRadioButton();
        optionC = new JRadioButton();
        optionD = new JRadioButton();
        
        optionGroup = new ButtonGroup();
        optionGroup.add(optionA);
        optionGroup.add(optionB);
        optionGroup.add(optionC);
        optionGroup.add(optionD);
        
        Font optionFont = new Font("Segoe UI", Font.PLAIN, 14);
        optionA.setFont(optionFont);
        optionB.setFont(optionFont);
        optionC.setFont(optionFont);
        optionD.setFont(optionFont);
        
        // Buttons
        previousButton = new JButton("Câu trước");
        nextButton = new JButton("Câu tiếp theo");
        submitButton = new JButton("Nộp bài");
        
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 14);
        previousButton.setFont(buttonFont);
        nextButton.setFont(buttonFont);
        submitButton.setFont(buttonFont);
        
        // Button colors (app style)
        com.quiz.ui.StyleUtil.secondary(previousButton);
        com.quiz.ui.StyleUtil.primary(nextButton);
        com.quiz.ui.StyleUtil.danger(submitButton);
        com.quiz.ui.StyleUtil.square(previousButton);
        com.quiz.ui.StyleUtil.square(nextButton);
        com.quiz.ui.StyleUtil.square(submitButton);
        
        // Progress bar
        progressBar = new JProgressBar(0, questions.size());
        progressBar.setStringPainted(true);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top panel - Question number and time
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 14, 10, 14));
        topPanel.setBackground(new Color(245, 245, 248));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(questionNumberLabel);
        leftPanel.setBackground(topPanel.getBackground());
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(timeLabel);
        rightPanel.setBackground(topPanel.getBackground());
        
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        
        // Progress bar
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBorder(new EmptyBorder(0, 14, 10, 14));
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        // Center panel - Question and options
        questionPanel = new JPanel(new BorderLayout());
        questionPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel questionContentPanel = new JPanel(new BorderLayout());
        questionContentPanel.add(questionContentLabel, BorderLayout.NORTH);
        
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 16, 16));
        optionsPanel.add(optionA);
        optionsPanel.add(optionB);
        optionsPanel.add(optionC);
        optionsPanel.add(optionD);
        
        questionContentPanel.add(optionsPanel, BorderLayout.CENTER);
        questionPanel.add(questionContentPanel, BorderLayout.CENTER);
        
        // Bottom panel - Navigation buttons
        buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(Box.createHorizontalStrut(50));
        buttonPanel.add(submitButton);
        
        // Center area contains progress + question
        JPanel center = new JPanel(new BorderLayout());
        center.add(progressPanel, BorderLayout.NORTH);
        center.add(questionPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        previousButton.addActionListener(e -> previousQuestion());
        nextButton.addActionListener(e -> nextQuestion());
        submitButton.addActionListener(e -> submitExam());
        
        // Prevent closing during exam
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(
                    ExamTakingFrame.this,
                    "Bạn có chắc chắn muốn thoát? Bài thi sẽ được nộp tự động.",
                    "Xác nhận thoát",
                    JOptionPane.YES_NO_OPTION
                );
                if (option == JOptionPane.YES_OPTION) {
                    submitExam();
                }
            }
        });

        // Keyboard navigation
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("RIGHT"), "next");
        getRootPane().getActionMap().put("next", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { if (nextButton.isEnabled()) nextQuestion(); }
        });
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("LEFT"), "prev");
        getRootPane().getActionMap().put("prev", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { if (previousButton.isEnabled()) previousQuestion(); }
        });
    }

    private void setupFrame() {
        setTitle("Thi trắc nghiệm - " + exam.getTitle());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                updateTimeDisplay();
                
                if (timeRemaining <= 0) {
                    timer.stop();
                    JOptionPane.showMessageDialog(ExamTakingFrame.this, 
                        "Hết thời gian! Bài thi sẽ được nộp tự động.", 
                        "Hết thời gian", JOptionPane.INFORMATION_MESSAGE);
                    submitExam();
                }
            }
        });
        timer.start();
    }

    private void updateTimeDisplay() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timeLabel.setText(String.format("Thời gian còn lại: %02d:%02d", minutes, seconds));
        if (timeRemaining <= 60) {
            timeLabel.setForeground(new Color(220, 53, 69));
        } else {
            timeLabel.setForeground(new Color(40, 167, 69));
        }
    }

    private void showQuestion(int index) {
        if (index < 0 || index >= questions.size()) {
            return;
        }
        
        currentQuestionIndex = index;
        Question question = questions.get(index);
        
        // Update question number
        questionNumberLabel.setText("Câu " + (index + 1) + " / " + questions.size());
        
        // Update progress bar
        progressBar.setValue(index + 1);
        progressBar.setString(String.format("Tiến độ: %d/%d", index + 1, questions.size()));
        
        // Update question content
        questionContentLabel.setText("<html><body style='width: 600px'>" + question.getContent() + "</body></html>");
        
        // Update options
        optionA.setText("A. " + question.getOptionA());
        optionB.setText("B. " + question.getOptionB());
        optionC.setText("C. " + question.getOptionC());
        optionD.setText("D. " + question.getOptionD());
        
        // Clear selection
        optionGroup.clearSelection();
        
        // Load previous answer if exists
        loadPreviousAnswer(question.getId());
        
        // Update button states
        previousButton.setEnabled(index > 0);
        nextButton.setEnabled(index < questions.size() - 1);
    }

    private void loadPreviousAnswer(int questionId) {
        for (UserAnswer answer : userAnswers) {
            if (answer.getQuestionId() == questionId) {
                String selectedOptions = answer.getSelectedOptions();
                if (selectedOptions != null) {
                    if (selectedOptions.contains("A")) optionA.setSelected(true);
                    if (selectedOptions.contains("B")) optionB.setSelected(true);
                    if (selectedOptions.contains("C")) optionC.setSelected(true);
                    if (selectedOptions.contains("D")) optionD.setSelected(true);
                }
                break;
            }
        }
    }

    private void saveCurrentAnswer() {
        Question currentQuestion = questions.get(currentQuestionIndex);
        String selectedOptions = "";
        
        if (optionA.isSelected()) selectedOptions += "A";
        if (optionB.isSelected()) selectedOptions += "B";
        if (optionC.isSelected()) selectedOptions += "C";
        if (optionD.isSelected()) selectedOptions += "D";
        
        // Update or add answer
        boolean found = false;
        for (UserAnswer answer : userAnswers) {
            if (answer.getQuestionId() == currentQuestion.getId()) {
                answer.setSelectedOptions(selectedOptions);
                found = true;
                break;
            }
        }
        
        if (!found) {
            UserAnswer newAnswer = new UserAnswer(0, currentQuestion.getId(), selectedOptions);
            userAnswers.add(newAnswer);
        }
    }

    private void previousQuestion() {
        saveCurrentAnswer();
        showQuestion(currentQuestionIndex - 1);
    }

    private void nextQuestion() {
        saveCurrentAnswer();
        showQuestion(currentQuestionIndex + 1);
    }

    private void submitExam() {
        saveCurrentAnswer();
        
        if (timer != null) {
            timer.stop();
        }
        
        // Calculate score
        double score = calculateScore();
        
        // Save exam result
        ExamResultDAO examResultDAO = new ExamResultDAO();
        int userId = (currentUser != null) ? currentUser.getId() : 1; // Fallback to admin if no current user
        // Debug log
        System.out.println("DEBUG - ExamTakingFrame.submitExam() - Current User: " + (currentUser != null ? currentUser.getUsername() : "null"));
        System.out.println("DEBUG - ExamTakingFrame.submitExam() - User ID: " + userId);
        ExamResult result = new ExamResult(userId, exam.getId(), startTime);
        result.setEndTime(LocalDateTime.now());
        result.setScore(score);
        
        if (examResultDAO.addExamResult(result, userAnswers)) {
            JOptionPane.showMessageDialog(this, 
                String.format("Nộp bài thành công!\nĐiểm số: %.1f/10", score), 
                "Kết quả", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi nộp bài!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        dispose();
    }

    private double calculateScore() {
        int correctAnswers = 0;
        int totalQuestions = questions.size();
        
        for (Question question : questions) {
            UserAnswer userAnswer = null;
            for (UserAnswer answer : userAnswers) {
                if (answer.getQuestionId() == question.getId()) {
                    userAnswer = answer;
                    break;
                }
            }
            
            if (userAnswer != null && userAnswer.getSelectedOptions() != null) {
                String userSelection = userAnswer.getSelectedOptions();
                
                // Lấy đáp án đúng từ database
                Question fullQuestion = questionDAO.getQuestionById(question.getId());
                List<String> correctOptions = fullQuestion != null ? fullQuestion.getCorrectAnswers() : null;
                
                if (correctOptions != null && !correctOptions.isEmpty()) {
                    boolean isCorrect = true;
                    for (String correctOption : correctOptions) {
                        if (!userSelection.contains(correctOption)) {
                            isCorrect = false;
                            break;
                        }
                    }
                    if (isCorrect && userSelection.length() == correctOptions.size()) {
                        correctAnswers++;
                    }
                }
            }
        }
        
        return (double) correctAnswers / totalQuestions * 10;
    }
}
