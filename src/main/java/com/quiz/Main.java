package com.quiz;

import com.formdev.flatlaf.FlatLightLaf;
import com.quiz.ui.LoginFrame;

import javax.swing.*;

/**
 * Main class để khởi chạy ứng dụng Quiz Management System
 */
public class Main {
    public static void main(String[] args) {
        // Thiết lập Look and Feel hiện đại
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Khởi chạy giao diện đăng nhập
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
