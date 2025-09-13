package com.quiz.ui.panels;

import com.quiz.dao.SubjectDAO;
import com.quiz.model.Subject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel quản lý môn học
 */
public class SubjectManagementPanel extends JPanel {
    private JTable subjectTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton;
    private SubjectDAO subjectDAO;
    private List<Subject> subjects;

    public SubjectManagementPanel() {
        subjectDAO = new SubjectDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadSubjects();
    }

    private void initializeComponents() {
        // Table
        String[] columnNames = {"ID", "Tên môn học", "Mô tả"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        subjectTable = new JTable(tableModel);
        subjectTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subjectTable.setRowHeight(25);
        subjectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Buttons
        addButton = new JButton("Thêm mới");
        editButton = new JButton("Sửa");
        deleteButton = new JButton("Xóa");
        refreshButton = new JButton("Làm mới");
        
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 12);
        addButton.setFont(buttonFont);
        editButton.setFont(buttonFont);
        deleteButton.setFont(buttonFont);
        refreshButton.setFont(buttonFont);
        
        // Thiết lập màu sắc cho buttons
        addButton.setBackground(new Color(40, 167, 69));
        addButton.setForeground(Color.WHITE);
        editButton.setBackground(new Color(255, 193, 7));
        editButton.setForeground(Color.BLACK);
        deleteButton.setBackground(new Color(220, 53, 69));
        deleteButton.setForeground(Color.WHITE);
        refreshButton.setBackground(new Color(108, 117, 125));
        refreshButton.setForeground(Color.WHITE);
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
        centerPanel.add(new JScrollPane(subjectTable), BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        addButton.addActionListener(e -> showAddSubjectDialog());
        editButton.addActionListener(e -> showEditSubjectDialog());
        deleteButton.addActionListener(e -> deleteSelectedSubject());
        refreshButton.addActionListener(e -> loadSubjects());
        
        // Double click to edit
        subjectTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showEditSubjectDialog();
                }
            }
        });
    }

    private void loadSubjects() {
        subjects = subjectDAO.getAllSubjects();
        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (Subject subject : subjects) {
            Object[] row = {
                subject.getId(),
                subject.getName(),
                subject.getDescription()
            };
            tableModel.addRow(row);
        }
    }

    private void showAddSubjectDialog() {
        SubjectDialog dialog = new SubjectDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm môn học mới", null);
        dialog.setVisible(true);
        if (dialog.isSubjectAdded()) {
            loadSubjects();
        }
    }

    private void showEditSubjectDialog() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn môn học cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Subject selectedSubject = subjects.get(selectedRow);
        SubjectDialog dialog = new SubjectDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Sửa thông tin môn học", selectedSubject);
        dialog.setVisible(true);
        if (dialog.isSubjectAdded()) {
            loadSubjects();
        }
    }

    private void deleteSelectedSubject() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn môn học cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Subject selectedSubject = subjects.get(selectedRow);
        int option = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa môn học '" + selectedSubject.getName() + "'?", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            if (subjectDAO.deleteSubject(selectedSubject.getId())) {
                JOptionPane.showMessageDialog(this, "Xóa môn học thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadSubjects();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa môn học!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Inner class for Subject Dialog
    private class SubjectDialog extends JDialog {
        private JTextField nameField;
        private JTextArea descriptionArea;
        private JButton saveButton, cancelButton;
        private boolean subjectAdded = false;
        private Subject subject;

        public SubjectDialog(JFrame parent, String title, Subject subject) {
            super(parent, title, true);
            this.subject = subject;
            initializeComponents();
            setupLayout();
            setupEventHandlers();
            setupDialog();
        }

        private void initializeComponents() {
            nameField = new JTextField(30);
            descriptionArea = new JTextArea(5, 30);
            descriptionArea.setLineWrap(true);
            descriptionArea.setWrapStyleWord(true);
            
            saveButton = new JButton("Lưu");
            cancelButton = new JButton("Hủy");
            
            Font font = new Font("Segoe UI", Font.PLAIN, 12);
            nameField.setFont(font);
            descriptionArea.setFont(font);
            saveButton.setFont(font);
            cancelButton.setFont(font);
            
            // Thiết lập màu sắc
            saveButton.setBackground(new Color(40, 167, 69));
            saveButton.setForeground(Color.WHITE);
            cancelButton.setBackground(new Color(108, 117, 125));
            cancelButton.setForeground(Color.WHITE);
            
            // Nếu là edit mode, điền thông tin
            if (subject != null) {
                nameField.setText(subject.getName());
                descriptionArea.setText(subject.getDescription());
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
            mainPanel.add(new JLabel("Tên môn học:"), gbc);
            gbc.gridx = 1;
            mainPanel.add(nameField, gbc);
            
            // Description
            gbc.gridx = 0; gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            mainPanel.add(new JLabel("Mô tả:"), gbc);
            gbc.gridx = 1;
            mainPanel.add(new JScrollPane(descriptionArea), gbc);
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            add(mainPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void setupEventHandlers() {
            saveButton.addActionListener(e -> saveSubject());
            cancelButton.addActionListener(e -> dispose());
        }

        private void setupDialog() {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setResizable(false);
            pack();
            setLocationRelativeTo(getParent());
        }

        private void saveSubject() {
            String name = nameField.getText().trim();
            String description = descriptionArea.getText().trim();
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên môn học!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean success;
            if (subject == null) {
                // Add new subject
                Subject newSubject = new Subject(name, description);
                success = subjectDAO.addSubject(newSubject);
            } else {
                // Update existing subject
                subject.setName(name);
                subject.setDescription(description);
                success = subjectDAO.updateSubject(subject);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Lưu thông tin thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                subjectAdded = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }

        public boolean isSubjectAdded() {
            return subjectAdded;
        }
    }
}
