package com.quiz.ui.panels;

import com.quiz.dao.QuestionDAO;
import com.quiz.dao.SubjectDAO;
import com.quiz.dao.TopicDAO;
import com.quiz.dao.DifficultyDAO;
import com.quiz.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel quản lý câu hỏi
 */
public class QuestionManagementPanel extends JPanel {
    private JTable questionTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<Subject> subjectFilterCombo;
    private JComboBox<Topic> topicFilterCombo;
    private JComboBox<Difficulty> difficultyFilterCombo;
    private JButton addButton, editButton, deleteButton, refreshButton;
    private QuestionDAO questionDAO;
    private SubjectDAO subjectDAO;
    private TopicDAO topicDAO;
    private DifficultyDAO difficultyDAO;
    private List<Question> questions;
    private User currentUser;

    public QuestionManagementPanel() {
        this(null);
    }
    
    public QuestionManagementPanel(User currentUser) {
        this.currentUser = currentUser;
        questionDAO = new QuestionDAO();
        subjectDAO = new SubjectDAO();
        topicDAO = new TopicDAO();
        difficultyDAO = new DifficultyDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadQuestions();
    }

    private void initializeComponents() {
        // Table
        String[] columnNames = {"ID", "Nội dung", "Môn học", "Chủ đề", "Độ khó", "Người tạo", "Ngày tạo"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        questionTable = new JTable(tableModel);
        questionTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        questionTable.setRowHeight(25);
        questionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Filter combos
        subjectFilterCombo = new JComboBox<>();
        subjectFilterCombo.addItem(new Subject(-1, "Tất cả môn học", ""));
        
        topicFilterCombo = new JComboBox<>();
        topicFilterCombo.addItem(new Topic(-1, "Tất cả chủ đề", 0));
        
        difficultyFilterCombo = new JComboBox<>();
        difficultyFilterCombo.addItem(new Difficulty(-1, "Tất cả độ khó"));
        
        // Load difficulties from database
        try {
            List<Difficulty> difficulties = difficultyDAO.getAllDifficulties();
            for (Difficulty difficulty : difficulties) {
                difficultyFilterCombo.addItem(difficulty);
            }
        } catch (Exception e) {
            System.err.println("Lỗi load difficulties: " + e.getMessage());
            // Fallback nếu có lỗi
            difficultyFilterCombo.addItem(new Difficulty(0, "Easy"));
            difficultyFilterCombo.addItem(new Difficulty(1, "Medium"));
            difficultyFilterCombo.addItem(new Difficulty(2, "Hard"));
        }
        
        // Load subjects
        List<Subject> subjects = subjectDAO.getAllSubjects();
        for (Subject subject : subjects) {
            subjectFilterCombo.addItem(subject);
        }
        
        // Buttons
        addButton = new JButton("Thêm mới", com.quiz.ui.IconUtil.load("/images/add.png", 16, 16));
        editButton = new JButton("Sửa", com.quiz.ui.IconUtil.load("/images/edit.png", 16, 16));
        deleteButton = new JButton("Xóa", com.quiz.ui.IconUtil.load("/images/delete.png", 16, 16));
        refreshButton = new JButton("Làm mới", com.quiz.ui.IconUtil.load("/images/refresh.png", 16, 16));
        
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 12);
        addButton.setFont(buttonFont);
        editButton.setFont(buttonFont);
        deleteButton.setFont(buttonFont);
        refreshButton.setFont(buttonFont);
        
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel - Search and filters
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(searchField);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(new JLabel("Môn học:"));
        topPanel.add(subjectFilterCombo);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(new JLabel("Chủ đề:"));
        topPanel.add(topicFilterCombo);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(new JLabel("Độ khó:"));
        topPanel.add(difficultyFilterCombo);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // North panel
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Center panel - Table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(questionTable), BorderLayout.CENTER);
        
        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        addButton.addActionListener(e -> showAddQuestionDialog());
        editButton.addActionListener(e -> showEditQuestionDialog());
        deleteButton.addActionListener(e -> deleteSelectedQuestion());
        refreshButton.addActionListener(e -> loadQuestions());
        
        // Subject filter change
        subjectFilterCombo.addActionListener(e -> {
            Subject selectedSubject = (Subject) subjectFilterCombo.getSelectedItem();
            updateTopicFilter(selectedSubject != null ? selectedSubject.getId() : -1);
            filterQuestions();
        });
        
        // Topic filter change
        topicFilterCombo.addActionListener(e -> filterQuestions());
        
        // Difficulty filter change
        difficultyFilterCombo.addActionListener(e -> filterQuestions());
        
        // Double click to edit
        questionTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showEditQuestionDialog();
                }
            }
        });
    }

    private void updateTopicFilter(int subjectId) {
        topicFilterCombo.removeAllItems();
        topicFilterCombo.addItem(new Topic(-1, "Tất cả chủ đề", 0));
        
        if (subjectId > 0) {
            List<Topic> topics = topicDAO.getTopicsBySubject(subjectId);
            for (Topic topic : topics) {
                topicFilterCombo.addItem(topic);
            }
        }
    }

    private void loadQuestions() {
        questions = questionDAO.getAllQuestions();
        updateTable();
    }

    private void filterQuestions() {
        String searchText = searchField.getText().toLowerCase();
        Subject selectedSubject = (Subject) subjectFilterCombo.getSelectedItem();
        Topic selectedTopic = (Topic) topicFilterCombo.getSelectedItem();
        Difficulty selectedDifficulty = (Difficulty) difficultyFilterCombo.getSelectedItem();
        
        List<Question> filteredQuestions = new ArrayList<>();
        
        for (Question question : questions) {
            boolean matches = true;
            
            // Search filter
            if (!searchText.isEmpty()) {
                matches = matches && question.getContent().toLowerCase().contains(searchText);
            }
            
            // Subject filter
            if (selectedSubject != null && selectedSubject.getId() > 0) {
                matches = matches && question.getSubject() != null && 
                         question.getSubject().getId() == selectedSubject.getId();
            }
            
            // Topic filter
            if (selectedTopic != null && selectedTopic.getId() > 0) {
                matches = matches && question.getTopic() != null && 
                         question.getTopic().getId() == selectedTopic.getId();
            }
            
            // Difficulty filter
            if (selectedDifficulty != null && selectedDifficulty.getId() > 0) {
                matches = matches && question.getDifficulty() != null && 
                         question.getDifficulty().getId() == selectedDifficulty.getId();
            }
            
            if (matches) {
                filteredQuestions.add(question);
            }
        }
        
        updateTable(filteredQuestions);
    }

    private void updateTable() {
        updateTable(questions);
    }
    
    private void updateTable(List<Question> questionsToShow) {
        tableModel.setRowCount(0);
        for (Question question : questionsToShow) {
            Object[] row = {
                question.getId(),
                question.getContent().length() > 50 ? question.getContent().substring(0, 50) + "..." : question.getContent(),
                question.getSubject() != null ? question.getSubject().getName() : "N/A",
                question.getTopic() != null ? question.getTopic().getName() : "N/A",
                question.getDifficulty() != null ? question.getDifficulty().getLevel() : "N/A",
                question.getCreatedByUser() != null ? question.getCreatedByUser().getUsername() : "N/A",
                question.getCreatedAt().toString().substring(0, 19)
            };
            tableModel.addRow(row);
        }
    }

    private void showAddQuestionDialog() {
        QuestionDialog dialog = new QuestionDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm câu hỏi mới", null);
        dialog.setVisible(true);
        if (dialog.isQuestionAdded()) {
            loadQuestions();
        }
    }

    private void showEditQuestionDialog() {
        int selectedRow = questionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn câu hỏi cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Question selectedQuestion = questions.get(selectedRow);
        QuestionDialog dialog = new QuestionDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Sửa câu hỏi", selectedQuestion);
        dialog.setVisible(true);
        if (dialog.isQuestionAdded()) {
            loadQuestions();
        }
    }

    private void deleteSelectedQuestion() {
        int selectedRow = questionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn câu hỏi cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Question selectedQuestion = questions.get(selectedRow);
        
        // Kiểm tra xem câu hỏi có đang được sử dụng trong đề thi nào không
        if (questionDAO.isQuestionUsedInExams(selectedQuestion.getId())) {
            List<String> examNames = questionDAO.getExamsUsingQuestion(selectedQuestion.getId());
            String examList = String.join(", ", examNames);
            
            int option = JOptionPane.showConfirmDialog(this, 
                "Câu hỏi này đang được sử dụng trong các đề thi sau:\n" + examList + 
                "\n\nViệc xóa câu hỏi sẽ tự động loại bỏ nó khỏi tất cả các đề thi.\n" +
                "Bạn có chắc chắn muốn tiếp tục?", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (option != JOptionPane.YES_OPTION) {
                return;
            }
        } else {
            int option = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa câu hỏi này?", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            
            if (option != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        // Thực hiện xóa
        if (questionDAO.deleteQuestion(selectedQuestion.getId())) {
            JOptionPane.showMessageDialog(this, "Xóa câu hỏi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadQuestions();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa câu hỏi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Inner class for Question Dialog
    private class QuestionDialog extends JDialog {
        private JTextArea contentArea;
        private JTextField optionAField, optionBField, optionCField, optionDField;
        private JComboBox<Subject> subjectCombo;
        private JComboBox<Topic> topicCombo;
        private JComboBox<Difficulty> difficultyCombo;
        private JCheckBox correctA, correctB, correctC, correctD;
        private JButton saveButton, cancelButton;
        private boolean questionAdded = false;
        private Question question;

        public QuestionDialog(JFrame parent, String title, Question question) {
            super(parent, title, true);
            this.question = question;
            initializeComponents();
            setupLayout();
            setupEventHandlers();
            setupDialog();
        }

        private void initializeComponents() {
            contentArea = new JTextArea(4, 50);
            contentArea.setLineWrap(true);
            contentArea.setWrapStyleWord(true);
            
            optionAField = new JTextField(50);
            optionBField = new JTextField(50);
            optionCField = new JTextField(50);
            optionDField = new JTextField(50);
            
            // Subject combo
            subjectCombo = new JComboBox<>();
            subjectCombo.addItem(new Subject(0, "-- Chọn môn học --", ""));
            List<Subject> subjects = subjectDAO.getAllSubjects();
            for (Subject subject : subjects) {
                subjectCombo.addItem(subject);
            }
            
            // Topic combo
            topicCombo = new JComboBox<>();
            topicCombo.addItem(new Topic(0, "-- Chọn chủ đề --", 0));
            
            // Difficulty combo
            difficultyCombo = new JComboBox<>();
            difficultyCombo.addItem(new Difficulty(0, "-- Chọn độ khó --"));
            
            // Load difficulties from database
            try {
                List<Difficulty> difficulties = difficultyDAO.getAllDifficulties();
                for (Difficulty difficulty : difficulties) {
                    difficultyCombo.addItem(difficulty);
                }
            } catch (Exception e) {
                System.err.println("Lỗi load difficulties in dialog: " + e.getMessage());
                // Fallback nếu có lỗi
                difficultyCombo.addItem(new Difficulty(1, "Easy"));
                difficultyCombo.addItem(new Difficulty(2, "Medium"));
                difficultyCombo.addItem(new Difficulty(3, "Hard"));
            }
            
            // Correct answer checkboxes
            correctA = new JCheckBox("A");
            correctB = new JCheckBox("B");
            correctC = new JCheckBox("C");
            correctD = new JCheckBox("D");
            
            saveButton = new JButton("Lưu");
            cancelButton = new JButton("Hủy");
            
            Font font = new Font("Segoe UI", Font.PLAIN, 12);
            contentArea.setFont(font);
            optionAField.setFont(font);
            optionBField.setFont(font);
            optionCField.setFont(font);
            optionDField.setFont(font);
            subjectCombo.setFont(font);
            topicCombo.setFont(font);
            difficultyCombo.setFont(font);
            correctA.setFont(font);
            correctB.setFont(font);
            correctC.setFont(font);
            correctD.setFont(font);
            saveButton.setFont(font);
            cancelButton.setFont(font);
            
            // Nếu là edit mode, điền thông tin
            if (question != null) {
                contentArea.setText(question.getContent());
                optionAField.setText(question.getOptionA());
                optionBField.setText(question.getOptionB());
                optionCField.setText(question.getOptionC());
                optionDField.setText(question.getOptionD());
                
                // Set subject
                for (int i = 0; i < subjectCombo.getItemCount(); i++) {
                    Subject subject = (Subject) subjectCombo.getItemAt(i);
                    if (subject.getId() == question.getSubjectId()) {
                        subjectCombo.setSelectedIndex(i);
                        break;
                    }
                }
                
                // Set difficulty
                for (int i = 0; i < difficultyCombo.getItemCount(); i++) {
                    Difficulty difficulty = (Difficulty) difficultyCombo.getItemAt(i);
                    if (difficulty.getId() == question.getDifficultyId()) {
                        difficultyCombo.setSelectedIndex(i);
                        break;
                    }
                }
                
                // Set correct answers
                if (question.getCorrectAnswers() != null) {
                    for (String answer : question.getCorrectAnswers()) {
                        switch (answer) {
                            case "A": correctA.setSelected(true); break;
                            case "B": correctB.setSelected(true); break;
                            case "C": correctC.setSelected(true); break;
                            case "D": correctD.setSelected(true); break;
                        }
                    }
                }
            }
        }

        private void setupLayout() {
            setLayout(new BorderLayout());
            
            JPanel mainPanel = new JPanel(new GridBagLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Content
            gbc.gridx = 0; gbc.gridy = 0;
            mainPanel.add(new JLabel("Nội dung câu hỏi:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
            mainPanel.add(new JScrollPane(contentArea), gbc);
            
            // Options
            gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
            mainPanel.add(new JLabel("Đáp án A:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            mainPanel.add(optionAField, gbc);
            gbc.gridx = 2;
            mainPanel.add(correctA, gbc);
            
            gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
            mainPanel.add(new JLabel("Đáp án B:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            mainPanel.add(optionBField, gbc);
            gbc.gridx = 2;
            mainPanel.add(correctB, gbc);
            
            gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
            mainPanel.add(new JLabel("Đáp án C:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            mainPanel.add(optionCField, gbc);
            gbc.gridx = 2;
            mainPanel.add(correctC, gbc);
            
            gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
            mainPanel.add(new JLabel("Đáp án D:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            mainPanel.add(optionDField, gbc);
            gbc.gridx = 2;
            mainPanel.add(correctD, gbc);
            
            // Subject and Topic
            gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
            mainPanel.add(new JLabel("Môn học:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            mainPanel.add(subjectCombo, gbc);
            
            gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE;
            mainPanel.add(new JLabel("Chủ đề:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            mainPanel.add(topicCombo, gbc);
            
            // Difficulty
            gbc.gridx = 0; gbc.gridy = 7; gbc.fill = GridBagConstraints.NONE;
            mainPanel.add(new JLabel("Độ khó:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            mainPanel.add(difficultyCombo, gbc);
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            add(mainPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void setupEventHandlers() {
            saveButton.addActionListener(e -> saveQuestion());
            cancelButton.addActionListener(e -> dispose());
            
            // Subject change
            subjectCombo.addActionListener(e -> {
                Subject selectedSubject = (Subject) subjectCombo.getSelectedItem();
                if (selectedSubject != null && selectedSubject.getId() > 0) {
                    updateTopicCombo(selectedSubject.getId());
                } else {
                    updateTopicCombo(0);
                }
            });
        }

        private void updateTopicCombo(int subjectId) {
            topicCombo.removeAllItems();
            topicCombo.addItem(new Topic(0, "-- Chọn chủ đề --", 0));
            if (subjectId > 0) {
                List<Topic> topics = topicDAO.getTopicsBySubject(subjectId);
                for (Topic topic : topics) {
                    topicCombo.addItem(topic);
                }
            }
        }

        private void setupDialog() {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setResizable(false);
            pack();
            setLocationRelativeTo(getParent());
            
            // Load existing question data if editing
            if (question != null) {
                loadQuestionData();
            }
        }
        
        private void loadQuestionData() {
            if (question == null) return;
            
            // Load basic data
            contentArea.setText(question.getContent());
            optionAField.setText(question.getOptionA());
            optionBField.setText(question.getOptionB());
            optionCField.setText(question.getOptionC());
            optionDField.setText(question.getOptionD());
            
            // Load subject
            if (question.getSubject() != null) {
                for (int i = 0; i < subjectCombo.getItemCount(); i++) {
                    Subject subject = subjectCombo.getItemAt(i);
                    if (subject.getId() == question.getSubjectId()) {
                        subjectCombo.setSelectedItem(subject);
                        break;
                    }
                }
            }
            
            // Load topic (after subject is selected)
            updateTopicCombo(question.getSubjectId());
            if (question.getTopic() != null) {
                for (int i = 0; i < topicCombo.getItemCount(); i++) {
                    Topic topic = topicCombo.getItemAt(i);
                    if (topic.getId() == question.getTopicId()) {
                        topicCombo.setSelectedItem(topic);
                        break;
                    }
                }
            }
            
            // Load difficulty
            if (question.getDifficulty() != null) {
                for (int i = 0; i < difficultyCombo.getItemCount(); i++) {
                    Difficulty difficulty = difficultyCombo.getItemAt(i);
                    if (difficulty.getId() == question.getDifficultyId()) {
                        difficultyCombo.setSelectedItem(difficulty);
                        break;
                    }
                }
            }
            
            // Load correct answers
            if (question.getCorrectAnswers() != null) {
                correctA.setSelected(question.getCorrectAnswers().contains("A"));
                correctB.setSelected(question.getCorrectAnswers().contains("B"));
                correctC.setSelected(question.getCorrectAnswers().contains("C"));
                correctD.setSelected(question.getCorrectAnswers().contains("D"));
            }
        }

        private void saveQuestion() {
            String content = contentArea.getText().trim();
            String optionA = optionAField.getText().trim();
            String optionB = optionBField.getText().trim();
            String optionC = optionCField.getText().trim();
            String optionD = optionDField.getText().trim();
            
            Subject selectedSubject = (Subject) subjectCombo.getSelectedItem();
            Topic selectedTopic = (Topic) topicCombo.getSelectedItem();
            Difficulty selectedDifficulty = (Difficulty) difficultyCombo.getSelectedItem();
            
            if (content.isEmpty() || optionA.isEmpty() || optionB.isEmpty() || 
                optionC.isEmpty() || optionD.isEmpty() || selectedSubject == null || 
                selectedTopic == null || selectedDifficulty == null ||
                selectedSubject.getId() == 0 || selectedTopic.getId() == 0 || selectedDifficulty.getId() == 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin và chọn môn học, chủ đề, độ khó!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check at least one correct answer
            if (!correctA.isSelected() && !correctB.isSelected() && 
                !correctC.isSelected() && !correctD.isSelected()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một đáp án đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get correct answers
            List<String> correctAnswers = new ArrayList<>();
            if (correctA.isSelected()) correctAnswers.add("A");
            if (correctB.isSelected()) correctAnswers.add("B");
            if (correctC.isSelected()) correctAnswers.add("C");
            if (correctD.isSelected()) correctAnswers.add("D");
            
            boolean success;
            if (question == null) {
                // Add new question
                int createdBy = (currentUser != null) ? currentUser.getId() : 1; // Fallback to admin if no current user
                
                // Debug log
                System.out.println("DEBUG - Current User: " + (currentUser != null ? currentUser.getUsername() : "null"));
                System.out.println("DEBUG - CreatedBy ID: " + createdBy);
                
                Question newQuestion = new Question(content, optionA, optionB, optionC, optionD,
                    selectedTopic.getId(), selectedDifficulty.getId(), selectedSubject.getId(), createdBy);
                success = questionDAO.addQuestion(newQuestion, correctAnswers);
            } else {
                // Update existing question
                question.setContent(content);
                question.setOptionA(optionA);
                question.setOptionB(optionB);
                question.setOptionC(optionC);
                question.setOptionD(optionD);
                question.setTopicId(selectedTopic.getId());
                question.setDifficultyId(selectedDifficulty.getId());
                question.setSubjectId(selectedSubject.getId());
                
                // Debug log
                System.out.println("DEBUG - Update Question - ID: " + question.getId());
                System.out.println("DEBUG - Update Question - Difficulty ID: " + selectedDifficulty.getId() + " (" + selectedDifficulty.getLevel() + ")");
                System.out.println("DEBUG - Update Question - Topic ID: " + selectedTopic.getId());
                System.out.println("DEBUG - Update Question - Subject ID: " + selectedSubject.getId());
                
                success = questionDAO.updateQuestion(question, correctAnswers);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Lưu câu hỏi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                questionAdded = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu câu hỏi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }

        public boolean isQuestionAdded() {
            return questionAdded;
        }
    }
}
