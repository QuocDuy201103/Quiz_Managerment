package com.quiz;

import com.formdev.flatlaf.FlatLightLaf;
import com.quiz.ui.RegisterFrame;

import javax.swing.*;

/**
 * Demo để test form đăng ký
 */
public class RegisterDemo {
    public static void main(String[] args) {
        // Thiết lập Look and Feel hiện đại
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Khởi chạy giao diện đăng ký
        SwingUtilities.invokeLater(() -> {
            new RegisterFrame().setVisible(true);
        });
    }
}
