package com.quiz.ui;

import com.quiz.dao.ExamResultDAO;
import com.quiz.dao.QuestionDAO;
import com.quiz.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Giao diện thi trắc nghiệm
 */
public class ExamTakingFrame extends JFrame {

    public enum QuizMode {
        EASY, MEDIUM, HARD, QUIZ_GAME
    }

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
    private JLabel progressTextLabel;
    private JRadioButton optionA, optionB, optionC, optionD;
    private ButtonGroup optionGroup;
    private JButton previousButton, nextButton, submitButton;
    private JPanel questionPanel, buttonPanel;
    private JProgressBar progressBar;

    // Enhanced UI Components
    private CircularTimerPanel timerPanel;
    private QuestionNavigationPanel navigationPanel;
    private JPanel scorePanel;
    private JLabel scoreLabel;
    private JButton hintButton;
    private int currentStreak = 0;
    private int totalCorrect = 0;
    private boolean showImmediateFeedback = false; // Disabled for traditional quiz
    private QuizMode currentMode = QuizMode.EASY;
    private boolean showHints = false;
    private int timePerQuestion = 60; // Default 1 minute per question

    public ExamTakingFrame(Exam exam) {
        this(exam, null, QuizMode.EASY);
    }

    public ExamTakingFrame(Exam exam, User currentUser) {
        this(exam, currentUser, QuizMode.EASY);
    }

    public ExamTakingFrame(Exam exam, User currentUser, QuizMode mode) {
        this.exam = exam;
        this.currentUser = currentUser;
        this.currentMode = mode;
        this.questions = exam.getQuestions();
        this.userAnswers = new ArrayList<>();
        this.startTime = LocalDateTime.now();
        this.timeRemaining = exam.getDuration() * 60; // Convert minutes to seconds
        this.questionDAO = new QuestionDAO();

        // Apply mode-specific settings
        applyModeSettings();
        
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
        
        // Enhanced Radio buttons with hover effects
        optionA = createStyledRadioButton();
        optionB = createStyledRadioButton();
        optionC = createStyledRadioButton();
        optionD = createStyledRadioButton();
        
        optionGroup = new ButtonGroup();
        optionGroup.add(optionA);
        optionGroup.add(optionB);
        optionGroup.add(optionC);
        optionGroup.add(optionD);
        
        // Add immediate feedback listeners
        addImmediateFeedbackListeners();
        
        // Buttons
        previousButton = new JButton("Câu trước");
        nextButton = new JButton("Câu tiếp theo");
        submitButton = new JButton("Nộp bài");
        hintButton = new JButton("Gợi ý");
        
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 14);
        previousButton.setFont(buttonFont);
        nextButton.setFont(buttonFont);
        submitButton.setFont(buttonFont);
        hintButton.setFont(buttonFont);
        
        // Button colors (app style)
        com.quiz.ui.StyleUtil.secondary(previousButton);
        com.quiz.ui.StyleUtil.primary(nextButton);
        com.quiz.ui.StyleUtil.danger(submitButton);
        com.quiz.ui.StyleUtil.warning(hintButton);
        com.quiz.ui.StyleUtil.square(previousButton);
        com.quiz.ui.StyleUtil.square(nextButton);
        com.quiz.ui.StyleUtil.square(submitButton);
        com.quiz.ui.StyleUtil.square(hintButton);

        // button icon (use slightly larger icons for better sharpness)
        hintButton.setIcon(com.quiz.ui.IconUtil.load("/images/hint.png", 20, 20));
        previousButton.setIcon(com.quiz.ui.IconUtil.load("/images/left-arrow.png", 20, 20));
        nextButton.setIcon(com.quiz.ui.IconUtil.load("/images/right-arrow.png", 20, 20));
        submitButton.setIcon(com.quiz.ui.IconUtil.load("/images/submit.png", 20, 20));

        // increase button size for clearer icons and better touch target
        Dimension navBtnSize = new Dimension(140, 40);
        Dimension actionBtnSize = new Dimension(130, 40);
        previousButton.setPreferredSize(navBtnSize);
        nextButton.setPreferredSize(navBtnSize);
        submitButton.setPreferredSize(actionBtnSize);
        hintButton.setPreferredSize(actionBtnSize);

        // add a bit of space between icon and text
        previousButton.setIconTextGap(8);
        nextButton.setIconTextGap(8);
        submitButton.setIconTextGap(8);
        hintButton.setIconTextGap(8);
        // Progress bar
        progressBar = new JProgressBar(0, questions.size());
        progressBar.setStringPainted(false); // we'll show text in a separate label to avoid overlap
        progressTextLabel = new JLabel("Tiến độ: 0/" + questions.size(), SwingConstants.CENTER);
        progressTextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        progressTextLabel.setForeground(new Color(100, 116, 139));
        // Modern look
        progressBar.setForeground(new Color(59, 130, 246));
        progressBar.setBackground(new Color(229, 231, 235));
        progressBar.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        progressBar.setPreferredSize(new Dimension(10, 14));

        // Enhanced components
        timerPanel = new CircularTimerPanel(timeRemaining);
        navigationPanel = new QuestionNavigationPanel(questions.size());

        // Score panel
        scorePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        scoreLabel = new JLabel("Điểm: 0");
        // streakLabel = new JLabel("Chuỗi: 0");
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        // streakLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        scoreLabel.setForeground(new Color(34, 197, 94));
        // streakLabel.setForeground(new Color(245, 158, 11));
        scorePanel.add(scoreLabel);
        scorePanel.add(Box.createHorizontalStrut(20));
        // scorePanel.add(streakLabel);
    }

    private JRadioButton createStyledRadioButton() {
        JRadioButton radioButton = new JRadioButton();
        radioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        radioButton.setOpaque(true);
        radioButton.setBackground(Color.WHITE);
        radioButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        // Add hover effect
        radioButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!radioButton.isSelected()) {
                    radioButton.setBackground(new Color(240, 248, 255));
                    radioButton.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(59, 130, 246), 2),
                            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!radioButton.isSelected()) {
                    radioButton.setBackground(Color.WHITE);
                    radioButton.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                }
            }
        });

        return radioButton;
    }

    private void applyModeSettings() {
        switch (currentMode) {
            case EASY:
                // Easy: with hints, 1 minute per question
                showHints = true;
                timePerQuestion = 60;
                timeRemaining = questions.size() * timePerQuestion;
                showImmediateFeedback = true;
                break;
            case MEDIUM:
                // Medium: random questions, 1 minute per question, no hints
                showHints = false;
                timePerQuestion = 60;
                timeRemaining = questions.size() * timePerQuestion;
                showImmediateFeedback = false;
                shuffleQuestions(); // Randomize question order
                break;
            case HARD:
                // Hard: no hints, 10 seconds per question
                showHints = false;
                timePerQuestion = 10;
                timeRemaining = questions.size() * timePerQuestion;
                showImmediateFeedback = false;
                break;
            case QUIZ_GAME:
                // Quiz game: drag and drop matching
                showHints = false;
                timePerQuestion = 60; // 1 minute for drag and drop
                timeRemaining = questions.size() * timePerQuestion;
                showImmediateFeedback = true;
                break;
        }
    }

    private void shuffleQuestions() {
        // Simple shuffle algorithm
        for (int i = questions.size() - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            Question temp = questions.get(i);
            questions.set(i, questions.get(j));
            questions.set(j, temp);
        }
    }

    private void addImmediateFeedbackListeners() {
        // No automatic feedback on selection - user will use buttons for feedback
    }

    private void provideImmediateFeedback() {
        Question currentQuestion = questions.get(currentQuestionIndex);
        String selectedOptions = "";

        if (currentMode == QuizMode.QUIZ_GAME) {
            // Handle drag and drop feedback
            selectedOptions = getDragAndDropAnswer();
        } else {
            // Handle regular multiple choice feedback
            if (optionA.isSelected())
                selectedOptions += "A";
            if (optionB.isSelected())
                selectedOptions += "B";
            if (optionC.isSelected())
                selectedOptions += "C";
            if (optionD.isSelected())
                selectedOptions += "D";
        }

        if (selectedOptions.isEmpty())
            return;

        // Get correct answer
        Question fullQuestion = questionDAO.getQuestionById(currentQuestion.getId());
        List<String> correctOptions = fullQuestion != null ? fullQuestion.getCorrectAnswers() : null;

        boolean isCorrect = false;
        if (correctOptions != null && !correctOptions.isEmpty()) {
            isCorrect = true;
            for (String correctOption : correctOptions) {
                if (!selectedOptions.contains(correctOption)) {
                    isCorrect = false;
                    break;
                }
            }
            if (isCorrect && selectedOptions.length() != correctOptions.size()) {
                isCorrect = false;
            }
        }

        // Visual feedback
        if (isCorrect) {
            showCorrectFeedback();
            currentStreak++;
            totalCorrect++;
        } else {
            showIncorrectFeedback();
            currentStreak = 0;
        }

        // Update score display
        updateScoreDisplay();

        // Don't mark answers in navigation panel during exam
        // This will be done after submission
    }

    private String getDragAndDropAnswer() {
        // Get the answer from fill-in-the-blanks interface
        Component[] components = questionPanel.getComponents();
        for (Component component : components) {
            if (component instanceof FillInTheBlanksPanel) {
                FillInTheBlanksPanel panel = (FillInTheBlanksPanel) component;
                return panel.getSelectedAnswer();
            }
        }
        return "";
    }

    private void showCorrectFeedback() {
        if (!showImmediateFeedback)
            return;

        switch (currentMode) {
            case EASY:
                // Easy mode: show positive feedback with gentle green flash
                flashColor(new Color(34, 197, 94), 200);
                showHint("🎉 Tuyệt vời! Bạn đã trả lời đúng!");
                break;
            case QUIZ_GAME:
                // Quiz game: fun feedback for matching
                flashColor(new Color(34, 197, 94), 200);
                showCelebrationEffect();
                break;
            default:
                // Simple flash for other modes
                flashColor(new Color(34, 197, 94), 200);
                break;
        }
    }

    private void showIncorrectFeedback() {
        if (!showImmediateFeedback)
            return;

        switch (currentMode) {
            case EASY:
                // Easy mode: show encouraging feedback with gentle orange flash
                flashColor(new Color(245, 158, 11), 200); // Orange instead of red
                showHint("Chưa đúng rồi! Hãy thử lại hoặc dùng gợi ý nhé!");
                break;
            case QUIZ_GAME:
                // Quiz game: simple feedback for matching
                flashColor(new Color(245, 158, 11), 200); // Orange instead of red
                break;
            default:
                // Simple flash for other modes
                flashColor(new Color(245, 158, 11), 200); // Orange instead of red
                break;
        }
    }

    private void showHint(String message) {
        JOptionPane.showMessageDialog(this, message, "Gợi ý",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showQuestionHint() {
        if (!showHints)
            return;

        Question currentQuestion = questions.get(currentQuestionIndex);
        Question fullQuestion = questionDAO.getQuestionById(currentQuestion.getId());
        List<String> correctOptions = fullQuestion != null ? fullQuestion.getCorrectAnswers() : null;

        String hintMessage;
        if (correctOptions != null && !correctOptions.isEmpty()) {
            hintMessage = "Gợi ý: Đáp án đúng là " + String.join(", ", correctOptions);
        } else {
            hintMessage = "Gợi ý: Hãy đọc kỹ câu hỏi và các lựa chọn!";
        }

        JOptionPane.showMessageDialog(this, hintMessage, "Gợi ý",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void flashColor(Color color, int duration) {
        Color originalColor = questionPanel.getBackground();
        questionPanel.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));

        Timer flashTimer = new Timer(duration, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                questionPanel.setBackground(originalColor);
                ((Timer) e.getSource()).stop();
            }
        });
        flashTimer.start();
    }

    private void showCelebrationEffect() {
        // Simple celebration effect for arcade mode
        Timer celebrationTimer = new Timer(100, new ActionListener() {
            private int count = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (count < 5) {
                    questionPanel.setBackground(new Color(255, 215, 0, 100)); // Gold
                    count++;
                } else {
                    questionPanel.setBackground(Color.WHITE);
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        celebrationTimer.start();
    }

    private void updateScoreDisplay() {
        // Hide score and streak during exam - only show after submission
        scoreLabel.setText("Điểm: --");
        // streakLabel.setText("🔥 Chuỗi: --");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top panel - Enhanced with timer and score
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(16, 20, 16, 20));
        topPanel.setBackground(new Color(247, 249, 252));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(questionNumberLabel);
        leftPanel.add(Box.createHorizontalStrut(20));
        leftPanel.add(scorePanel);
        leftPanel.setBackground(topPanel.getBackground());
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(timerPanel);
        rightPanel.setBackground(topPanel.getBackground());
        
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        
        // Progress bar
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBackground(new Color(248, 250, 252));
        progressPanel.setBorder(new EmptyBorder(0, 20, 16, 20));
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(progressTextLabel, BorderLayout.SOUTH);

        // Main content area
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(248, 250, 252));

        // Center panel - Question and options with card design
        questionPanel = new JPanel(new BorderLayout());
        questionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(24, 24, 24, 24)));
        questionPanel.setBackground(Color.WHITE);
        
        JPanel questionContentPanel = new JPanel(new BorderLayout());
        questionContentPanel.setBackground(Color.WHITE);
        questionContentPanel.add(questionContentLabel, BorderLayout.NORTH);
        
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 12, 12));
        optionsPanel.setBackground(Color.WHITE);
        optionsPanel.setBorder(new EmptyBorder(12, 4, 4, 4));
        optionsPanel.add(optionA);
        optionsPanel.add(optionB);
        optionsPanel.add(optionC);
        optionsPanel.add(optionD);
        
        questionContentPanel.add(optionsPanel, BorderLayout.CENTER);
        questionPanel.add(questionContentPanel, BorderLayout.CENTER);

        // Question navigation panel
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(mainContent.getBackground());
        navPanel.setBorder(new EmptyBorder(12, 20, 16, 20));
        navPanel.add(navigationPanel, BorderLayout.CENTER);

        mainContent.add(progressPanel, BorderLayout.NORTH);

        // Center the question card neatly
        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setBackground(mainContent.getBackground());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(16, 20, 16, 20);
        centerWrap.add(questionPanel, gbc);
        mainContent.add(centerWrap, BorderLayout.CENTER);
        mainContent.add(navPanel, BorderLayout.SOUTH);
        
        // Bottom panel - Navigation buttons
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        buttonPanel.setBackground(mainContent.getBackground());
        buttonPanel.setBorder(new EmptyBorder(16, 20, 20, 20));
        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);

        // Add hint button and check answer button for Easy mode
        if (showHints) {
            buttonPanel.add(Box.createHorizontalStrut(20));
            buttonPanel.add(hintButton);

            // Add check answer button for Easy mode
            JButton checkAnswerButton = new JButton("Kiểm tra đáp án");
            checkAnswerButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            StyleUtil.success(checkAnswerButton);
            StyleUtil.square(checkAnswerButton);
            checkAnswerButton.addActionListener(e -> provideImmediateFeedback());
            checkAnswerButton.setIcon(com.quiz.ui.IconUtil.load("/images/accept.png", 20, 20));
            checkAnswerButton.setPreferredSize(new Dimension(150, 40));
            checkAnswerButton.setIconTextGap(8);

            buttonPanel.add(Box.createHorizontalStrut(10));
            buttonPanel.add(checkAnswerButton);
        }

        buttonPanel.add(Box.createHorizontalStrut(50));
        buttonPanel.add(submitButton);

        add(topPanel, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        previousButton.addActionListener(e -> previousQuestion());
        nextButton.addActionListener(e -> nextQuestion());
        submitButton.addActionListener(e -> submitExam());
        hintButton.addActionListener(e -> showQuestionHint());
        
        // Prevent closing during exam
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(
                    ExamTakingFrame.this,
                    "Bạn có chắc chắn muốn thoát? Bài thi sẽ được nộp tự động.",
                    "Xác nhận thoát",
                        JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    forceSubmitExam();
                }
            }
        });

        // Keyboard navigation
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("RIGHT"), "next");
        getRootPane().getActionMap().put("next", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nextButton.isEnabled())
                    nextQuestion();
            }
        });
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("LEFT"), "prev");
        getRootPane().getActionMap().put("prev", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (previousButton.isEnabled())
                    previousQuestion();
            }
        });
    }

    private void setupFrame() {
        Object modeIcon = getModeIcon();
        if (modeIcon instanceof ImageIcon) {
            setIconImage(((ImageIcon) modeIcon).getImage());
            setTitle("Thi trắc nghiệm - " + exam.getTitle() + " (" + getModeName() + ")");
        } else {
            setTitle(modeIcon.toString() + " Thi trắc nghiệm - " + exam.getTitle() + " (" + getModeName() + ")");
        }
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(true);
        setSize(1000, 700);
        setMinimumSize(new Dimension(900, 650));
        setLocationRelativeTo(null);
    }

    private Object getModeIcon() {
        switch (currentMode) {
            case EASY:
                return com.quiz.ui.IconUtil.load("/images/one.png", 16, 16);
            case MEDIUM:
                return com.quiz.ui.IconUtil.load("/images/two.png", 16, 16);
            case HARD:
                return com.quiz.ui.IconUtil.load("/images/three.png", 16, 16);
            case QUIZ_GAME:
                return com.quiz.ui.IconUtil.load("/images/joystick.png", 16, 16);
            default:
                return com.quiz.ui.IconUtil.load("/images/joystick.png", 16, 16);
        }
    }

    private String getModeName() {
        switch (currentMode) {
            case EASY:
                return "Dễ";
            case MEDIUM:
                return "Trung bình";
            case HARD:
                return "Khó";
            case QUIZ_GAME:
                return "Trò chơi";
            default:
                return "Mặc định";
        }
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

        // Update circular timer
        if (timerPanel != null) {
            timerPanel.updateTime(timeRemaining);
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
        if (progressTextLabel != null) {
            progressTextLabel.setText(String.format("Tiến độ: %d/%d", index + 1, questions.size()));
        }

        // Clear the question panel
        questionPanel.removeAll();

        if (currentMode == QuizMode.QUIZ_GAME) {
            // Show drag and drop interface for Quiz Game mode
            showDragAndDropQuestion(question);
        } else {
            // Show regular multiple choice interface
            showRegularQuestion(question);
        }
        
        // Load previous answer if exists
        loadPreviousAnswer(question.getId());
        
        // Update button states
        previousButton.setEnabled(index > 0);
        nextButton.setEnabled(index < questions.size() - 1);

        // Update navigation panel
        if (navigationPanel != null) {
            navigationPanel.updateCurrentQuestion(index);
        }

        // Add smooth transition effect
        addQuestionTransitionEffect();

        // Refresh the panel
        questionPanel.revalidate();
        questionPanel.repaint();
    }

    private void showRegularQuestion(Question question) {
        // Update question content with enhanced styling
        questionContentLabel.setText("<html><body style='width: 600px; font-size: 16px; line-height: 1.5;'>" +
                question.getContent() + "</body></html>");

        // Update options with enhanced styling
        optionA.setText("<html><body style='font-size: 14px;'>A. " + question.getOptionA() + "</body></html>");
        optionB.setText("<html><body style='font-size: 14px;'>B. " + question.getOptionB() + "</body></html>");
        optionC.setText("<html><body style='font-size: 14px;'>C. " + question.getOptionC() + "</body></html>");
        optionD.setText("<html><body style='font-size: 14px;'>D. " + question.getOptionD() + "</body></html>");

        // Clear selection
        optionGroup.clearSelection();

        // Create regular question layout
        JPanel questionContentPanel = new JPanel(new BorderLayout());
        questionContentPanel.setBackground(Color.WHITE);
        questionContentPanel.add(questionContentLabel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 12, 12));
        optionsPanel.setBackground(Color.WHITE);
        optionsPanel.add(optionA);
        optionsPanel.add(optionB);
        optionsPanel.add(optionC);
        optionsPanel.add(optionD);

        questionContentPanel.add(optionsPanel, BorderLayout.CENTER);
        questionPanel.add(questionContentPanel, BorderLayout.CENTER);
    }

    private void showDragAndDropQuestion(Question question) {
        // Create fill-in-the-blanks drag and drop interface
        FillInTheBlanksPanel fillBlanksPanel = new FillInTheBlanksPanel(question);
        questionPanel.add(fillBlanksPanel, BorderLayout.CENTER);
    }

    private void addQuestionTransitionEffect() {
        // Removed transition effect to prevent interface flashing
        // Simple repaint for smooth navigation
        questionPanel.repaint();
    }

    private void loadPreviousAnswer(int questionId) {
        for (UserAnswer answer : userAnswers) {
            if (answer.getQuestionId() == questionId) {
                String selectedOptions = answer.getSelectedOptions();
                if (selectedOptions != null) {
                    if (selectedOptions.contains("A"))
                        optionA.setSelected(true);
                    if (selectedOptions.contains("B"))
                        optionB.setSelected(true);
                    if (selectedOptions.contains("C"))
                        optionC.setSelected(true);
                    if (selectedOptions.contains("D"))
                        optionD.setSelected(true);
                }
                break;
            }
        }
    }

    private void saveCurrentAnswer() {
        Question currentQuestion = questions.get(currentQuestionIndex);
        String selectedOptions = "";
        
        if (currentMode == QuizMode.QUIZ_GAME) {
            // Handle drag and drop answers
            selectedOptions = getDragAndDropAnswer();
        } else {
            // Handle regular multiple choice answers
            if (optionA.isSelected())
                selectedOptions += "A";
            if (optionB.isSelected())
                selectedOptions += "B";
            if (optionC.isSelected())
                selectedOptions += "C";
            if (optionD.isSelected())
                selectedOptions += "D";
        }
        
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

        // Debug logging
        System.out.println("DEBUG - Saved answer for question " + currentQuestion.getId() +
                ": '" + selectedOptions + "' (Mode: " + currentMode + ")");
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
        // Show confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn nộp bài?",
                "Xác nhận nộp bài",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return; // User cancelled
        }

        processExamSubmission();
    }

    private void forceSubmitExam() {
        // Force submit without confirmation (for window close)
        processExamSubmission();
    }

    private void processExamSubmission() {
        saveCurrentAnswer();
        
        // Stop timer
        if (timer != null) {
            timer.stop();
        }

        // Disable submit button to prevent multiple submissions
        submitButton.setEnabled(false);
        
        // Calculate score
        double score = calculateScore();
        System.out.println("DEBUG - Score calculated: " + score);
        
        // Save exam result
        ExamResultDAO examResultDAO = new ExamResultDAO();
        int userId = (currentUser != null) ? currentUser.getId() : 1; // Fallback to admin if no current user
        // Debug log
        System.out.println("DEBUG - ExamTakingFrame.submitExam() - Current User: "
                + (currentUser != null ? currentUser.getUsername() : "null"));
        System.out.println("DEBUG - ExamTakingFrame.submitExam() - User ID: " + userId);
        ExamResult result = new ExamResult(userId, exam.getId(), startTime);
        result.setEndTime(LocalDateTime.now());
        result.setScore(score);
        
        System.out.println("DEBUG - Attempting to save exam result...");
        boolean saveResult = examResultDAO.addExamResult(result, userAnswers);
        System.out.println("DEBUG - Save result: " + saveResult);

        if (saveResult) {
            System.out.println("DEBUG - Showing detailed results...");
            // Calculate and show detailed results
            showDetailedResults(score, questions.size());

            System.out.println("DEBUG - Showing result dialog...");
            try {
                // Show enhanced result dialog
                ExamResultDialog resultDialog = new ExamResultDialog(
                        this, score, questions.size(), totalCorrect,
                        (exam.getDuration() * 60) - timeRemaining, exam.getDuration() * 60);
                resultDialog.setVisible(true);
            } catch (Exception e) {
                System.out.println("DEBUG - Error showing result dialog: " + e.getMessage());
                // Fallback to simple message
            JOptionPane.showMessageDialog(this, 
                String.format("Nộp bài thành công!\nĐiểm số: %.1f/10", score), 
                "Kết quả", JOptionPane.INFORMATION_MESSAGE);
            }

            System.out.println("DEBUG - Closing exam frame...");
            // Close the exam frame after result dialog is closed
            dispose();
        } else {
            System.out.println("DEBUG - Failed to save exam result");
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi nộp bài!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            // Still close the frame even if save failed
            dispose();
        }
    }

    private void showDetailedResults(double score, int totalQuestions) {
        // Calculate correct answers and update navigation panel
        totalCorrect = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            UserAnswer userAnswer = null;
            for (UserAnswer answer : userAnswers) {
                if (answer.getQuestionId() == question.getId()) {
                    userAnswer = answer;
                    break;
                }
            }

            boolean isCorrect = false;
            if (userAnswer != null && userAnswer.getSelectedOptions() != null) {
                String userSelection = userAnswer.getSelectedOptions();
                Question fullQuestion = questionDAO.getQuestionById(question.getId());
                List<String> correctOptions = fullQuestion != null ? fullQuestion.getCorrectAnswers() : null;

                if (correctOptions != null && !correctOptions.isEmpty()) {
                    isCorrect = true;
                    for (String correctOption : correctOptions) {
                        if (!userSelection.contains(correctOption)) {
                            isCorrect = false;
                            break;
                        }
                    }
                    if (isCorrect && userSelection.length() != correctOptions.size()) {
                        isCorrect = false;
                    }
                }
            }

            if (isCorrect) {
                totalCorrect++;
            }

            // Mark in navigation panel
            if (navigationPanel != null) {
                navigationPanel.markAnswered(i, isCorrect);
            }
        }

        // Update final score display
        scoreLabel.setText("Điểm: " + totalCorrect + "/" + totalQuestions);
        // streakLabel.setText("🔥 Kết quả: " + String.format("%.1f/10", score));
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

    // Enhanced UI Components Classes

    /**
     * Circular Timer Panel with visual countdown
     */
    private class CircularTimerPanel extends JPanel {
        private int totalTime;
        private int remainingTime;

        public CircularTimerPanel(int totalTimeInSeconds) {
            this.totalTime = totalTimeInSeconds;
            this.remainingTime = totalTimeInSeconds;
            setPreferredSize(new Dimension(120, 120));
            setOpaque(false);
        }

        public void updateTime(int remainingTime) {
            this.remainingTime = remainingTime;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 20;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            // Background circle
            g2d.setColor(new Color(240, 240, 240));
            g2d.fillOval(x, y, size, size);

            // Progress arc
            double progress = (double) remainingTime / totalTime;
            int arcAngle = (int) (360 * progress);

            if (remainingTime <= 60) {
                g2d.setColor(new Color(239, 68, 68)); // Red for last minute
            } else if (remainingTime <= 300) {
                g2d.setColor(new Color(245, 158, 11)); // Orange for last 5 minutes
            } else {
                g2d.setColor(new Color(34, 197, 94)); // Green for normal time
            }

            g2d.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawArc(x + 4, y + 4, size - 8, size - 8, 90, arcAngle);

            // Time text
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
            String timeText = String.format("%02d:%02d", remainingTime / 60, remainingTime % 60);
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (size - fm.stringWidth(timeText)) / 2;
            int textY = y + size / 2 + fm.getAscent() / 2;
            g2d.drawString(timeText, textX, textY);

            g2d.dispose();
        }
    }

    /**
     * Question Navigation Panel with visual question map
     */
    private class QuestionNavigationPanel extends JPanel {
        private List<JButton> questionButtons;
        private int currentQuestion;

        public QuestionNavigationPanel(int totalQuestions) {
            this.questionButtons = new ArrayList<>();
            this.currentQuestion = 0;

            setLayout(new GridLayout(0, 5, 5, 5));
            setBorder(BorderFactory.createTitledBorder("Điều hướng câu hỏi"));

            for (int i = 0; i < totalQuestions; i++) {
                JButton btn = new JButton(String.valueOf(i + 1));
                btn.setPreferredSize(new Dimension(40, 40));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                btn.putClientProperty("questionIndex", i);

                // Style the button
                StyleUtil.square(btn);
                btn.setBackground(new Color(108, 117, 125));
                btn.setForeground(Color.WHITE);

                final int questionIndex = i;
                btn.addActionListener(e -> {
                    saveCurrentAnswer();
                    showQuestion(questionIndex);
                });

                questionButtons.add(btn);
                add(btn);
            }
        }

        public void updateCurrentQuestion(int questionIndex) {
            if (currentQuestion >= 0 && currentQuestion < questionButtons.size()) {
                questionButtons.get(currentQuestion).setBackground(new Color(108, 117, 125));
            }

            currentQuestion = questionIndex;
            if (questionIndex >= 0 && questionIndex < questionButtons.size()) {
                questionButtons.get(questionIndex).setBackground(new Color(59, 130, 246));
            }
        }

        public void markAnswered(int questionIndex, boolean isCorrect) {
            if (questionIndex >= 0 && questionIndex < questionButtons.size()) {
                JButton btn = questionButtons.get(questionIndex);
                if (isCorrect) {
                    btn.setBackground(new Color(34, 197, 94));
                } else {
                    btn.setBackground(new Color(239, 68, 68));
                }
            }
        }
    }

    // =================================================================================
    // == CORRECTED DRAG AND DROP IMPLEMENTATION STARTING HERE ==
    // =================================================================================

    /**
     * Fill in the Blanks Panel for Quiz Game mode
     */
    private class FillInTheBlanksPanel extends JPanel {
        private Question question;
        private JPanel questionTextPanel;
        private JPanel answerOptionsPanel;
        private List<BlankSlot> blankSlots;
        private String selectedAnswer = "";

        public FillInTheBlanksPanel(Question question) {
            this.question = question;
            this.blankSlots = new ArrayList<>();

            // Sử dụng BoxLayout để xếp chồng các thành phần theo chiều dọc (Y_AXIS)
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(20, 20, 20, 20));

            initializeComponents();
            setupLayout();
        }

        private void initializeComponents() {
            // Create question area and a dedicated row for blanks
            questionTextPanel = new JPanel(new GridBagLayout());
            questionTextPanel.setBackground(Color.WHITE);
            questionTextPanel.setBorder(new EmptyBorder(0, 0, 12, 0));
            GridBagConstraints qgbc = new GridBagConstraints();
            qgbc.gridx = 0; qgbc.gridy = 0; qgbc.anchor = GridBagConstraints.CENTER;
            qgbc.insets = new Insets(0, 0, 10, 0);

            JPanel blanksRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            blanksRow.setOpaque(false);

            // Parse question content and create blanks
            String questionText = question.getContent();

            if (questionText.contains("[blank]")) {
                String[] parts = questionText.split("\\[blank\\]");
                StringBuilder html = new StringBuilder("<html><span style='font-size:18px;'>");
                for (int i = 0; i < parts.length; i++) {
                    html.append(parts[i]);
                    if (i < parts.length - 1) {
                        // visual underline placeholder in the sentence
                        html.append("<span style='border-bottom:2px dashed #94a3b8;padding:0 50px;'></span>");
                        BlankSlot blank = new BlankSlot();
                        blankSlots.add(blank);
                        blanksRow.add(blank);
                    }
                }
                html.append("</span></html>");
                JLabel questionLabel = new JLabel(html.toString());
                questionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
                questionTextPanel.add(questionLabel, qgbc);
            } else {
                JLabel questionLabel = new JLabel(questionText);
                questionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
                questionTextPanel.add(questionLabel, qgbc);
                BlankSlot blank = new BlankSlot();
                blankSlots.add(blank);
                blanksRow.add(blank);
            }

            // add blanks row as a second line
            qgbc.gridy = 1;
            questionTextPanel.add(blanksRow, qgbc);

            // Answer options (draggable items)
            answerOptionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            answerOptionsPanel.setBackground(new Color(241, 245, 249)); // Màu nền xám nhạt
            answerOptionsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            String[] options = { question.getOptionA(), question.getOptionB(), question.getOptionC(),
                    question.getOptionD() };
            List<String> shuffledOptions = new ArrayList<>(List.of(options));
            java.util.Collections.shuffle(shuffledOptions);

            for (String option : shuffledOptions) {
                if (option != null && !option.trim().isEmpty()) {
                    DraggableLabel draggable = new DraggableLabel(option, this);
                    answerOptionsPanel.add(draggable);
                }
            }
        }

        private void setupLayout() {
            // Panel trên chứa câu hỏi và ô thả
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setBackground(Color.WHITE);

            JLabel instructionLabel = new JLabel("Kéo thả từ vào chỗ trống trong câu hỏi:");
            instructionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
            instructionLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

            topPanel.add(instructionLabel, BorderLayout.NORTH);
            topPanel.add(questionTextPanel, BorderLayout.CENTER);

            // Panel dưới chứa các đáp án để kéo
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.setBackground(Color.WHITE);

            JLabel optionsLabel = new JLabel("Kéo kết quả đúng vào ô trống");
            optionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            optionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            optionsLabel.setBorder(new EmptyBorder(20, 0, 10, 0));

            bottomPanel.add(optionsLabel, BorderLayout.NORTH);
            bottomPanel.add(answerOptionsPanel, BorderLayout.CENTER);

            // Thêm các panel vào FillInTheBlanksPanel theo chiều dọc
            add(topPanel);
            add(Box.createRigidArea(new Dimension(0, 30))); // Thêm khoảng trống giữa 2 panel
            add(bottomPanel);
        }

        public String getSelectedAnswer() {
            StringBuilder answer = new StringBuilder();
            for (BlankSlot slot : blankSlots) {
                if (!slot.isEmpty()) {
                    String slotAnswer = slot.getCurrentAnswer();
                    String[] options = { question.getOptionA(), question.getOptionB(), question.getOptionC(),
                            question.getOptionD() };
                    for (int i = 0; i < 4; i++) {
                        if (slotAnswer.equals(options[i])) {
                            answer.append((char) ('A' + i));
                            break;
                        }
                    }
                }
            }
            return answer.toString();
        }

        public void checkCompletion() {
            boolean allFilled = true;
            for (BlankSlot slot : blankSlots) {
                if (slot.isEmpty()) {
                    allFilled = false;
                    break;
                }
            }
            if (allFilled) {
                SwingUtilities.invokeLater(() -> provideImmediateFeedback());
            }
        }
    }

    /**
     * Draggable Label with correct JLayeredPane implementation
     */
    private class DraggableLabel extends JLabel {
        private String optionText;
        private boolean isDragging = false;
        private Point dragOffset;
        private Container originalParent;
        private FillInTheBlanksPanel mainPanel;
        private BlankSlot lastHoveredSlot = null;

        public DraggableLabel(String text, FillInTheBlanksPanel mainPanel) {
            super(text);
            this.optionText = text;
            this.mainPanel = mainPanel;

            setOpaque(true);
            setBackground(new Color(59, 130, 246));
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(37, 99, 235), 2),
                    new EmptyBorder(10, 15, 10, 15)));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            MouseAdapter ma = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    isDragging = true;
                    dragOffset = e.getPoint();
                    originalParent = getParent();

                    JLayeredPane layeredPane = getRootPane().getLayeredPane();
                    Point layeredPanePoint = SwingUtilities.convertPoint(DraggableLabel.this, new Point(0, 0),
                            layeredPane);

                    originalParent.remove(DraggableLabel.this);

                    layeredPane.add(DraggableLabel.this, JLayeredPane.DRAG_LAYER);
                    setBounds(layeredPanePoint.x, layeredPanePoint.y, getPreferredSize().width,
                            getPreferredSize().height);

                    setBackground(new Color(37, 99, 235));
                    layeredPane.repaint();
                }

                // Thay thế HOÀN TOÀN phương thức mouseDragged cũ bằng phương thức mới này
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (isDragging) {
                        // Di chuyển label (giữ nguyên)
                        Point mousePointOnLayeredPane = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
                                getParent());
                        int newX = mousePointOnLayeredPane.x - dragOffset.x;
                        int newY = mousePointOnLayeredPane.y - dragOffset.y;
                        setLocation(newX, newY);

                        // === PHẦN LOGIC HIGHLIGHT MỚI ===
                        Point dropPointInLayeredPane = SwingUtilities.convertPoint(DraggableLabel.this,
                                new Point(dragOffset.x, dragOffset.y), getParent());
                        Point dropPointInQuestionPanel = SwingUtilities.convertPoint(getParent(),
                                dropPointInLayeredPane, mainPanel.questionTextPanel);

                        Component target = mainPanel.questionTextPanel.getComponentAt(dropPointInQuestionPanel);
                        // if getComponentAt returns the container itself, try to find the nearest BlankSlot manually
                        if (!(target instanceof BlankSlot)) {
                            for (Component comp : mainPanel.questionTextPanel.getComponents()) {
                                if (comp instanceof BlankSlot && comp.getBounds().contains(dropPointInQuestionPanel)) {
                                    target = comp;
                                    break;
                                }
                            }
                        }

                        BlankSlot currentSlot = null;
                        if (target instanceof BlankSlot) {
                            currentSlot = (BlankSlot) target;
                        }

                        // Nếu slot hiện tại khác slot trước đó
                        if (currentSlot != lastHoveredSlot) {
                            // Bỏ highlight slot cũ (nếu có)
                            if (lastHoveredSlot != null) {
                                lastHoveredSlot.unhighlight();
                            }

                            // Highlight slot mới (nếu có và đang trống)
                            if (currentSlot != null && currentSlot.isEmpty()) {
                                currentSlot.highlight();
                                lastHoveredSlot = currentSlot;
                            } else {
                                lastHoveredSlot = null;
                            }
                        }
                    }
                }

                // Sửa lại phương thức mouseReleased
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (isDragging) {
                        isDragging = false;

                        // Bỏ highlight slot cuối cùng khi thả chuột
                        if (lastHoveredSlot != null) {
                            lastHoveredSlot.unhighlight();
                            lastHoveredSlot = null;
                        }

                        // Phần code còn lại giữ nguyên...
                        JLayeredPane layeredPane = (JLayeredPane) getParent();

                        // Convert drop point to the coordinate system of the main question text panel
                        Point dropPointInLayeredPane = SwingUtilities.convertPoint(DraggableLabel.this, e.getPoint(),
                                layeredPane);
                        Point dropPointInQuestionPanel = SwingUtilities.convertPoint(layeredPane,
                                dropPointInLayeredPane, mainPanel.questionTextPanel);

                        Component target = mainPanel.questionTextPanel.getComponentAt(dropPointInQuestionPanel);

                        boolean droppedOnSlot = false;
                        if (target instanceof BlankSlot) {
                            BlankSlot slot = (BlankSlot) target;
                            if (slot.isEmpty()) {
                                slot.setAnswer(optionText);
                                setVisible(false); // Hide label after successful drop
                                droppedOnSlot = true;

                                // Save the current answer immediately
                                saveCurrentAnswer();

                                mainPanel.checkCompletion();
                                // Make sure the slot is at the front of its parent for visibility
                                Container parent = slot.getParent();
                                if (parent != null) {
                                    try {
                                        parent.setComponentZOrder(slot, 0);
                                    } catch (Exception ignore) {
                                    }
                                    parent.revalidate();
                                    parent.repaint();
                                }
                            }
                        }

                        // Must remove from layeredPane regardless of drop success
                        layeredPane.remove(DraggableLabel.this);

                        if (!droppedOnSlot) {
                            // Return to original position if drop was not on a valid, empty slot
                            originalParent.add(DraggableLabel.this);
                            setBackground(new Color(59, 130, 246));
                        }

                        // Repaint everything to reflect the changes
                        layeredPane.revalidate();
                        layeredPane.repaint();
                        originalParent.revalidate();
                        originalParent.repaint();
                    }
                }
            };
            addMouseListener(ma);
            addMouseMotionListener(ma);
        }
    }

    /**
     * Blank Slot for fill-in-the-blanks functionality
     */
    private class BlankSlot extends JPanel {
        private String currentAnswer;
        private JLabel label;

        public BlankSlot() {
            this.currentAnswer = "";

            setLayout(new BorderLayout());
            setBackground(new Color(240, 240, 240));
            setBorder(BorderFactory.createCompoundBorder(
                    // Giữ nguyên border gạch đứt
                    BorderFactory.createDashedBorder(Color.GRAY, 4, 2),
                    new EmptyBorder(8, 15, 8, 15)));
            setPreferredSize(new Dimension(200, 56)); // Larger to avoid any visual overlap

            // Clear placeholder text to show where to drop
            label = new JLabel("Thả vào đây");
            label.setIcon(com.quiz.ui.IconUtil.load("/images/download.png", 16, 16));
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setForeground(new Color(59, 130, 246)); // Blue color for visibility
            label.setBorder(new EmptyBorder(2, 2, 2, 2));
            add(label, BorderLayout.CENTER);
        }

        // Thêm 2 phương thức mới này vào trong lớp private class BlankSlot
        public void highlight() {
            setBackground(new Color(209, 250, 229)); // Màu xanh lá cây nhạt
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createDashedBorder(new Color(22, 163, 74), 4, 2), // Border xanh lá
                    new EmptyBorder(8, 15, 8, 15)));
        }

        public void unhighlight() {
            setBackground(new Color(240, 240, 240)); // Trở về màu xám ban đầu
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createDashedBorder(Color.GRAY, 4, 2),
                    new EmptyBorder(8, 15, 8, 15)));
        }

        public boolean isEmpty() {
            return currentAnswer.isEmpty();
        }

        public void setAnswer(String answer) {
            this.currentAnswer = answer;
            label.setText(answer);
            label.setForeground(Color.BLACK);
            setBackground(new Color(220, 252, 231)); // Light green
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(34, 197, 94), 2),
                    new EmptyBorder(8, 15, 8, 15)));
            // Ensure visibility and layout refresh
            revalidate();
            repaint();
            Container p = getParent();
            if (p != null) {
                try {
                    p.setComponentZOrder(this, 0);
                } catch (Exception ignore) {
                }
                p.revalidate();
                p.repaint();
            }
        }

        public String getCurrentAnswer() {
            return currentAnswer;
        }
    }
}