package com.quiz.ui.panels;

import com.quiz.dao.ExamDAO;
import com.quiz.dao.QuestionDAO;
import com.quiz.dao.SubjectDAO;
import com.quiz.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel quản lý đề thi
 */
public class ExamManagementPanel extends JPanel {
    private JTable examTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<Subject> subjectFilterCombo;
    private JButton addButton, editButton, deleteButton, refreshButton, viewButton;
    private ExamDAO examDAO;
    private SubjectDAO subjectDAO;
    private QuestionDAO questionDAO;
    private List<Exam> exams;
    private User currentUser;

    public ExamManagementPanel() {
        this(null);
    }

    public ExamManagementPanel(User currentUser) {
        this.currentUser = currentUser;
        examDAO = new ExamDAO();
        subjectDAO = new SubjectDAO();
        questionDAO = new QuestionDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadExams();
    }

    private void initializeComponents() {
        // Table
        String[] columnNames = {"ID", "Tiêu đề", "Môn học", "Thời gian (phút)", "Số câu hỏi", "Người tạo", "Ngày tạo"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        examTable = new JTable(tableModel);
        examTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        examTable.setRowHeight(25);
        examTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Subject filter combo
        subjectFilterCombo = new JComboBox<>();
        subjectFilterCombo.addItem(new Subject(0, "Tất cả môn học", ""));
        List<Subject> subjects = subjectDAO.getAllSubjects();
        for (Subject subject : subjects) {
            subjectFilterCombo.addItem(subject);
        }
        
        // Buttons
        addButton = new JButton("Thêm mới");
        editButton = new JButton("Sửa");
        deleteButton = new JButton("Xóa");
        viewButton = new JButton("Xem chi tiết");
        refreshButton = new JButton("Làm mới");
        
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 12);
        addButton.setFont(buttonFont);
        editButton.setFont(buttonFont);
        deleteButton.setFont(buttonFont);
        viewButton.setFont(buttonFont);
        refreshButton.setFont(buttonFont);
        
        // Thiết lập màu sắc cho buttons
        addButton.setBackground(new Color(40, 167, 69));
        addButton.setForeground(Color.WHITE);
        editButton.setBackground(new Color(255, 193, 7));
        editButton.setForeground(Color.BLACK);
        deleteButton.setBackground(new Color(220, 53, 69));
        deleteButton.setForeground(Color.WHITE);
        viewButton.setBackground(new Color(0, 123, 255));
        viewButton.setForeground(Color.WHITE);
        refreshButton.setBackground(new Color(108, 117, 125));
        refreshButton.setForeground(Color.WHITE);
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
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(refreshButton);
        
        // North panel
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Center panel - Table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(examTable), BorderLayout.CENTER);
        
        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        addButton.addActionListener(e -> showAddExamDialog());
        editButton.addActionListener(e -> showEditExamDialog());
        deleteButton.addActionListener(e -> deleteSelectedExam());
        viewButton.addActionListener(e -> viewExamDetails());
        refreshButton.addActionListener(e -> loadExams());
        
        // Subject filter change
        subjectFilterCombo.addActionListener(e -> filterExams());
        
        // Double click to view details
        examTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewExamDetails();
                }
            }
        });
    }

    private void loadExams() {
        exams = examDAO.getAllExams();
        updateTable();
    }

    private void filterExams() {
        // Implement filtering logic here
        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (Exam exam : exams) {
            int questionCount = exam.getQuestions() != null ? exam.getQuestions().size() : 0;
            Object[] row = {
                exam.getId(),
                exam.getTitle(),
                exam.getSubject() != null ? exam.getSubject().getName() : "N/A",
                exam.getDuration(),
                questionCount,
                exam.getCreatedByUser() != null ? exam.getCreatedByUser().getUsername() : "N/A",
                exam.getCreatedAt().toString().substring(0, 19)
            };
            tableModel.addRow(row);
        }
    }

    private void showAddExamDialog() {
        ExamDialog dialog = new ExamDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm đề thi mới", null);
        dialog.setVisible(true);
        if (dialog.isExamAdded()) {
            loadExams();
        }
    }

    private void showEditExamDialog() {
        int selectedRow = examTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đề thi cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Exam selectedExam = exams.get(selectedRow);
        ExamDialog dialog = new ExamDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Sửa đề thi", selectedExam);
        dialog.setVisible(true);
        if (dialog.isExamAdded()) {
            loadExams();
        }
    }

    private void deleteSelectedExam() {
        int selectedRow = examTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đề thi cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Exam selectedExam = exams.get(selectedRow);
        int option = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa đề thi '" + selectedExam.getTitle() + "'?", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            if (examDAO.deleteExam(selectedExam.getId())) {
                JOptionPane.showMessageDialog(this, "Xóa đề thi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadExams();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa đề thi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewExamDetails() {
        int selectedRow = examTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đề thi cần xem!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Exam selectedExam = exams.get(selectedRow);
        Exam examWithQuestions = examDAO.getExamById(selectedExam.getId());
        
        ExamDetailsDialog dialog = new ExamDetailsDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Chi tiết đề thi", examWithQuestions);
        dialog.setVisible(true);
    }

    // Inner class for Exam Dialog
    private class ExamDialog extends JDialog {
        private JTextField titleField;
        private JSpinner durationSpinner;
        private JComboBox<Subject> subjectCombo;
        private JList<Question> questionList;
        private DefaultListModel<Question> questionListModel;
        private JButton saveButton, cancelButton;
        private boolean examAdded = false;
        private Exam exam;

        public ExamDialog(JFrame parent, String title, Exam exam) {
            super(parent, title, true);
            this.exam = exam;
            initializeComponents();
            setupLayout();
            setupEventHandlers();
            setupDialog();
        }

        private void initializeComponents() {
            titleField = new JTextField(30);
            
            durationSpinner = new JSpinner(new SpinnerNumberModel(60, 1, 300, 1));
            
            // Subject combo
            subjectCombo = new JComboBox<>();
            List<Subject> subjects = subjectDAO.getAllSubjects();
            for (Subject subject : subjects) {
                subjectCombo.addItem(subject);
            }
            
            // Question list
            questionListModel = new DefaultListModel<>();
            questionList = new JList<>(questionListModel);
            questionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            questionList.setCellRenderer(new QuestionListCellRenderer());
            
            saveButton = new JButton("Lưu");
            cancelButton = new JButton("Hủy");
            
            Font font = new Font("Segoe UI", Font.PLAIN, 12);
            titleField.setFont(font);
            durationSpinner.setFont(font);
            subjectCombo.setFont(font);
            questionList.setFont(font);
            saveButton.setFont(font);
            cancelButton.setFont(font);
            
            // Thiết lập màu sắc
            saveButton.setBackground(new Color(40, 167, 69));
            saveButton.setForeground(Color.WHITE);
            cancelButton.setBackground(new Color(108, 117, 125));
            cancelButton.setForeground(Color.WHITE);
            
            // Load questions
            loadQuestions();
            
            // Nếu là edit mode, điền thông tin
            if (exam != null) {
                titleField.setText(exam.getTitle());
                durationSpinner.setValue(exam.getDuration());
                
                // Set subject
                for (int i = 0; i < subjectCombo.getItemCount(); i++) {
                    Subject subject = (Subject) subjectCombo.getItemAt(i);
                    if (subject.getId() == exam.getSubjectId()) {
                        subjectCombo.setSelectedIndex(i);
                        break;
                    }
                }
                
                // Set selected questions
                if (exam.getQuestions() != null) {
                    for (Question question : exam.getQuestions()) {
                        for (int i = 0; i < questionListModel.getSize(); i++) {
                            Question listQuestion = questionListModel.getElementAt(i);
                            if (listQuestion.getId() == question.getId()) {
                                questionList.addSelectionInterval(i, i);
                                break;
                            }
                        }
                    }
                }
            }
        }

        private void loadQuestions() {
            List<Question> questions = questionDAO.getAllQuestions();
            for (Question question : questions) {
                questionListModel.addElement(question);
            }
        }

        private void setupLayout() {
            setLayout(new BorderLayout());
            
            JPanel mainPanel = new JPanel(new GridBagLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Title
            gbc.gridx = 0; gbc.gridy = 0;
            mainPanel.add(new JLabel("Tiêu đề:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            mainPanel.add(titleField, gbc);
            
            // Duration
            gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
            mainPanel.add(new JLabel("Thời gian (phút):"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            mainPanel.add(durationSpinner, gbc);
            
            // Subject
            gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
            mainPanel.add(new JLabel("Môn học:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            mainPanel.add(subjectCombo, gbc);
            
            // Questions
            gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            mainPanel.add(new JLabel("Chọn câu hỏi:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0; gbc.weighty = 1.0;
            mainPanel.add(new JScrollPane(questionList), gbc);
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            add(mainPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void setupEventHandlers() {
            saveButton.addActionListener(e -> saveExam());
            cancelButton.addActionListener(e -> dispose());
        }

        private void setupDialog() {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setResizable(true);
            setSize(600, 500);
            setLocationRelativeTo(getParent());
        }

        private void saveExam() {
            String title = titleField.getText().trim();
            int duration = (Integer) durationSpinner.getValue();
            Subject selectedSubject = (Subject) subjectCombo.getSelectedItem();
            List<Question> selectedQuestions = questionList.getSelectedValuesList();
            
            if (title.isEmpty() || selectedSubject == null || selectedQuestions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin và chọn ít nhất một câu hỏi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            List<Integer> questionIds = new ArrayList<>();
            for (Question question : selectedQuestions) {
                questionIds.add(question.getId());
            }
            
            boolean success;
            if (exam == null) {
                // Add new exam
                int createdBy = (currentUser != null) ? currentUser.getId() : 1; // Fallback to admin if no current user
                // Debug log
                System.out.println("DEBUG - ExamManagementPanel.saveExam() - Current User: " + (currentUser != null ? currentUser.getUsername() : "null"));
                System.out.println("DEBUG - ExamManagementPanel.saveExam() - CreatedBy ID: " + createdBy);
                Exam newExam = new Exam(title, duration, selectedSubject.getId(), createdBy);
                success = examDAO.addExam(newExam, questionIds);
            } else {
                // Update existing exam
                exam.setTitle(title);
                exam.setDuration(duration);
                exam.setSubjectId(selectedSubject.getId());
                success = examDAO.updateExam(exam, questionIds);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Lưu đề thi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                examAdded = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu đề thi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }

        public boolean isExamAdded() {
            return examAdded;
        }
    }

    // Custom cell renderer for question list
    private class QuestionListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Question) {
                Question question = (Question) value;
                String text = String.format("[%d] %s (%s - %s)", 
                    question.getId(),
                    question.getContent().length() > 50 ? question.getContent().substring(0, 50) + "..." : question.getContent(),
                    question.getSubject() != null ? question.getSubject().getName() : "N/A",
                    question.getDifficulty() != null ? question.getDifficulty().getLevel() : "N/A"
                );
                setText(text);
            }
            
            return this;
        }
    }

    // Inner class for Exam Details Dialog
    private class ExamDetailsDialog extends JDialog {
        private Exam exam;

        public ExamDetailsDialog(JFrame parent, String title, Exam exam) {
            super(parent, title, true);
            this.exam = exam;
            initializeComponents();
            setupLayout();
            setupDialog();
        }

        private void initializeComponents() {
            // Components will be created in setupLayout
        }

        private void setupLayout() {
            setLayout(new BorderLayout());
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Exam info
            JPanel infoPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            gbc.gridx = 0; gbc.gridy = 0;
            infoPanel.add(new JLabel("Tiêu đề:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(exam.getTitle()), gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            infoPanel.add(new JLabel("Môn học:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(exam.getSubject() != null ? exam.getSubject().getName() : "N/A"), gbc);
            
            gbc.gridx = 0; gbc.gridy = 2;
            infoPanel.add(new JLabel("Thời gian:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(exam.getDuration() + " phút"), gbc);
            
            gbc.gridx = 0; gbc.gridy = 3;
            infoPanel.add(new JLabel("Số câu hỏi:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(String.valueOf(exam.getQuestions() != null ? exam.getQuestions().size() : 0)), gbc);
            
            // Questions list
            JList<Question> questionsList = new JList<>();
            DefaultListModel<Question> listModel = new DefaultListModel<>();
            if (exam.getQuestions() != null) {
                for (Question question : exam.getQuestions()) {
                    listModel.addElement(question);
                }
            }
            questionsList.setModel(listModel);
            questionsList.setCellRenderer(new QuestionListCellRenderer());
            
            JScrollPane scrollPane = new JScrollPane(questionsList);
            scrollPane.setPreferredSize(new Dimension(600, 300));
            
            mainPanel.add(infoPanel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            
            add(mainPanel, BorderLayout.CENTER);
        }

        private void setupDialog() {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setResizable(false);
            pack();
            setLocationRelativeTo(getParent());
        }
    }
}
