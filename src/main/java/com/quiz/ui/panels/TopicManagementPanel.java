package com.quiz.ui.panels;

import com.quiz.dao.SubjectDAO;
import com.quiz.dao.TopicDAO;
import com.quiz.model.Subject;
import com.quiz.model.Topic;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel quản lý chủ đề
 */
public class TopicManagementPanel extends JPanel {
    private JTable topicTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton;
    private TopicDAO topicDAO;
    private SubjectDAO subjectDAO;
    private List<Topic> topics;

    public TopicManagementPanel() {
        topicDAO = new TopicDAO();
        subjectDAO = new SubjectDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadTopics();
    }

    private void initializeComponents() {
        // Table
        String[] columnNames = {"ID", "Tên chủ đề", "Môn học"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        topicTable = new JTable(tableModel);
        topicTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        topicTable.setRowHeight(25);
        topicTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
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
        
        // Top panel - Search and buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(searchField);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        topPanel.add(refreshButton);
        
        // Center panel - Table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(topicTable), BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        addButton.addActionListener(e -> showAddTopicDialog());
        editButton.addActionListener(e -> showEditTopicDialog());
        deleteButton.addActionListener(e -> deleteSelectedTopic());
        refreshButton.addActionListener(e -> loadTopics());
        
        // Double click to edit
        topicTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showEditTopicDialog();
                }
            }
        });
    }

    private void loadTopics() {
        topics = topicDAO.getAllTopics();
        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (Topic topic : topics) {
            Object[] row = {
                topic.getId(),
                topic.getName(),
                topic.getSubject() != null ? topic.getSubject().getName() : "N/A"
            };
            tableModel.addRow(row);
        }
    }

    private void showAddTopicDialog() {
        TopicDialog dialog = new TopicDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm chủ đề mới", null);
        dialog.setVisible(true);
        if (dialog.isTopicAdded()) {
            loadTopics();
        }
    }

    private void showEditTopicDialog() {
        int selectedRow = topicTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chủ đề cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Topic selectedTopic = topics.get(selectedRow);
        TopicDialog dialog = new TopicDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Sửa thông tin chủ đề", selectedTopic);
        dialog.setVisible(true);
        if (dialog.isTopicAdded()) {
            loadTopics();
        }
    }

    private void deleteSelectedTopic() {
        int selectedRow = topicTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chủ đề cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Topic selectedTopic = topics.get(selectedRow);
        int option = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa chủ đề '" + selectedTopic.getName() + "'?", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            if (topicDAO.deleteTopic(selectedTopic.getId())) {
                JOptionPane.showMessageDialog(this, "Xóa chủ đề thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadTopics();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa chủ đề!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Inner class for Topic Dialog
    private class TopicDialog extends JDialog {
        private JTextField nameField;
        private JComboBox<Subject> subjectComboBox;
        private JButton saveButton, cancelButton;
        private boolean topicAdded = false;
        private Topic topic;

        public TopicDialog(JFrame parent, String title, Topic topic) {
            super(parent, title, true);
            this.topic = topic;
            initializeComponents();
            setupLayout();
            setupEventHandlers();
            setupDialog();
        }

        private void initializeComponents() {
            nameField = new JTextField(30);
            
            // Subject combo box
            subjectComboBox = new JComboBox<>();
            List<Subject> subjects = subjectDAO.getAllSubjects();
            for (Subject subject : subjects) {
                subjectComboBox.addItem(subject);
            }
            
            saveButton = new JButton("Lưu");
            cancelButton = new JButton("Hủy");
            
            Font font = new Font("Segoe UI", Font.PLAIN, 12);
            nameField.setFont(font);
            subjectComboBox.setFont(font);
            saveButton.setFont(font);
            cancelButton.setFont(font);
            
            
            // Nếu là edit mode, điền thông tin
            if (topic != null) {
                nameField.setText(topic.getName());
                
                // Set subject
                for (int i = 0; i < subjectComboBox.getItemCount(); i++) {
                    Subject subject = (Subject) subjectComboBox.getItemAt(i);
                    if (subject.getId() == topic.getSubjectId()) {
                        subjectComboBox.setSelectedIndex(i);
                        break;
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
            
            // Name
            gbc.gridx = 0; gbc.gridy = 0;
            mainPanel.add(new JLabel("Tên chủ đề:"), gbc);
            gbc.gridx = 1;
            mainPanel.add(nameField, gbc);
            
            // Subject
            gbc.gridx = 0; gbc.gridy = 1;
            mainPanel.add(new JLabel("Môn học:"), gbc);
            gbc.gridx = 1;
            mainPanel.add(subjectComboBox, gbc);
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            add(mainPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void setupEventHandlers() {
            saveButton.addActionListener(e -> saveTopic());
            cancelButton.addActionListener(e -> dispose());
        }

        private void setupDialog() {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setResizable(false);
            pack();
            setLocationRelativeTo(getParent());
        }

        private void saveTopic() {
            String name = nameField.getText().trim();
            Subject selectedSubject = (Subject) subjectComboBox.getSelectedItem();
            
            if (name.isEmpty() || selectedSubject == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean success;
            if (topic == null) {
                // Add new topic
                Topic newTopic = new Topic(name, selectedSubject.getId());
                success = topicDAO.addTopic(newTopic);
            } else {
                // Update existing topic
                topic.setName(name);
                topic.setSubjectId(selectedSubject.getId());
                success = topicDAO.updateTopic(topic);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Lưu thông tin thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                topicAdded = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }

        public boolean isTopicAdded() {
            return topicAdded;
        }
    }
}
